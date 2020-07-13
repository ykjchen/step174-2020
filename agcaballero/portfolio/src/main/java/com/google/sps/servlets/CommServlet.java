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
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Properties;

/** 
 * Servlet to post and retrieve messages sent
 * through the "Contact Me" form
 */
@WebServlet("/comm")
public class CommServlet extends HttpServlet {
  
  /**
   * A private class to represent a message
   * sent from "Contact Me"
   */
  private class Message {
    
    /** Name of the contacter */
    private String name;
    /** Email of the contacter */
    private String email;
    /** Subject of the contact message */
    private String subject;
    /** Text/main description of the contact message */
    private String body;
    
    public Message(String name, String email, String subject, String body) {
      this.name = name;
      this.email = email;
      this.subject = subject;
      this.body = body;
    }

    public Entity getEntity() {
      Entity entity = new Entity("Message");
      entity.setProperty("name", name);
      entity.setProperty("email", email);
      entity.setProperty("subject", subject);
      entity.setProperty("body", body);

      return entity;
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Retrieve info from form
    String fname = getParameter(request, "fname", "");
    String lname = getParameter(request, "lname", "");
    String email = getParameter(request, "email", "");
    String subject = getParameter(request, "subject", "");
    String body = getParameter(request, "body", "");
    
    // create a Message with info from form
    Message message = new Message(fname + " " + lname, email, subject, body);
    
    // add this Message to the database
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(message.getEntity());

    response.sendRedirect("/contact.html");
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
