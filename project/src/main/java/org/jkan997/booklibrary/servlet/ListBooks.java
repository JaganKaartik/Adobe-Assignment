package org.jkan997.booklibrary.servlet;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.Servlet;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.*;
import org.jkan997.booklibrary.models.Book;
import com.google.gson.stream.JsonWriter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.LoggerFactory;

@Component(service = Servlet.class, property = {
    "sling.servlet.methods=" + HttpConstants.METHOD_GET,
    "sling.servlet.paths=" + "/bin/ListBooks"
})
public class ListBooks extends SlingSafeMethodsServlet {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ListBooks.class);

    @Reference
    SlingRepository repository;

    @Activate
    protected void activate(Map<String, Object> props) {
        LOGGER.info("Activating " + this.getClass().getSimpleName());
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try{
            PrintWriter wrt = response.getWriter();
            String author = request.getParameter("author");
            String title = request.getParameter("title");
            Session jcrSession = repository.loginAdministrative(null);
            String queryString = "select * from [nt:unstructured] as p where isdescendantnode (p, [/books]) AND (p.title LIKE '"+title+"%' AND p.author LIKE'%"+author+"%')";
            QueryManager queryManager= jcrSession.getWorkspace().getQueryManager();
            Query query = queryManager.createQuery(queryString,"JCR-SQL2");
            QueryResult result = query.execute();
            NodeIterator nodeIter = result.getNodes();         
            JsonWriter writer = new JsonWriter(wrt);
            List<Book> booklist = new ArrayList<Book>();
            while ( nodeIter.hasNext() ) 
            {
                Node node = nodeIter.nextNode();
                Property property = node.getProperty("author");     
                Value authorFromDB = property.getValue();
                property = node.getProperty("title");     
                Value titleFromDB = property.getValue();
                String genre = node.getPath();
                String path = genre;
                genre = genre.replaceAll("\\/(books)\\/","");
                genre = genre.replaceAll("\\/(.)+","");
                Book b = new Book();
                b.setAuthor(authorFromDB);
                b.setTitle(titleFromDB);
                b.setGenre(genre);
                b.setPath(path);
                try{
                    property = node.getProperty("reserved");
                    Value status = property.getValue();
                    b.setReserved(status);
                    b.setIsReserved(true);
                }
                catch(Exception e){
                    b.setIsReserved(false);
                }
                booklist.add(b); 
            }
            writer.beginArray();
            for(Book b : booklist)
            {
                writer.beginObject();
                writer.name("title").value(b.getTitle().getString());
                writer.name("author").value(b.getAuthor().getString());
                writer.name("genre").value(b.getGenre());
                if(Boolean.compare(b.getIsReserved(),true)==0)
                {
                    writer.name("reserved").value(b.getReserved().getString());
                }
                writer.name("path").value(b.getPath());
                writer.endObject();
            }
            writer.endArray();
            writer.close();
            wrt.close();
        }
        catch(Exception ex){
            throw new RuntimeException(ex);
        }
        throw new RuntimeException("" + getClass().getCanonicalName() + " not implemented yet.");
    }
}
