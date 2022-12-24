package org.SportsRoom;

public class Parser {

	public static String parse(String message) {
		if(!message.contains("\\") || !message.contains("{") || !message.contains("}")) return message;

		int commandStartIndex = message.indexOf('\\');
		int commandEndIndex = message.indexOf('}', commandStartIndex);
		String command = message.substring(commandStartIndex + 1, message.indexOf('{', commandStartIndex));
		if(command.contains(" ")) return parse(message.substring(commandStartIndex + 1));
		String parameter = message.substring(message.indexOf('{', commandStartIndex) + 1, commandEndIndex);

		switch (command) {
			case "avgPts":
				if(commandEndIndex + 1 < message.length())
					return message.substring(0, commandStartIndex)
						+ "<b color=\"red\">" + Stats.getPts(Stats.returnPlayerID(parameter)) + "</b color=\"red\">"
						+ parse(message.substring(commandEndIndex + 1));
				else return message.substring(0, commandStartIndex)
						+ "<b color=\"red\">" + Stats.getPts(Stats.returnPlayerID(parameter)) + "</b color=\"red\">";
			case "avgAst":
				if(commandEndIndex + 1 < message.length())
					return message.substring(0, commandStartIndex)
							+ "<b color=\"red\">" + Stats.getAst(Stats.returnPlayerID(parameter)) + "</b color=\"red\">"
							+ parse(message.substring(commandEndIndex + 1));
				else return message.substring(0, commandStartIndex)
						+ "<b color=\"red\">" + Stats.getAst(Stats.returnPlayerID(parameter)) + "</b color=\"red\">";
			case "gamesPlayed":
				if(commandEndIndex + 1 < message.length())
					return message.substring(0, commandStartIndex)
							+ "<b color=\"red\">" + Stats.getGP(Stats.returnPlayerID(parameter)) + "</b color=\"red\">"
							+ parse(message.substring(commandEndIndex + 1));
				else return message.substring(0, commandStartIndex)
						+ "<b color=\"red\">" + Stats.getGP(Stats.returnPlayerID(parameter)) + "</b color=\"red\">";
			case "avgBlk":
				if(commandEndIndex + 1 < message.length())
					return message.substring(0, commandStartIndex)
							+ "<b color=\"red\">" + Stats.getBlk(Stats.returnPlayerID(parameter)) + "</b color=\"red\">"
							+ parse(message.substring(commandEndIndex + 1));
				else return message.substring(0, commandStartIndex)
						+ "<b color=\"red\">" + Stats.getBlk(Stats.returnPlayerID(parameter)) + "</b color=\"red\">";
			default:
				return message.substring(0, commandEndIndex + 1)
						+ parse(message.substring(commandEndIndex + 1));
		}
	}

}
