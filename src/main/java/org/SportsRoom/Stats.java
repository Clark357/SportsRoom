package org.SportsRoom;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Stats {

	private static JSONObject getPlayerStats(int playerID) {
		String urlString = "https://www.balldontlie.io/api/v1/season_averages?player_ids[]=" + playerID;
		JSONObject jsonObj = new JSONObject();
		try {
			URL url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			int responseCode = con.getResponseCode();
			if (responseCode != 200) {
				throw new RuntimeException("HttpResponseCode: " + responseCode);
			} else {
				Scanner read = new Scanner(url.openStream());
				StringBuilder jsonString = new StringBuilder();
				while (read.hasNextLine()) {
					jsonString.append(read.nextLine());
				}
				JSONParser parser = new JSONParser();
				jsonObj = (JSONObject) parser.parse(jsonString.toString());
				JSONArray jsonArray = (JSONArray) jsonObj.get("data");
				jsonObj = (JSONObject) jsonArray.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObj;
	}

	public static String getPts(int playerID) {
		JSONObject jsonObj = getPlayerStats(playerID);
		String pts = jsonObj.get("pts").toString();
		return pts;
	}

	public static String getAst(int playerID) {
		JSONObject jsonObj = getPlayerStats(playerID);
		String ast = jsonObj.get("ast").toString();
		return ast;
	}

	public static String getGP(int playerID) {
		JSONObject jsonObj = getPlayerStats(playerID);
		String gp = jsonObj.get("games_played").toString();
		return gp;
	}

	public static String getBlk(int playerID) {
		JSONObject jsonObj = getPlayerStats(playerID);
		String blk = jsonObj.get("blk").toString();
		return blk;
	}

	public static String getTeam(int playerID) {

		String urlString = "https://www.balldontlie.io/api/v1/players/" + playerID;
		String teamName = "";
		try {
			URL url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			int responseCode = con.getResponseCode();
			if (responseCode != 200) {
				throw new RuntimeException("HttpResponseCode: " + responseCode);
			} else {
				Scanner read = new Scanner(url.openStream());
				StringBuilder JSONString = new StringBuilder();
				while (read.hasNextLine()) {
					JSONString.append(read.nextLine());
				}
				JSONParser parser = new JSONParser();
				JSONObject jsonObj = (JSONObject) parser.parse(JSONString.toString());
				jsonObj = (JSONObject) jsonObj.get("team");
				teamName = jsonObj.get("name").toString();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return teamName;
	}

	public static String getPlayerName(int playerID) {
		String urlString = "https://www.balldontlie.io/api/v1/players/" + playerID;
		String playerName = "";
		try {
			URL url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			int responseCode = con.getResponseCode();
			if (responseCode != 200) {
				throw new RuntimeException("HttpResponseCode: " + responseCode);
			} else {
				Scanner read = new Scanner(url.openStream());
				StringBuilder JSONString = new StringBuilder();
				while (read.hasNextLine()) {
					JSONString.append(read.nextLine());
				}
				JSONParser parser = new JSONParser();
				JSONObject jsonObj = (JSONObject) parser.parse(JSONString.toString());
				playerName = jsonObj.get("first_name").toString() + " " + jsonObj.get("last_name").toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return playerName;
	}

	private static JSONObject gameConnection(int gameID) {
		String urlString = "https://www.balldontlie.io/api/v1/games/" + gameID;
		JSONObject jsonObj = new JSONObject();
		try {
			URL url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			int responseCode = con.getResponseCode();
			if (responseCode != 200) {
				throw new RuntimeException("HttpResponseCode: " + responseCode);
			} else {
				Scanner read = new Scanner(url.openStream());
				StringBuilder JSONString = new StringBuilder();
				while (read.hasNextLine()) {
					JSONString.append(read.nextLine());
				}
				JSONParser parser = new JSONParser();
				jsonObj = (JSONObject) parser.parse(JSONString.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObj;
	}

	public static String getGameState(int gameID) {
		JSONObject jsonObj = gameConnection(gameID);
		String gameState = jsonObj.get("status").toString();
		return gameState;
	}

	public static String getGameScore(int gameID) {
		JSONObject jsonObj = gameConnection(gameID);
		String gameScore = jsonObj.get("home_team_score").toString() + "-" + jsonObj.get("visitor_team_score").toString();
		return gameScore;
	}

	public static String getWinner(int gameID) {
		JSONObject jsonObj = gameConnection(gameID);
		String winner;
		Long homeTeamScore = (Long) jsonObj.get("home_team_score");
		Long visitorTeamScore = (Long) jsonObj.get("visitor_team_score");
		if (homeTeamScore > visitorTeamScore) {
			jsonObj = (JSONObject) jsonObj.get("home_team");
			winner = jsonObj.get("name").toString();
		} else if (visitorTeamScore > homeTeamScore) {
			jsonObj = (JSONObject) jsonObj.get("visitor_team");
			winner = jsonObj.get("name").toString();
		} else {
			winner = "No winner";
		}
		return winner;
	}

	public static int returnPlayerID(String playerName) {
		String urlString = "https://www.balldontlie.io/api/v1/players?search=" + playerName;
		int playerID = -1;
		try {
			URL url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			playerID = 237;

			//Warning: Does not work
//			con.connect();
//			int responseCode = con.getResponseCode();
//			if (responseCode != 200) {
//				throw new RuntimeException("HttpResponseCode: " + responseCode);
//			} else {
//				Scanner read = new Scanner(url.openStream());
//				StringBuilder JSONString = new StringBuilder();
//				while (read.hasNextLine()) {
//					JSONString.append(read.nextLine());
//				}
//				JSONParser parser = new JSONParser();
//				JSONObject jsonObj = (JSONObject) parser.parse(JSONString.toString());
//				JSONArray data = (JSONArray) jsonObj.get("data");
//				jsonObj = (JSONObject) data.get(0);
//				playerID = (int) jsonObj.get("id");
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return playerID;
	}
}
