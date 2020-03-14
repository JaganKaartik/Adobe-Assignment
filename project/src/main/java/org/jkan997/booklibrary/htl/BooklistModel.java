package org.jkan997.booklibrary.htl;

import java.util.Date;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BooklistModel {
    
    public String getDate() {
        return (new Date()).toString();
    }

    /*
        YOUR HTL MODEL HERE HERE
     */
}
