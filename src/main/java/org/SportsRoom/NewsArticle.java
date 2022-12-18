package org.SportsRoom;

public class NewsArticle {

	private String name, imgLink, permaLink, content, publishDate;

	public NewsArticle(String name, String content, String imgLink, String permaLink,String publishDate){
		this.name = name;
		this.content = content;
		this.imgLink = imgLink;
		this.permaLink = permaLink;
		this.publishDate = publishDate.substring(0,10);
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

	public String getPermaLink(){return permaLink;}

	public String getImgLink(){return imgLink;}
	public String toString() {
		return name + "\n" + "Published on: " + publishDate + "\n" + content;
	}
}
