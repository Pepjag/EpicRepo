package blog;
 
import java.io.IOException;
import java.util.List;

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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
 
public class SubServlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws IOException {

        String blogName = req.getParameter("blogName");
        if (blogName == null){
        	blogName = "default";
        }
        Key blogKey = KeyFactory.createKey("Blog", blogName);
        
    	String btnVal=req.getParameter("b1");
    	String input=req.getParameter("email");
    	
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
    	if("Subscribe!".equals(btnVal)){
            Entity email = new Entity("email", input);
            System.out.println("Subscribe:" + input + "<end>");
            email.setProperty("email", input);     
            datastore.put(email);   		
    	}
    	else if("Unsubscribe!".equals(btnVal)){
    		// Use class Query to assemble a query
    		Query query = new Query("email", blogKey);
    		List<Entity> emails = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    		
			System.out.print(emails.size());
			
    		for (Entity email : emails){
    			System.out.print(email.toString());
    			if (email.getProperty("email").equals(input)) {
    				datastore.delete(email.getKey());
    			}
    		}
    	}
        resp.sendRedirect("/Homework3.jsp");
    }
}