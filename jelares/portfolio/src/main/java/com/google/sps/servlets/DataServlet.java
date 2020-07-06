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
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
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

  // Make the comments list data type
  final ExampleComments comments = new ExampleComments(new ArrayList<String>());

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Query to find all comment entities sorted from newest to oldest
    Query query = new Query("Comment").addSort("timestamp", SortDirection.ASCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // adding all comment entities to an ExampleComments instance
    final ExampleComments comments = new ExampleComments(new ArrayList<String>());
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String commentText = (String) entity.getProperty("comment-text");
      comments.addComment(commentText);
    }

    response.setContentType("application/json;");
    response.getWriter().println(commentsToJson(comments));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Get the input from the form.
    final String comment = request.getParameter("comment");
    final long timestamp = System.currentTimeMillis();

    // Add the input to the comments object
    comments.addComment(comment);

    // Add the input to datastore
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("comment-text", comment);
    commentEntity.setProperty("timestamp", timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    // Redirect back to the HTML page.
    response.sendRedirect("/data/dataPage.html");
  }

  /** Turns ExampleComments object into JSON */
  public String commentsToJson(ExampleComments comments) {
    final int numberOfComments = comments.getNumComments();

    // Initially there will be no comments
    if (numberOfComments == 0) {
      return "{}";

    } else {
      String jsonComments = "{";

      /**
      * Adds all comments to the jsonObject in the form:
      * {"comment1": "text", "comment2": "text", ..., "commentN": "text"}
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
}