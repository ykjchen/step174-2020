// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> requiredAttendees = request.getAttendees();

    // Get the relevant events (events with at least one required attendee).
    Collection<Event> relevantEvents = getRelevantEvents(events, requiredAttendees);

    // Get the TimeRanges of the relevant events in a List, ordered by start time.
    List<TimeRange> timeRanges = getEventTimeRanges(relevantEvents); 

    // Get a condensed TimeRanges list for the relevent events (condensed means overlapping events are turned into a single event).
    List<TimeRange> condensedTimeRanges = getCondensedTimeRanges(timeRanges);

    // Get the inverse time ranges in a day, given a collection of non-overlapping time ranges
    // sorted by their start time. Inverse means all the time ranges not covered by any time range
    // in the collection.
    List<TimeRange> openTimeRanges = getInverseTimeRanges(condensedTimeRanges);

    // Filter the open time ranges such that only open time ranges with a duration greater than the duration of the
    // meeting remain
    List<TimeRange> validTimeRanges = getValidTimeRanges(openTimeRanges, (int) request.getDuration());

    return validTimeRanges;
  }

  // Adds all relevant events (events with at least one required attendee) to a new collection.
  private Collection<Event> getRelevantEvents(Collection<Event> events, Collection<String> requiredAttendees) {
    Collection<Event> relevantEvents = new HashSet<>();

    for (Event event : events) {
      Set<String> eventAttendees = event.getAttendees();

      if (!Collections.disjoint(requiredAttendees, eventAttendees)) {
        relevantEvents.add(event);
      }
    }

    return relevantEvents;
  }

  // Adds the timeranges of all events in the collection to a list, sorted by start time.
  private List<TimeRange> getEventTimeRanges(Collection<Event> events) {
    List<TimeRange> timeRanges = new ArrayList<>();

    for (Event event : events) {
      timeRanges.add(event.getWhen());
    }

    // Sort the timeRanges by start time.
    Collections.sort(timeRanges, TimeRange.ORDER_BY_START);
    return timeRanges;
  }

  /**
   * Condenses an ordered list of time ranges (ordered by start time).
   * @param timeRanges list of timeRanges sorted by start time
   * @return list of condensed timeRanges (condensed means overlapping events are turned into a single event).
   */
  private List<TimeRange> getCondensedTimeRanges(List<TimeRange> timeRanges) {
    List<TimeRange> condensedTimeRanges = new ArrayList<>();

    for (TimeRange timeRange : timeRanges) {
      // Case 0: no previous timeRanges
      if (condensedTimeRanges.isEmpty()) {
        condensedTimeRanges.add(timeRange);

      } else {
        TimeRange currentCondensedRange = condensedTimeRanges.get(condensedTimeRanges.size()-1);

        // Case 1: current timeRange does not overlap with current condensed time range being built
        if (!currentCondensedRange.overlaps(timeRange)) {
          // Add the timeRange as a new condensed range
          condensedTimeRanges.add(timeRange);
        }
        // Case 2: current timeRange overlaps with current condensed time range being built, but is not contained in it
        else if (currentCondensedRange.overlaps(timeRange) && !currentCondensedRange.contains(timeRange)) {
          // Replace the current condensed time range with a new condensed time range augmented with the current timeRange
          TimeRange newCondensedRange = TimeRange.fromStartEnd(currentCondensedRange.start(), timeRange.end(), false);

          condensedTimeRanges.remove(condensedTimeRanges.size()-1);
          condensedTimeRanges.add(newCondensedRange);
        }

        // Case 3: if current timeRange is contained within current condensed time range being built, do nothing
      }
    }

    return condensedTimeRanges;
  }

  /**
   * Finds the timeRanges in the day which are not covered by any events. May add ranges of duration 0
   * between timeRanges.
   * @param coveredRanges List of timeRanges which we are finding the inverse of
   * @return List of timeRanges between the timeRanges in coveredRanges
   */
  private List<TimeRange> getInverseTimeRanges(List<TimeRange> coveredRanges) {
    List<TimeRange> inverseRanges = new ArrayList<>();

    if (coveredRanges.isEmpty()){
      inverseRanges.add(TimeRange.WHOLE_DAY);
      return inverseRanges;
    }

    TimeRange fromStart = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, coveredRanges.get(0).start(), false);
    inverseRanges.add(fromStart);

    int lastEnd = coveredRanges.get(0).end();
    int nextStart;

    // With every loop iteration, add the inverse range from [coveredRanges.get(i).end to coveredRanges.get(i+1).start()]
    // Loop from the second element to the last element: the first and last timeRanges are a special
    // case as we must look at the inverse TimeRange [0 to first.start()] (1), and [last.end() to end of day] (2).
    // Ex: [Start of day|--(1)--|First timeRange ... other ranges ... Last timeRange|--(2)--|End of day] 
    for (int i = 1; i < coveredRanges.size(); i++){
      nextStart = coveredRanges.get(i).start();
      TimeRange betweenLastAndNext = TimeRange.fromStartEnd(lastEnd, nextStart, false);
      inverseRanges.add(betweenLastAndNext);
      lastEnd = coveredRanges.get(i).end();
    }

    TimeRange toEnd = TimeRange.fromStartEnd(lastEnd, TimeRange.END_OF_DAY , true);
    inverseRanges.add(toEnd);

    return inverseRanges;
   }

  // Filters out all timeRanges with a duration less than the duration of the meeting.
   private List<TimeRange> getValidTimeRanges(List<TimeRange> timeRanges, int duration) {
     List<TimeRange> validTimeRanges = new ArrayList<>();

     for (TimeRange timeRange : timeRanges) {
       if (timeRange.duration() >= duration) {
         validTimeRanges.add(timeRange);
       }
     }

     return validTimeRanges;
   }
}
