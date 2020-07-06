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

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Retrieve max number of comments (if no info provided, default is 50)
    int maxComments = Integer.parseInt(getParameter(request, "max-comments", "50"));

    // Retrieve from Datastore entities of type "Comment"
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);
    StringBuilder commentDivs = new StringBuilder();

    int count = 0;

    // Build a String of divs to hold comments to add to page
    for (Entity entity : results.asIterable()) {
      // Check if max number of comments have been loaded
      if(count == maxComments) break;

      // Retrieve info from the Entity
      Date timestamp = (Date) entity.getProperty("timestamp");
      String name = (String) entity.getProperty("name");
      String email = (String) entity.getProperty("email");
      String comment = (String) entity.getProperty("comment");

      // append current div to HTML string commentDivs
      commentDivs.append(formatComment(timestamp, name, email, comment));
      
      // increment count of comments
      count++;  
    }

    // Respond to request with the commentDivs html
    response.setContentType("application/html;");
    response.getWriter().println(commentDivs);
  }

  // Takes properties of a comment and formats them with proper HTML
  private String formatComment(Date timestamp, String name, String email, String comment) {
    // gets time in local time zone (default: US ET)
    LocalDateTime ldt = new LocalDateTime(timestamp.getTime(), DateTimeZone.forID("US/Eastern"));
    DateTimeFormatter fmt = DateTimeFormat.forPattern("h:mm a M/dd/yy");

    return "<div class='comment-div'>"
        + "<p class='date'>" + fmt.print(ldt) + "</p>"
        + "<p><b>" + name + " (" + email + "):</b> <br><br>" + comment + "</p></div>";
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Retrieve input from form & store it in commentEntity
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name", getParameter(request, "name", ""));
    commentEntity.setProperty("email", getParameter(request, "email", ""));
    commentEntity.setProperty("comment", getParameter(request, "comment", ""));

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
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
