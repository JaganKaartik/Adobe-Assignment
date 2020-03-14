package org.jkan997.booklibrary.servlet;

import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.jcr.Node;
import javax.jcr.Session;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import com.google.gson.stream.JsonWriter;
import javax.servlet.Servlet;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.jcr.api.SlingRepository;
import org.jkan997.booklibrary.util.Constans;
import static org.jkan997.booklibrary.util.Constans.NT_UNSTRUCTURED;
import static org.jkan997.booklibrary.util.Constans.SLING_ORDERED_FOLDER;
import org.jkan997.booklibrary.util.internal.CsvProcessor;
import org.jkan997.booklibrary.util.internal.JcrHelper;
import org.slf4j.LoggerFactory;

@Component(service = Servlet.class, property = {
    "sling.servlet.methods=" + HttpConstants.METHOD_GET,
    "sling.servlet.paths=" + "/bin/ImportBooks"
})

public class ImportBooks extends SlingSafeMethodsServlet {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ImportBooks.class);

    @Reference
    SlingRepository repository;

    @Activate
    protected void activate(Map<String, Object> props) {
        LOGGER.info("Activation " + this.getClass().getSimpleName());
    }

    // This method import books to Sling Repository
    private void createApps(Node appsNode) throws Exception {
        Node bookList = null;
        if (appsNode.hasNode("booklist")) {
            bookList = appsNode.getNode("booklist");
            bookList.remove();
            appsNode.getSession().save();
        }
        bookList = appsNode.addNode("booklist", SLING_ORDERED_FOLDER);
        Node htlFile = bookList.addNode("booklist.html", "nt:file");
        Node contentNode = htlFile.addNode("jcr:content", "nt:resource");
        InputStream bookListIs = getClass().getResourceAsStream("/booklist.html");
        contentNode.setProperty("jcr:data", bookListIs);
        bookListIs.close();
        bookList.getSession().save();
    }

// This method import books to Sling Repository
    private int importBooks(Node parentNode, String resourceName) throws Exception {
        int res = 0;
        List<String[]> books = CsvProcessor.readBooks(resourceName);
        for (String[] book : books) {
            String author = book[1];
            String title = book[0];
            String newName = JcrHelper.escapeName(title);
            int i = 1;
            while (parentNode.hasNode(newName)) {
                newName = JcrHelper.escapeName(title) + " " + (i++);
            }
            Node bookNode = parentNode.addNode(newName, NT_UNSTRUCTURED);
            bookNode.setProperty("author", author);
            bookNode.setProperty("title", title);
            bookNode.setProperty("sling:resourceType", Constans.BOOK);
            res++;
        }
        return res;
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            response.setContentType("application/json");
            PrintWriter wrt = response.getWriter();
            Session jcrSession = repository.loginAdministrative(null);
            Node rootNode = jcrSession.getRootNode();
            Node appsNode = rootNode.getNode("apps");
            createApps(appsNode);
            Node booksNode;
            if (rootNode.hasNode("books")) {
                booksNode = rootNode.getNode("book s");
                booksNode.remove();
                jcrSession.save();
            }
            booksNode = rootNode.addNode("books", SLING_ORDERED_FOLDER);
            booksNode.setProperty("sling:resourceType", "booklist");
            Node fictionNode = booksNode.addNode("fiction", SLING_ORDERED_FOLDER);
            Node nonFictionNode = booksNode.addNode("nonfiction", SLING_ORDERED_FOLDER);
            int count = 0;
            count += importBooks(fictionNode, "fiction.csv");
            count += importBooks(nonFictionNode, "nonfiction.csv");
            jcrSession.save();
            JsonWriter writer = new JsonWriter(wrt);
            writer.beginObject();
            writer.name("BookCount").value(count);
            writer.endObject();
            writer.close();
            wrt.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
