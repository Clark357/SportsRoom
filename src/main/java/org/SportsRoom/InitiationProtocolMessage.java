package org.SportsRoom;

import java.io.*;

public class InitiationProtocolMessage implements Serializable{

	private String groupName;
	private Role role;
	private int numOfUsers;

	public InitiationProtocolMessage(String groupName, Role role, int numOfUsers) {
		this.groupName = groupName;
		this.role = role;
		this.numOfUsers = numOfUsers;
	}

	public String getGroupName() {
		return groupName;
	}

	public Role getRole() {
		return role;
	}

	public int getNumOfUsers() {
		return numOfUsers;
	}
}
