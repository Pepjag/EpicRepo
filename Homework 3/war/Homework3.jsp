<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@ page import="com.google.appengine.api.datastore.Query"%>
<%@ page import="com.google.appengine.api.datastore.Entity"%>
<%@ page import="com.google.appengine.api.datastore.FetchOptions"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ page import="blog.entity.BlogPost"%>
<%@ page import="blog.util.Utils"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<html>
<head>
<title>HI!</title>
<link type="text/css" rel="stylesheet" href="Homework3.css" />
</head>

<body>
	<center>
    <div id="logo">
      <h1>
      <img src="images/wow_left.png" alt="WOW!"> 
      <a href="/"><span>Boblahblog</span></a>
      <img src="images/wow_right.png" alt="WOW!">
      </h1>
    </div>
    </center>
    
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
	%>
	<center><p>
		Hello, ${fn:escapeXml(user.nickname)}! Feel free to post on our blog! <br>
		<a href="post.jsp">[Create new post]</a>
		<a href="allposts.jsp">[See all posts]</a>
		<a href="subscribe.jsp">[Subscribe]</a>
	    <a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">[Sign out]</a>
	</p></center>
	<%
    } else {
	%>
	<center><p>
		Hello! <a
			href="<%= userService.createLoginURL(request.getRequestURI()) %>">Sign
			in</a> to post on our blog. <br>
			<a href="allposts.jsp">[See all posts]</a>
			<a href="subscribe.jsp">[Subscribe]</a>
	</p></center>
	<%
	    }

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key blogKey = KeyFactory.createKey("Blog", blogName);
    // Run an ancestor query to ensure we see the most up-to-date
    // view of the Greetings belonging to the selected Blog.
    Query query = new Query("Greeting", blogKey).addSort("date", Query.SortDirection.DESCENDING);
    List<Entity> greetings = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(5));
    if (greetings.isEmpty()) {
        %>
	<p>No one has posted on our blog! Why don't we change that?</p>
	<%
    } else {
        for (Entity greeting : greetings) {

            pageContext.setAttribute("page_content",
            		Utils.printFormattedPost(greeting));
     %>
   	<p>
		${page_content}
	</p>
	<%
        }
    }


    %>
</body>
</html>