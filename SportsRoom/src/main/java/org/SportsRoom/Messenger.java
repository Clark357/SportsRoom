package org.SportsRoom;

import org.jgroups.JChannel;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;

public class Messenger {

	private JChannel superGroup;
	private Storage chatStorage;
	private ArrayList<User> users;
	private ArrayList<ChatMessage> message;
	private MessengerListener listener;

	public Messenger(String groupName ,MessengerListener listener) {
		this.listener = listener;
	}

	public void synchronizeHistory() {
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	public void sendMessage() {
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	private void createSuperGroup() {
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	public ArrayList<User> getUsers() {
		return users;
	}
}
