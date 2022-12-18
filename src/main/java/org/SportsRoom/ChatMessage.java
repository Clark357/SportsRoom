package org.SportsRoom;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.time.*;

public class ChatMessage {

	private LocalDateTime date;
	private User user;
	private String content;

	public ChatMessage(LocalDateTime date, User user, String content) {
		this.date = date;
		this.user = user;
		this.content = content;
	}
	public ChatMessage(){}

	public LocalDateTime getDate() {
		return date;
	}

	public User getUser() {
		return user;
	}

	public String getContent() {
		return content;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return 	"[" + date +
				"]" + user +
				": " + content;
	}
}
