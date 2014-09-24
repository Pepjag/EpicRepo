package blog;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;


@SuppressWarnings("serial")
public class ScheduledSenderServlet extends HttpServlet {
	private static final Logger _logger = Logger.getLogger(ScheduledSenderServlet.class.getName());

	@SuppressWarnings("deprecation")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		try {
			_logger.info("Cron Job has been executed");
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			// Put your logic here
			// BEGIN

			Date yesterdayMorning = new Date();
			yesterdayMorning.setTime(yesterdayMorning.getTime() - 86400000);  // milliseconds in a day
			
			// build list of emails
    		Query emailQuery = new Query("Email Address").addSort("address", Query.SortDirection.DESCENDING);
    		List<Entity> emails = datastore.prepare(emailQuery).asList(FetchOptions.Builder.withDefaults());
    		
    		// build list of new posts
    		Filter dateMinFilter = new FilterPredicate("date", FilterOperator.GREATER_THAN_OR_EQUAL, yesterdayMorning);
    		Query postQuery = new Query("Greeting").setFilter(dateMinFilter);
    		List<Entity> posts = datastore.prepare(postQuery).asList(FetchOptions.Builder.withDefaults());
    		
    		String postString = new String();
			for (Entity post : posts){
				postString += post.getProperty("user").toString() + " posted \"" + post.getProperty("title") + "\" on " + post.getProperty("date") + " : \n\n";
				postString += post.getProperty("content") + "\n\n\n";
			} 		
    		
    		for (Entity email : emails){
    			String mailString = new String();
    			
    			mailString += "Hello, " + email.getProperty("address").toString() + "!\n\n";
    			
    			if (posts.size() > 0) {
    				mailString += "Here are the latest " + posts.size() + " posts :\n\n";
    			} else {
    				mailString = " We have no new posts :(\n";
    			}
    			
    			mailString += postString;
    			
    			_logger.info("Attempting to send an email to " + email.getProperty("address").toString());
    			
    			MimeMessage outMessage = new MimeMessage(session);
    			outMessage.setFrom(new InternetAddress("posts@sj-ee461l-testblog.appspotmail.com"));
    			outMessage.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email.getProperty("address").toString()));
    			outMessage.setSubject("The latest Bobblahblog posts just for you!\n\n");
    			outMessage.setText(mailString);
    			Transport.send(outMessage);   			
    		}
		} catch (Exception ex) {
			// Log any exceptions in your Cron Job
			_logger.info("ERROR: Could not send out Email Results response : " + ex.getMessage());
		}
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
}
