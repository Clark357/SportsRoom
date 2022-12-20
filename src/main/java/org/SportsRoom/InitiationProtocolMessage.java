package org.SportsRoom;

import java.io.*;

public class InitiationProtocolMessage implements Serializable{

	private String groupName;
	private Role role;
	private int numOfUsers;
	private int actualNumOfUsers;
	private long[] primes;

	public InitiationProtocolMessage(String groupName, Role role, int numOfUsers, int actualNumOfUsers, long[] primes) {
		this.groupName = groupName;
		this.role = role;
		this.numOfUsers = numOfUsers;
		this.actualNumOfUsers = actualNumOfUsers;
		this.primes = primes;
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

	public int getActualNumOfUsers() {
		return actualNumOfUsers;
	}

	public long[] getPrimes() {
		return primes;
	}
}
