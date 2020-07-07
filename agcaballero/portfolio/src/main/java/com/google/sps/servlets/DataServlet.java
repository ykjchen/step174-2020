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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.FetchOptions.Builder;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Servlet that accepts data from the comments form &
 * posts that data to the comments.html page
 */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  /**
   * Nested private class to represent a comment
   */
  private class Comment {
    /** Stores the date & time comment submitted */
    private final Date timestamp;
    /** Stores the name of the commenter */
    private final String name;
    /** Stores the email of the commenter */
    private final String email;
    /** Stores the text of the comment */
    private final String text;

    /**
     * Constructs a Comment object from an Entity
     */
    public Comment(Entity entity) {
      timestamp = (Date) entity.getProperty("timestamp");
      name = (String) entity.getProperty("name");
      email = (String) entity.getProperty("email");
      text = (String) entity.getProperty("text");
    }

    /**
     * @return comment in format: "name (email): text"
     */
    public String toString() {
      return name + " (" + email + "): " + text;
    }

    /**
     * @return a Comment as a HTML div with proper formatting to be displayed
     */
    private String htmlFormat() {
      // gets time in local time zone (default: US ET)
      LocalDateTime localTime =
          new LocalDateTime(timestamp.getTime(), DateTimeZone.forID("US/Eastern"));
      DateTimeFormatter formatter = DateTimeFormat.forPattern("h:mm a M/dd/yy");

      return "<div class='comment-div'>"
          + "<p class='date'>" + formatter.print(localTime) + "</p>"
          + "<p><b>" + name + " (" + email + "):</b> <br><br>" + text + "</p></div>";
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Retrieve max number of comments (if no info provided, default is 50)
    int maxComments = Integer.parseInt(getRequestParameter(request, "max-comments", "50"));

    // Retrieve from Datastore all entities of type "Comment", sorted by descending time
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);
    StringBuilder commentDivs = new StringBuilder();

    // Build a String of divs to hold capped # of comments to add to page
    for (Entity entity : results.asIterable(FetchOptions.Builder.withLimit(maxComments))) {
      // Create a Comment from the Entity
      Comment comment = new Comment(entity);

      // append current entity's div to HTML string commentDivs
      commentDivs.append(comment.htmlFormat());
    }

    // Respond to request with the commentDivs html
    response.setContentType("application/html;");
    response.getWriter().println(commentDivs);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Retrieve input from form & store it in commentEntity
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name", getRequestParameter(request, "name", ""));
    commentEntity.setProperty("email", getRequestParameter(request, "email", ""));
    commentEntity.setProperty("text", getRequestParameter(request, "comment", ""));

    // Store date/time in commentEntity
    Calendar cal = Calendar.getInstance();
    commentEntity.setProperty("timestamp", cal.getTime());
    
    // Put commentEntity into Datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

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
