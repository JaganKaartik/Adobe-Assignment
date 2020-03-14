package org.jkan997.booklibrary.servlet;

import java.util.Map;
import javax.servlet.Servlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.LoggerFactory;

@Component(service = Servlet.class, property = {
    "sling.servlet.resourceTypes=" + "book",
    "sling.servlet.extensions=" + "reserve",
    "sling.servlet.extensions=" + "unreserve",
    "sling.servlet.extensions=" + "rent",
    "sling.servlet.extensions=" + "return"
})
public class ModifyBookState extends SlingSafeMethodsServlet {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ModifyBookState.class);

    @Reference
    SlingRepository repository;

    @Activate
    protected void activate(Map<String, Object> props) {
        LOGGER.info("Activating " + this.getClass().getSimpleName());
    }
    
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        /*
        YOUR IMPLEMENTATION HERE
         */
        throw new RuntimeException("" + getClass().getCanonicalName() + " not implemented yet.");
    }

}
