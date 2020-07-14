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

package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.servletData.LogInformation;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/log-url")
public class LogUrlServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    UserService userService = UserServiceFactory.getUserService();

    // If user is not logged in, give a login link in the navbar
    if (!userService.isUserLoggedIn()) {
      String loginMessage = "LOGIN";
      String loginUrl = userService.createLoginURL("/index.html");

      LogInformation logInfo = new LogInformation(loginMessage, loginUrl);
      Gson gson = new Gson();
      out.println(gson.toJson(logInfo));
      return;
    } else {
      String logoutMessage = "LOGOUT";
      String logoutUrl = userService.createLogoutURL("/index.html");
      LogInformation logInfo = new LogInformation(logoutMessage, logoutUrl);
      Gson gson = new Gson();
      out.println(gson.toJson(logInfo));
      return;
    }
  }
}
