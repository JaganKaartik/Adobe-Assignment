package org.jkan997.booklibrary.util.internal;

import java.util.Calendar;
import java.util.Date;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import org.jkan997.booklibrary.util.ISO8601;

/**
 *
 * @author jakaniew
 */
public class Book {

    private String title;
    private String author;
    private String genre;
    private String path;

    public Book(Node n) throws RepositoryException {
        this.fromNode(n);
    }

    public Book() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

  

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public final void fromNode(Node n) throws RepositoryException {
        title = n.getProperty("title").getString();
        author = n.getProperty("author").getString();
        if (n.hasProperty("reserved")) {
          //  reserved = n.getProperty("reserved").getDate();
        }
        genre = n.getParent().getName();
        path = n.getPath();
    }

    public static int C = 0;

    public void toJSON(JsonWriter json) throws IOException {
        json.beginObject();
        json.name("title").value(this.getTitle());
        json.name("author").value(this.getAuthor());
        json.name("genre").value(this.getGenre());
        C++;
        if (C % 2 == 0) {
            json.name("reserved").value(ISO8601.format(Calendar.getInstance()));
        }
        json.name("path").value(this.getPath());
        json.endObject();
    }
}
