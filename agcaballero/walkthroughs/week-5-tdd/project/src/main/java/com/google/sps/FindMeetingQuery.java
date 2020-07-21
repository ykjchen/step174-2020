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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public final class FindMeetingQuery {

  /** An enum to represent the different options for availability at every meeting time */
  private enum Availability { ALL_AVAILABLE, MANDATORY_AVAILABLE, UNAVAILABLE }
   
  /** the number of minutes in a day */
  private static final int MINUTES_IN_DAY = 24 * 60;
  
  /**
   * Takes the events of the day and information about a potential meeting 
   * and returns the time ranges in which this meeting could be scheduled
   *
   * @return a collection of TimeRanges in which the meeting could be scheduled
   * @param events the collection of events scheduled for that day
   * @param request the meeting request to be fulfilled (will have duration & attendees)
   */ 
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> attendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getOptionalAttendees();

    // minutes of day is + 1 to account for first and last minute of day
    Availability[] minutes = new Availability[MINUTES_IN_DAY + 1];
    // set all spots to default value of all available
    Arrays.fill(minutes, Availability.ALL_AVAILABLE);

    for (Event event: events) {
      Availability status = Availability.ALL_AVAILABLE;
      
      if (! Collections.disjoint(event.getAttendees(), attendees)) {
        // if there's overlap in mandatory attendees, time is unavailable
        status = Availability.UNAVAILABLE;
      } else if (! Collections.disjoint(event.getAttendees(), optionalAttendees)) {
        // if there's an overlap in only the optional attendees, time is available for only the mandatory employees
        status = Availability.MANDATORY_AVAILABLE;
      }

      if (status != Availability.ALL_AVAILABLE) {
        TimeRange range = event.getWhen();

        for (int i = range.start(); i < range.end(); i++)
          // make sure unavailable times are not overwritten as mandatory available
          if (minutes[i] != Availability.UNAVAILABLE)
            minutes[i] = status;
      }
    }
    
    // declare variables necessary to keep track of times that work for everyone
    // and times that work just for mandatory attendees
    int start = 0, startWithOptional = 0; 
    ArrayList<TimeRange> times = new ArrayList<TimeRange>();
    ArrayList<TimeRange> timesWithOptional = new ArrayList<TimeRange>();

    boolean available = false, availableWithOptional = false;
    switch (minutes[start]) {
      case ALL_AVAILABLE:
       availableWithOptional = true;
      case MANDATORY_AVAILABLE: 
       available = true;
    }

    // go through minutes array once and find all the available times with optional attendees 
    // & without optional attendees. for each availability status (all available, mandatory available,
    // and none available), you have to either add an available time or start an availability period
    // for other the times (with just mandatory) or timesWithOptional (all attendees).
    for(int i = 0; i < minutes.length; i++) {
      switch(minutes[i]) {
        case ALL_AVAILABLE:
          // start availability period for all attendees (including optional)
          if (! availableWithOptional) {
            startWithOptional = i;
            availableWithOptional = true;
          }

          // start (or continue) availability period for just mandatory atttendees
          if (! available) {
            start = i;
            available = true;
          }

          break;

        case MANDATORY_AVAILABLE:
          // end availability period for all attendees & add a possible meeting time range
          if (availableWithOptional) {
            addMeeting(request, startWithOptional, i, false, timesWithOptional);
            availableWithOptional = false;
          }
          
          // start (or continue) availability period for just mandatory atttendees
          if (! available) {
            start = i;
            available = true;
          }

          break;

        case UNAVAILABLE: 
          // end availability period for all attendees & add a possible meeting time range
          if (availableWithOptional) {
            addMeeting(request, startWithOptional, i, false, timesWithOptional);
            availableWithOptional = false;
          }
          
          // end availability period for mandatory attendees & add a possible meeting time range
          if (available) {
            addMeeting(request, start, i, false, times);
            available = false;
          }

          break;
      }
    }

    // add meetings for end of day (if the time up till then was available)
    if (availableWithOptional) {
      addMeeting(request, startWithOptional, TimeRange.END_OF_DAY, true, timesWithOptional);
    }
          
    if (available) {
      addMeeting(request, start, TimeRange.END_OF_DAY, true, times);
    }
    
    // if there are any times where everyone can attend, return those
    // else just return times where all mandatory attendees can come
    if (timesWithOptional.size() > 0) {
      return timesWithOptional;
    } else {
      return times;
    }
  }

  /** Private helper method where if a possible time range is long enough,
   *  will add that time to the TimeRange collection
   */
  private void addMeeting(MeetingRequest request, int start, int end, boolean inclusive, Collection<TimeRange> times) {
    int duration = end - start + 1;

    if(duration >= request.getDuration())
      times.add(TimeRange.fromStartEnd(start, end, inclusive)); 
  }
}
