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
    ArrayList<TimeRange> times = new ArrayList<TimeRange>();
     // minutes of day is + 1 to account for first and last minute of day
    boolean[] minutes = new boolean[MINUTES_IN_DAY + 1];

    for(int i = 0; i < minutes.length; i++) {
      minutes[i] = true;
    }

    for(Event event: events) {
      // if there's an overlap in attendees block off those times
      if(attendeeOverlap(event.getAttendees(), attendees)) {
        TimeRange range = event.getWhen();

        for(int i = range.start(); i < range.end(); i++)
          minutes[i] = false;
      }
    }

    int start = 0; 
    boolean available = minutes[start];

    // add available times to times array
    for(int i = 0; i < minutes.length; i++) {
      // if this is part of an available time range
      if(available) {
        // then, if current minute is false or you've reached end of day, add a new time range
        if(! minutes[i]  || i == MINUTES_IN_DAY) {
          int end = i;
          int duration = end - start;

          if(duration >= request.getDuration())
            times.add(TimeRange.fromStartEnd(start, end - 1, true)); // add time range (inclusive of start & end) 

          available = false;
        }
            
      }
      // if current time has been taken until now
      else {
        // if now available, set start to now & available to true
        if(minutes[i]) {
          start = i;
          available = true;
        }
      }
    }
    
    return times;
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

}
