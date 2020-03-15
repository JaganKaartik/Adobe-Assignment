package org.jkan997.booklibrary.scheduled;

import javax.jcr.Session;
import javax.jcr.Value;

import com.google.gson.stream.JsonWriter;
import java.util.concurrent.TimeUnit;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.LoggerFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.jkan997.booklibrary.servlet.ImportBooks;
import javax.jcr.query.*;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.jcr.Node;


@Component(name = "Reservation Purge Service", service = Runnable.class, immediate = true, property = {"scheduler.period:Long=10"}) // Executed every 10 seconds
public class ReservationPurgeJob implements Runnable {
    
    @Reference
    SlingRepository repository;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ImportBooks.class);

    private static final Value NullLiteral = null;

    public void run() {
        LOGGER.info("Executing a perodic job " + getClass().getSimpleName());
        // YOUR IMPLEMENTATION HERE
        try{
        Session jcrSession = repository.loginAdministrative(null);
        String queryString = "select * from [nt:unstructured] as p where isdescendantnode (p, [/books])";
        QueryManager queryManager= jcrSession.getWorkspace().getQueryManager();
        Query query = queryManager.createQuery(queryString,"JCR-SQL2");
        QueryResult result = query.execute();
        NodeIterator nodeIter = result.getNodes();
        while(nodeIter.hasNext())
        {
            Node node = nodeIter.nextNode();
            Property property;
            try{
                property = node.getProperty("reserved");
                //All Books that are reserved.
                String dateFromDB = property.getValue().getString();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss aa");
                Date date1 = format.parse(dateFromDB);
                Date date2 = new Date();
                long diff = date2.getTime() - date1.getTime();
                System.out.println(date1);
                System.out.println(date2);
                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                if(diffInMinutes>=1) //diffInMinutes --> 1 Minute Threshold
                {
                    //If diffInMinutes is greater than 1 minute remove reservations ie. set property reserved to null
                    Value v = NullLiteral;
                    node.setProperty("reserved",v);
                }
                jcrSession.save();	        
            }
            catch(Exception e){
                /* reserved is null */
            }
        }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

}
