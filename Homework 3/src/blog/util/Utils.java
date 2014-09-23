package blog.util;

import com.google.appengine.api.datastore.Entity;

public class Utils {
	public static String printFormattedPost(Entity post){
		String outputString = new String();
		String userString = post.getProperty("user").toString();
		userString = userString.split("@")[0];
		
		outputString +="<div class=\"relative elem-green\">";
		outputString +="  <span class=\"label\">" + post.getProperty("title") + "</span>";
		outputString +="  <p><br>";
		outputString +="    " + post.getProperty("content") + "<br><br> - " + userString;
		outputString +="  <br></p>";
		outputString +="  <span class=\"endlabel\">" + post.getProperty("date") + "</span>";
		outputString +="</div>";
				
		return outputString;
	}
}
