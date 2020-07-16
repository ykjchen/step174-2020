// Copyright 2019 Google LLC
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
    System.out.println(timeRanges);

    // Get a condensed TimeRanges list for the relevent events (condensed means overlapping events are turned into a single event).
    List<TimeRange> condensedTimeRanges = getCondensedTimeRanges(timeRanges);
    System.out.println(condensedTimeRanges + "\n");

    return condensedTimeRanges;
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
}
