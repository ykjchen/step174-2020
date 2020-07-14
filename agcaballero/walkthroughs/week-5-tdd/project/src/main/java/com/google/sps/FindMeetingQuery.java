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
   
  private static final int MINUTES_IN_DAY = 24 * 60;
   
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> attendees = request.getAttendees();
    ArrayList<TimeRange> times = new ArrayList<TimeRange>();
    boolean[] minutes = new boolean[MINUTES_IN_DAY];

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

    int beginning = 0; 
    boolean available = minutes[beginning];

    // add available times to times array
    for(int i = 0; i < minutes.length; i++) {
      // if all minutes since beginning are true 
      if(available) {
        // if current minute is false, add a new time range
        if(! minutes[i]) {
          int end = i - 1;
          int duration = end - beginning;

          if(duration >= request.getDuration())
            times.add(TimeRange.fromStartDuration(beginning, duration));

          available = false;
        }
            
      }
      // if current time has been taken until now
      else {
        // if now available, set beginning to now & available to true
        if(minutes[i]) {
          beginning = i;
          available = true;
        }
      }
    }
    
    return times;
  }
  
  /**
   * @return true if overlap between attendees of two events, false otherwise
   */
  private boolean attendeeOverlap(Collection<String> groupA, Collection<String> groupB) {
    for(String attendeeA: groupA)
      for(String attendeeB: groupB)
        if(attendeeA.equals(attendeeB)) return true;

    return false;
  }

}
