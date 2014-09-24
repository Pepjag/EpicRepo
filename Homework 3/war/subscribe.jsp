<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
 
<html>
   <head>
   <link type="text/css" rel="stylesheet" href="Homework3.css" />
   </head>
 
  <body>
  
  <%
    String blogName = request.getParameter("blogName");
    if (blogName == null) {
        blogName = "default";
    }
    pageContext.setAttribute("blogName", blogName);
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user != null) {
      pageContext.setAttribute("user", user);
    }
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Key blogKey = KeyFactory.createKey("Blog", blogName);
%>
  
  
  
    <form action="/subscribe" method="post">
    <p>Enter your email!</p>
      <div><textarea name="email" rows="1" cols="60"></textarea></div>
      <div><input type="submit" name="b1" value="Subscribe!" /></div>
      <div><input type="submit" name="b1" value="Unsubscribe!"></div>
    </form>
 
 
  </body>
</html>