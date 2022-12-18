package org.SportsRoom;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;

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
