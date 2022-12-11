package SportsRoom;
import java.util.*;


public class News {

	private ArrayList<NewsArticle> news;

	private void addArticle(NewsArticle n) {
		news.add(n);
	}

	public ArrayList<NewsArticle> getAllNews() {
		return news;
	}

	/**
	 * TODO importNews method will utilize official NBA News API and HttpClient to import the news from official NBA site and turn it into NewsArticle objects
	 */
	public void importNews(){
		
	}

}
