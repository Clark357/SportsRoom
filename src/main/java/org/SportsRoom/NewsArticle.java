package org.SportsRoom;
import java.time.*;

public class NewsArticle {

	private String name, imgLink, permaLink, content, publishDate;

	public NewsArticle(String name, String content, String imgLink, String permaLink,String publishDate){
		this.name = name;
		this.content = content;
		this.imgLink = imgLink;
		this.permaLink = permaLink;
		this.publishDate = publishDate;
	}

	public String getName() {
		return name;
	}

	public String getContent() {
		return content;
	}

	public String getDate() {
		return publishDate;
	}

	public String toString() {
		return name + "\n" + "Published on: " + publishDate + "\n" + content;
	}
}
