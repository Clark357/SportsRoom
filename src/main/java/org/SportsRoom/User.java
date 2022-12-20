package org.SportsRoom;

import java.io.Serializable;

enum Role{
	MODERATOR,
	WRITER,
	READER
}

public class User implements Serializable {

	public Role role;
	private String username;
	private String address;
	

	public User(String username, String address, Role role) {

		setUsername(username);
		setAddress(address);
		this.role = role;
	}
	public User(){}
	public String getUsername() {
		return username;
	}

	public Role getRole() {
		return role;
	}

	public String getAddress() {
		return address;
	}

	public void upgradeRole() {
		switch (role) {
			case WRITER:
				role = Role.MODERATOR;
				break;

			case READER:
				role = Role.WRITER;
				break;

			default:
				break;
		}
	}

	public void downgradeRole() {
		switch (role) {
			case WRITER:
				role = Role.READER;
				break;

			case MODERATOR:
				role = Role.WRITER;
				break;

			default:
				break;
		}
	}

	public void setUsername(String username) {
		this.username = username; //TODO Change according to program requirements
	}

	public void setAddress(String address) {
		this.address = address; //TODO Change according to program requirements
	}

	@Override
	public String toString() {
		return 	role +": " + username + ", '"
				+ address + '\'';
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof User)
			return ((User)obj).getAddress().equals(getAddress());
		return false;
	}
}
