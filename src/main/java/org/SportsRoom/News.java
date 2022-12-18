package org.SportsRoom;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import javax.print.DocFlavor;

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
			URL url = new URL("https://www.nba.com/news");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			int responseCode = con.getResponseCode();
			if(responseCode != 200){
				throw new RuntimeException("HttpResponseCode: " + responseCode);
			}
			else{
				Scanner scan = new Scanner(url.openStream());
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

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String writeHtml() {
		StringBuilder htmlString = new StringBuilder("<!DOCTYPE html>\n" +
				"<html lang =\"en\">\n" +
				"<head>\n" +
				"<title>Latest NBA News</title>\n" +
				"<body>\n" +
				"<main>\n" +
				"<h1>Latest NBA News<h1>\n");
		for(int i = 0; i < news.size(); i++){
			String section = "<section>\n"+
					"<h2>" +  "<a target=\"_blank\" href=\""+ news.get(i).getPermaLink() + "\">" + news.get(i).getName() + "</a></h2>\n"+
					"<p>" + news.get(i).getContent() + "<img src=\""+news.get(i).getImgLink()+"\"></p>\n"+
					"</section>\n"
					;
			htmlString.append(section);
		}
		htmlString.append("</main>\n</body>\n</head>\n</html>");
		return htmlString.toString();
	}
	
}
