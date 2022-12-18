package org.SportsRoom;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.time.*;

public class SynchronizationProtocolMessage implements Serializable{
	private LocalDateTime date;
	private boolean isRequest;
	private ChatMessage[] messages;
	private User[] users;
	private String groupName;

	public SynchronizationProtocolMessage(LocalDateTime date, boolean isRequest, ChatMessage[] messages, User[] users, String groupName) {
		this.date = date;
		this.isRequest = isRequest;
		this.messages = messages;
		this.users = users;
		this.groupName = groupName;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public boolean isRequest() {
		return isRequest;
	}

	public ChatMessage[] getMessages() {
		return messages;
	}

	public String getGroupName() {
		return groupName;
	}

	public User[] getUsers() {
		return users;
	}
}
