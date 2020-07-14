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

package com.google.sps.servletData;

/** Represents a response to the login/logout link on the home page*/
public final class LogInformation {
  // Link will say LOGIN if the user is logged out, or LOGOUT if the user is logged in
  private final String logMessage;
  private final String logUrl;

  public LogInformation(String logMessage, String logUrl) {
    this.logMessage = logMessage;
    this.logUrl = logUrl;
  }

  public String getLogMessage() {
    return logMessage;
  }

  public String getLogUrl() {
    return logUrl;
  }
}