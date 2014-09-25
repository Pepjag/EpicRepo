package blog.util;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;

public class Utils {
	public static String printFormattedPost(Entity post){
		String outputString = new String();
		String userString = post.getProperty("user").toString();
		if (userString != null) {
			userString = userString.split("@")[0];
		} else {
			userString = "NULL USER";
		}
		
		outputString +="<center>\n";
		outputString +="<div class=\"relative elem-green\">\n";
		outputString +="  <span class=\"label\">" + post.getProperty("title") + "</span>\n";
		outputString +="  <p><br>\n";
		outputString +="    " + ((Text)post.getProperty("content")).getValue() + "<br><br> - " + userString + "\n";
		outputString +="  <br></p>\n";
		outputString +="  <span class=\"endlabel\">" + post.getProperty("date") + "</span>\n";
		outputString +="</div>\n";
		outputString +="</center>\n";
				
		return outputString;
	}
}
