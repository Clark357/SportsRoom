package org.SportsRoom;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.time.*;;
import com.fasterxml.jackson.*;

public class Storage {

	private ArrayList<ChatMessage> messages;
	private File messageFile;
	private File groupInfo;

	public void addMessages(ArrayList<ChatMessage> newMessages) {
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	public ArrayList<ChatMessage> getMessages(int amount) {
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	public ArrayList<ChatMessage> getMessages(LocalDateTime startingTime) {
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	public long getSharedKey() {
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	public void setSharedKey(long sharedKey) {
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	public ArrayList<User> getUsers() {
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

}
