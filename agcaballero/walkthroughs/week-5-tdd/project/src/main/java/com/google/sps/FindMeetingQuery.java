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

public final class FindMeetingQuery {
   
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
    
    // are there any mandatory employees?
    boolean mandatory = false;
    if(attendees.size() > 0) {
      mandatory = true;
    }
    
    // are there any optional employees?
    boolean optional = false; 
    if(optionalAttendees.size() > 0) {
      optional = true;
    }

    // minutes of day is + 1 to account for first and last minute of day
    int[] minutes = new int[MINUTES_IN_DAY + 1];
    // 0 - all attendees (mandatory & optional) can attend 
    // 1 - mandatory attendees can attend (but not all optional)
    // 2 - not all mandatory attendees can attend (even if all optional can)

    for(Event event: events) {
      int status = 0;
      
      if (attendeeOverlap(event.getAttendees(), attendees)) {
        status = 2;
      }
      else if (attendeeOverlap(event.getAttendees(), optionalAttendees)) {
        status = 1;
      }

      if (status != 0) {

        TimeRange range = event.getWhen();

        for(int i = range.start(); i < range.end(); i++)
          // make sure 2s (which are higher priority than 1s) are not overwritten
          if(minutes[i] != 2)
            minutes[i] = status;
      }
    }
    
    // declare variables necessary to keep track of times that work for everyone
    // and times that work just for mandatory employees
    int start = 0, startWithOptional = 0; 
    boolean available = false, availableWithOptional = false;
    ArrayList<TimeRange> times = new ArrayList<TimeRange>();
    ArrayList<TimeRange> timesWithOptional = new ArrayList<TimeRange>();

    switch(minutes[start]) {
      case 0:
       availableWithOptional = true;
      case 1: 
       available = true;
    }

    // add available times to times array
    for(int i = 0; i < minutes.length; i++) {
      switch(minutes[i]) {
        case 0:
          if(! availableWithOptional && optional) {
            startWithOptional = i;
            availableWithOptional = true;
          }

          if(! available) {
            start = i;
            available = true;
          }

          break;

        case 1:
          if(availableWithOptional && optional) {
            addMeeting(request, startWithOptional, i, false, timesWithOptional);
            availableWithOptional = false;
          }
          
          if(! available) {
            start = i;
            available = true;
          }

          break;

        case 2: 
          if(availableWithOptional && optional) {
            addMeeting(request, startWithOptional, i, false, timesWithOptional);
            availableWithOptional = false;
          }
          
          if(available) {
            addMeeting(request, start, i, false, times);
            available = false;
          }

          break;
      }
    }

    // add meetings for end of day
    if(availableWithOptional && optional) {
      addMeeting(request, startWithOptional, TimeRange.END_OF_DAY, true, timesWithOptional);
    }
          
    if(available) {
      addMeeting(request, start, TimeRange.END_OF_DAY, true, times);
    }
    
    // if there are any times where everyone can attend, return those
    // else just return times where all mandatory attendees can come
    if(timesWithOptional.size() > 0 || (! mandatory && optional)) {
      return timesWithOptional;
    }
    else  {
      return times;
    }
  }
  
  /**
   * A private helper method to determine if there is overlap between two groups
   * of attendees, represented as String collections
   *
   * @return true if overlap between attendees of two events, false otherwise
   */
  private boolean attendeeOverlap(Collection<String> groupA, Collection<String> groupB) {
    for(String attendeeA: groupA)
      for(String attendeeB: groupB)
        if(attendeeA.equals(attendeeB)) return true;

    return false;
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
