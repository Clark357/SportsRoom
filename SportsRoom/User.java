package SportsRoom;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;

enum Role{
	WRITER,
	MODERATOR,
	READER
}

public class User {

	public Role role;
	private String name;
	private String ip;
	

	public User(String name, String ip) {

		setName(name);
		setIP(ip);
		role = Role.READER;
	}

	public String getUsername() {
		return name;
	}

	public String getIP() {
		return ip;
	}

	public void setName(String name) {
		this.name = name; //TODO Change according to program requirements
	}

	public void setIP(String ip) {
		this.ip = ip; //TODO Change according to program requirements
	}
}
