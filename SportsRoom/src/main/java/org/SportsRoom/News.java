package org.SportsRoom;
import java.net.HttpURLConnection;
import java.net.URL;
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
	public void importNews() {
		try {
			URL url = new URL("https://www.nba.com/news");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			int responseCode = conn.getResponseCode();
			if(responseCode != 200){
				throw new RuntimeException("HttpResponseCode: " + responseCode);
			}
			else{
				Scanner scan = new Scanner(url.openStream());
				while(scan.hasNextLine()){
					String line = scan.nextLine();
					System.out.println(line);
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	public static void main(String[] args) {
		News a = new News();
		a.importNews();
	}

}
