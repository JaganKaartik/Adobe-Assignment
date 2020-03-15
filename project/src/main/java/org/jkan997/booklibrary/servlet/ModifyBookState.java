package org.jkan997.booklibrary.servlet;

import org.jkan997.booklibrary.models.Book;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.lang.model.type.NullType;
import javax.lang.model.util.ElementScanner6;
import javax.servlet.Servlet;

import com.google.gson.stream.JsonWriter;

import java.io.PrintWriter;
import java.lang.String;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.scripting.sightly.impl.compiler.expression.node.NullLiteral;
import org.slf4j.LoggerFactory;
import org.apache.sling.api.servlets.HttpConstants;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Component(service = Servlet.class, property = { "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths=" + "/books/fiction/", "sling.servlet.paths=" + "/books/nonfiction/",
        "sling.servlet.resourceTypes=" + "book", "sling.servlet.extensions=" + "reserve",
        "sling.servlet.extensions=" + "unreserve", "sling.servlet.extensions=" + "rent",
        "sling.servlet.extensions=" + "return" })

public class ModifyBookState extends SlingSafeMethodsServlet {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ModifyBookState.class);

    private static final Value NullLiteral = null;

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
            JsonWriter writer = new JsonWriter(wrt);
            System.out.println("helo");
            String extension = request.getRequestPathInfo().getExtension();
            System.out.println(extension);
            String path = request.getPathInfo();
            path = path.replaceAll("\\/(books)\\/.*\\/","");
            String title = path.replaceAll("."+extension,"");
            Session jcrSession = repository.loginAdministrative(null);
            String queryString = "select * from [nt:unstructured] as p where isdescendantnode (p, [/books]) AND (p.title = '"+title+"')";
            System.out.println(queryString);
            javax.jcr.query.QueryManager queryManager= jcrSession.getWorkspace().getQueryManager();
            javax.jcr.query.Query query = queryManager.createQuery(queryString,"JCR-SQL2");
            javax.jcr.query.QueryResult result = query.execute();
            javax.jcr.NodeIterator nodeIter = result.getNodes();

            SimpleDateFormat dateTimeInGMT = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss aa");
	        dateTimeInGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
            dateTimeInGMT.format(new Date());
    
            while(nodeIter.hasNext())
            {
                Node node = nodeIter.nextNode();
                System.out.println(node);
                if(extension.equals("reserve"))                                                               
                {
                    String v = dateTimeInGMT.format(new Date());
                    System.out.println(v);
                    node.setProperty("reserved",v);
                    writer.beginObject();
                    writer.name("Operation Successfull").value("Reserved Book"+title);
                    writer.endObject();
                }
                else if(extension.equals("unreserve"))
                {
                    Value v = NullLiteral;
                    System.out.println(v);
                    node.setProperty("reserved",v);
                    writer.beginObject();
                    writer.name("Operation Successfull").value("Unreserved Book"+title);
                    writer.endObject();
                }
                jcrSession.save();
            } 
            writer.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        // throw new RuntimeException("" + getClass().getCanonicalName() + " not implemented yet.");
    }

}
