package org.SportsRoom;

import java.io.Serializable;

enum Role{
	MODERATOR,
	WRITER,
	READER
}

public class User implements Serializable {

	public Role role;
	private String name;
	private String address;
	

	public User(String name, String address, boolean canSubmit) {

		setName(name);
		setAddress(address);
		if(canSubmit)
			role = Role.WRITER;
		else
			role = Role.READER;
	}
	public User(){}
	public String getName() {
		return name;
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

	public void setName(String name) {
		this.name = name; //TODO Change according to program requirements
	}

	public void setAddress(String address) {
		this.address = address; //TODO Change according to program requirements
	}

	@Override
	public String toString() {
		return 	role +": " + name + ", \'"
				+ ip + '\'';
	}
}
