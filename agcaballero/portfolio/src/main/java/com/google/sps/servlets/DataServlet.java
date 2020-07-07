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
import java.util.Arrays;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that accepts data from the comments form &
 * posts that data to the comments.html page
 */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private class Comment {
    /** Stores the name of the commenter */
    private final String name; 
    /** Stores the email of the commenter */
    private final String email; 
    /** Stores the text of the comment */
    private final String comment; 

    /**
     * Constructs a Comment object and sets all fields 
     */
    public Comment(String name, String email, String comment) {
      this.name = name;
      this.email = email;
      this.comment = comment;
    }

    /**
     * @return comment in format: "name (email): comment" 
     */
    public String toString() {
      return name + " (" + email + "): " + comment;
    }
  }

  /** list to hold all comments that have been submitted in current session */
  private final ArrayList<Comment> comments = new ArrayList<Comment>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    StringBuilder commentDivs = new StringBuilder();

    // build a String of divs to add to page
    for (Comment comment : comments) {
      commentDivs.append("<div class='comment-div'><p>" + comment + "</p></div>");
    }

    // respond with the commentDivs html
    response.setContentType("application/html;");
    response.getWriter().println(commentDivs);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String name = getRequestParameter(request, "name", "");
    String email = getRequestParameter(request, "email", "");
    String comment = getRequestParameter(request, "comment", "");

    comments.add(new Comment(name, email, comment));

    // Redirect to the comments page
    response.sendRedirect("comments.html");
  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getRequestParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
