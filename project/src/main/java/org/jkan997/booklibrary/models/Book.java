package org.jkan997.booklibrary.models;

import javax.jcr.Value;

// Complete this class if you would like to use Sling Models
public class  Book {
    private Value author;
    private Value title;
    private String path;
    private Value reserved;
    private String genre;
    private Boolean isReserved;

    public Value getAuthor() {
		return author;
	}
	public void setAuthor(Value author) {
		this.author = author;
	}
	public Value getTitle() {
		return title;
	}
	public void setTitle(Value title) {
		this.title = title;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Value getReserved() {
		return reserved;
	}
	public void setReserved(Value reserved) {
		this.reserved = reserved;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public Boolean getIsReserved() {
		return isReserved;
	}
	public void setIsReserved(Boolean isReserved) {
		this.isReserved = isReserved;
	}
}
