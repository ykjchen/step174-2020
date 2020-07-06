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

import com.google.sps.data.ExampleComments;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Create the example comments object with 3 comments
    final List<String> commentText = new ArrayList<>();
    commentText.add("Hi comment1!");
    commentText.add("Hey comment2!");
    commentText.add("Sup comment3!");
    final ExampleComments threeComments = new ExampleComments(commentText);

    // Turn the comments data into a JSON string
    final String jsonComments = commentsToJson(threeComments);

    // Send the JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(jsonComments);
  }

  /** Turns ExampleComments object into JSON */
  public String commentsToJson(ExampleComments comments) {
    String jsonComments = "{";
    int numberOfComments = comments.getNumComments();

    /**
        Adds all comments to the jsonObject in the form:
        {"comment1": "text", "comment2": "text", ..., "commentN": "text"}
    */
    for (int commentNumber = 0; commentNumber < numberOfComments; commentNumber++) {
      jsonComments += "\"comment" + commentNumber + "\": ";
      jsonComments += "\"" + comments.getComment(commentNumber) + "\"";

      if (commentNumber < (numberOfComments - 1)) {
        jsonComments += ", ";
      } else {
        jsonComments += "}";
      }
    }

    return jsonComments;
  }
}