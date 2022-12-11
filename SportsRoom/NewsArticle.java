package SportsRoom;
import java.time.*;

public class NewsArticle {

	private String name;
	private String content;
	private LocalDate dateOfPublish;

	public NewsArticle(String name, String content, LocalDate publishDate){
		this.name = name;
		this.content = content;
		dateOfPublish = publishDate;
	}

	public String getName() {
		return name;
	}

	public String getContent() {
		return content;
	}

	public LocalDate getDate() {
		return dateOfPublish;
	}

	public String toString() {
		return name + "\n" + "Published on: " + dateOfPublish + "\n" + content;
	}
}
