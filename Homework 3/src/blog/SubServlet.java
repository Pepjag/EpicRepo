package blog;
 
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
 
public class SubServlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws IOException {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        String blogName = req.getParameter("blogName");
        if (blogName == null) {
            blogName = "default";
        }
        Key blogKey = KeyFactory.createKey("Blog", blogName);
        
    	String btnVal=req.getParameter("b1");
    	String input=req.getParameter("email");
        
    	if("Subscribe!".equals(btnVal)){
            Entity email = new Entity("Email Address", blogKey);
            email.setProperty("address", input);
            System.out.println("Subscribe:" + input + "\n");   
            datastore.put(email);   		
            
            try {
				Properties props = new Properties();
				Session session = Session.getDefaultInstance(props, null);
				MimeMessage outMessage = new MimeMessage(session);
				outMessage.setFrom(new InternetAddress("subscribe@sj-ee461l-testblog.appspotmail.com"));
				outMessage.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email.getProperty("address").toString()));
				outMessage.addRecipients(MimeMessage.RecipientType.BCC, "steven.prickett+bobblahblogsubserv@gmail.com");
				outMessage.setSubject("Congratulations on subscribing to BOBBLAHBLOG!");
				outMessage.setText("You rock! See you at 5!\n\nLove,\nSteven and Jose\n");
				Transport.send(outMessage);
            } catch (Exception ex) {
            	System.out.println("ERROR! Couldn't send congrats email! Exception: " + ex.getMessage());
            }
    	}
    	else if("Unsubscribe!".equals(btnVal)){
    		// Use class Query to assemble a query
    		Boolean removeSuccess = false;
    		Query query = new Query("Email Address").addSort("address", Query.SortDirection.DESCENDING);
    		List<Entity> emails = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    		
			System.out.print(emails.size());
			
    		for (Entity email : emails){
    			if (email.getProperty("address").equals(input)) {
    				System.out.println("Removing " + email.getProperty("address") + " from sub list...\n");
    				removeSuccess = true;
    				datastore.delete(email.getKey());
    				
                    try {
        				Properties props = new Properties();
        				Session session = Session.getDefaultInstance(props, null);
        				MimeMessage outMessage = new MimeMessage(session);
        				outMessage.setFrom(new InternetAddress("subscribe@sj-ee461l-testblog.appspotmail.com"));
        				outMessage.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email.getProperty("address").toString()));
        				outMessage.addRecipients(MimeMessage.RecipientType.BCC, "steven.prickett+bobblahblogsubserv@gmail.com");
        				outMessage.setSubject("You have unsubscribed from BOBBLAHBLOG :(");
        				outMessage.setText("Oh well!\n\nLove,\nSteven and Jose\n");
        				Transport.send(outMessage);
                    } catch (Exception ex) {
                    	System.out.println("ERROR! Couldn't send unsub email! Exception: " + ex.getMessage());
                    }
    			}
    		}
    		if (!removeSuccess) {
    			System.out.println("Apparently we couldn't find " + input + " in the datastore.");
    		}
    	}
        resp.sendRedirect("/Homework3.jsp");
    }
}