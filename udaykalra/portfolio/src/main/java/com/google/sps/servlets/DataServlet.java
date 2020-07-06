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

package com.google.sps.servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some comments. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  ArrayList<String> comments = new ArrayList<String>();

  /**
   * Converts a ServerStats instance into a JSON string using the Gson library. Note: We first added
   * the Gson library dependency to pom.xml.
   */
  private String arrayListToJson(ArrayList<String> inputList) {
    Gson gson = new Gson();
    String json = gson.toJson(inputList);
    return json;
  }

  /**
   * Obtains comments json to display upon request.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    String outputJson = arrayListToJson(comments);
    response.getWriter().println(outputJson);
  }

  /**
   * Writes comment data to ArrayList data
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String text = getRequestParameter(request, "text-input", "");
    boolean toErase = Boolean.parseBoolean(getRequestParameter(request, "erase", "false"));

    if (toErase) {
      comments = new ArrayList<String>();
    } else if (!(text.equals(""))) {
      comments.add(text);
    }

    // Redirect back to the HTML page.
    response.sendRedirect("/index.html");
  }

  /**
   * Obtains parameter from Comments typing field.
   */
  private String getRequestParameter(
      HttpServletRequest request, String comment, String defaultValue) {
    String value = request.getParameter(comment);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
