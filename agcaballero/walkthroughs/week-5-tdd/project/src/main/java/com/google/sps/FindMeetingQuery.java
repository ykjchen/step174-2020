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
    // this array keeps track of whether a minute in the day is available or not
    // if the value of minutes[number] is true then it means that minute (which corresponds to number)
    // is available; if it's false, then it's unavailable
    // minutes of day is + 1 to account for first and last minute of day
    boolean[] availableMinutes = new boolean[MINUTES_IN_DAY + 1];

    for(int i = 0; i < availableMinutes.length; i++) {
      availableMinutes[i] = true;
    }

    for(Event event: events) {
      // if there's an overlap in attendees block off those times (if they're not disjoint sets)
      if(! Collections.disjoint(event.getAttendees(), attendees)) {
        TimeRange range = event.getWhen();

        for(int i = range.start(); i < range.end(); i++)
          availableMinutes[i] = false;
      }
    }

    int start = 0; 
    boolean wasLastMinuteAvailable = availableMinutes[start];

    // add available times to times array
    for(int i = 0; i < availableMinutes.length; i++) {
      if(wasLastMinuteAvailable) {
        // If the previous minute was available, but the current minute is unavailable or if it's
        // the end of the day, this is the end of an available time range. If the time range is longer 
        // than the required duration, it's recorded as an available time range.
        if(! availableMinutes[i]  || i == availableMinutes.length - 1) {
          int end = i;
          int duration = end - start;

          if(duration >= request.getDuration()) {
            times.add(TimeRange.fromStartEnd(start, end - 1, true)); // add time range (inclusive of start & end) 
          }
        
          wasLastMinuteAvailable = false;
        }
            
      }
      else {
        // If the last minute was unavailable, but this minute is available, then this is the beginning
        // of a new available time range, so start will be set to this minute and wasLastMinuteAvailable to true.
        if(availableMinutes[i]) {
          start = i;
          wasLastMinuteAvailable = true;
        }
      }
    }
    
    return times;
  }
}
