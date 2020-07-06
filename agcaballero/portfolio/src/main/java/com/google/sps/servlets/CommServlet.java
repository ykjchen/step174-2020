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

import java.io.IOException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Properties;

/** Servlet that allows users to send me emails */
@WebServlet("/comm")
public class CommServlet extends HttpServlet {

  private static final String FROM = "site-notif@google.com";
  private static final String RECIPIENT = "agcaballero@google.com"; // my work email

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String fname = getParameter(request, "fname", "");
    String lname = getParameter(request, "lname", "");
    String email = getParameter(request, "email", "");
    String subject = getParameter(request, "subject", "");
    String body = getParameter(request, "body", "");

    // set up SMTP mail server
    Properties prop = new Properties();
    prop.setProperty("mail.smtp.host", "localhost");
    prop.put("mail.smtp.host", "smtp server ");
    prop.put("mail.smtp.auth", "true");
    prop.put("mail.smtp.port", "25");
    Session session = Session.getDefaultInstance(prop, null);

    System.out.println("ATTN 1");
    
    try {
      Message message = new MimeMessage(session);

      // set sender & recipient emails
      message.setFrom(new InternetAddress(FROM));
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(RECIPIENT));
      
      // set subject, content & date of email
      message.setSubject(subject);
      message.setText(body + "\n-" + fname + " " + lname + " (" + email + ")");
      message.setSentDate(new Date());

      System.out.println("ATTN 2");
      
      Transport.send(message);
      System.out.println("Message sent!");
    } catch (MessagingException e) {
      e.printStackTrace();
    }

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
