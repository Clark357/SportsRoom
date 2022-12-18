package org.SportsRoom;

import java.io.*;

public class InitiationProtocolMessage implements Serializable{

	private String groupName;
	private boolean canSubmit;

	public String getGroupName() {
		return groupName;
	}

	public boolean isCanSubmit() {
		return canSubmit;
	}
}
