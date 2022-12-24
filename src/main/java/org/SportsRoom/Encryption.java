package org.SportsRoom;

import java.util.ArrayList;

public class Encryption {

	public static String Encrypt(String message, long sharedKey) {
		char[] allChars = message.toCharArray();
		String result = "";
        for(char x: allChars){
         	int y = x;
        	result=result+(y*sharedKey)+"\n";
		}
		return result;
	}

	public static String Decrypt(String message, long sharedKey) {
		String[] lol = message.split("\n");
		String result ="";
		for(String k : lol){
			char x = (char)((Long.parseLong(k))/sharedKey);
			result += x;
		}
		return result;
	}

}
