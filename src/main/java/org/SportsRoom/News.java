package org.SportsRoom;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class News {

	private ArrayList<NewsArticle> news;

	public News(){
		news = new ArrayList<NewsArticle>();
		importNews();
	}

	private void addArticle(NewsArticle n) {
		news.add(n);
	}

	public ArrayList<NewsArticle> getAllNews() {
		return news;
	}

	public void importNews() {
		try {
			HttpClient con = HttpClient.newBuilder()
					.version(HttpClient.Version.HTTP_2)
					.followRedirects(HttpClient.Redirect.NORMAL)
					.connectTimeout(Duration.ofSeconds(20))
					.build();
			HttpResponse<String> response= con.send(HttpRequest.newBuilder(new URI("https://www.nba.com/news")).GET().timeout(Duration.ofMinutes(2)).build(), HttpResponse.BodyHandlers.ofString());

			int responseCode = response.statusCode();
			if(responseCode != 200){
				throw new RuntimeException("HttpResponseCode: " + responseCode);
			}
			else{
				Scanner scan = new Scanner(response.body());
				StringBuilder htmlString = new StringBuilder();
				while(scan.hasNextLine()) {
					htmlString.append(scan.nextLine());
				}
				Document html = Jsoup.parse(htmlString.toString());
				Elements jsonElements = html.body().getElementsByTag("script");
				String jsonString = jsonElements.get(2).toString();
				jsonString = jsonString.substring(51, jsonString.length() - 9);
				JSONParser parser = new JSONParser();
				JSONObject jsonObj = (JSONObject) parser.parse(jsonString);
				jsonObj = (JSONObject) jsonObj.get("props");
				jsonObj = (JSONObject) jsonObj.get("pageProps");
				jsonObj = (JSONObject) jsonObj.get("latest");
				JSONArray jsonNewsArray = (JSONArray) jsonObj.get("items");
				for (int i = 0; i < jsonNewsArray.size(); i++){
					String name = (String) ((JSONObject)jsonNewsArray.get(i)).get("title");
					String content = (String) ((JSONObject)jsonNewsArray.get(i)).get("excerpt");
					String imgLink = (String) ((JSONObject)jsonNewsArray.get(i)).get("featuredImage");
					String permaLink = (String) ((JSONObject)jsonNewsArray.get(i)).get("permalink");
					String publishDate = (String) ((JSONObject)jsonNewsArray.get(i)).get("date");
					NewsArticle article = new NewsArticle(name, content, imgLink, permaLink, publishDate);
					addArticle(article);
				}
				scan.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String writeHtml(){
		StringBuilder htmlString = new StringBuilder("<!DOCTYPE html>\n" +
				"<html lang =\"en\">\n" +
				"<head>\n" +
				"<title>Latest NBA News</title>\n" +
				"<body>\n" +
				"<main>\n" +
				"<h1>Latest NBA News<h1>\n");
		for(int i = 0; i < news.size(); i++){
			String section = "<table>\n" +
					"<tr>\n" +
					"<td><img src=\""+news.get(i).getImgLink()+"\" width=\"200\" height=\"120\"></td>\n" +
					"<td style=\"font-family: system-ui\"><a target=\"_blank\" href=\""+ news.get(i).getPermaLink()+"\">" + news.get(i).getName() +
					"</a><p>Published on:"+news.get(i).getDate()+"</p><p>"+ news.get(i).getContent() + "</p></td>"+
					"</tr>\n</table>\n";
			htmlString.append(section);
		}
		htmlString.append("</main>\n</body>\n</head>\n</html>");
		return htmlString.toString();
	}
	
}
