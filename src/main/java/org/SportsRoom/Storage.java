package org.SportsRoom;

import java.io.*;
import java.util.*;
import java.time.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Storage {
	private RandomAccessFile raf;
	private static ObjectMapper mapper;
	private boolean isInitialized;

	/**
	 * Constructs a storage with given fileName
	 * @param fileName: name of the storage file
	 */
	public Storage(String fileName) {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());// For serializing LocalDateTime variables
		File groupInfo = new File("src/data/" + fileName + ".json");
		try {
			isInitialized = !groupInfo.createNewFile(); //Checks if the storage already exists or is not
			// yet initialized with initializeStorageFile()
			raf = new RandomAccessFile(groupInfo, "rw");
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
			raf = null;
		}
	}

	/**
	 *
	 * @return A string array with the names of existing files in the data directory
	 */
	public String[] getChatNames(){
		File parent = new File("src\\data");
		String[] output = parent.list();

		for (int i = 0; i < Objects.requireNonNull(output).length; i++) {
			output[i] = output[i].substring(0,output[i].length()-5);
		}
		return  output;
	}

	/**
	 * Writes given messages into storage, sorted by time
	 * @param input:An array of ChatMessage objects sorted by time
	 */
	public void addMessages(ChatMessage[] input) {
		if(!isInitialized) return;
		ChatMessage[] localMessages;
		ChatMessage[] output;

		localMessages = getMessages(input[0].getDate());

		output = mergeMessages(localMessages, input);

		try{
			if(Objects.requireNonNull(output).length != 0){
				raf.seek(raf.length());
				raf.writeBytes("\n");
				for(int i = 0; i < localMessages.length; i++){
					getToLineStart();
				}
				for (ChatMessage chatMessage : output) {
					raf.writeBytes(mapper.writeValueAsString(chatMessage) + "\n");
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}

	}

	/**
	 * Creates a new storage file with given users and shared key
	 * @param sharedKey for encryption
	 * @param users of the group chat
	 */
	public void initializeStorageFile(Long sharedKey, User[] users) {
		if(isInitialized) return;

		try {
			raf.seek(0);
			raf.writeBytes(sharedKey + "\n");
			for (User user : users) {
				raf.writeBytes(mapper.writeValueAsString(user) + "\n");
			}
			raf.writeBytes("*****");
			raf.writeBytes("\n" + mapper.writeValueAsString(new ChatMessage(LocalDateTime.parse("2000-01-01T01:01:01"), new User("SportsRoom", "0",true), "This is the start of your conversation")));
			isInitialized = true;
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}

	/**
	 * Reads and returns users in the file
	 * @return users in the file
	 */
	public ArrayList<User> getUsers() {
		if(!isInitialized) return null;
		ArrayList<User> output;
		String temp;
		output = new ArrayList<>();

		try {
			raf.seek(0);
			raf.readLine(); //Skips the shared key
			temp = raf.readLine(); // gets the first user

			while(!temp.equals("*****")) {
				output.add(mapper.readValue(temp, User.class));
				temp = raf.readLine();
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		return output;
	}

	/**
	 * Merges 2 given sorted ChatMessage arrays into 1, sorted by time
	 * @param arr1 array to be merged
	 * @param arr2 array to be merged
	 * @return sorted combination of given arrays
	 */
	private ChatMessage[] mergeMessages(ChatMessage[] arr1, ChatMessage[] arr2) {
		if(!isInitialized) return null;
		int index1;
		int index2;
		ChatMessage[] output;

		index1 = 0;
		index2 = 0;
		output = new ChatMessage[arr1.length + arr2.length - 1];
		if(output.length == 0) return output;
		if (arr2.length == 0) return arr1;
		if(arr1.length == 0) return arr2;

		for(int i = 0; i < output.length; i++){
			if(index2 == -1 || (index1 != -1 && arr1[index1].getDate().isBefore(arr2[index2].getDate()))){
				output[i] = arr1[index1];
				index1++;
				if(index1 >= arr1.length) index1 = -1;
			}else {
				output[i] = arr2[index2];
				index2++;
				if(index2 >= arr2.length) index2 = -1;
			}
		}
		return output;
	}

	/**
	 * Returns given amount of the latest messages, sorted by time
	 * @param amount of messages requested
	 * @return requested messages
	 */
	public ChatMessage[] getMessages(int amount) {
		if(!isInitialized) return null;
		ChatMessage[] output;
		String temp;
		try {
			raf.seek(raf.length() - 1);

			for(int i = 0; i < amount; i++){
				getToLineStart();
				temp = raf.readLine();
				if(temp.charAt(2) == '*') {
					amount = i;
					break; //TODO can we use break statements?
				}
				raf.seek(raf.getFilePointer() - temp.length() - 1);
			}
			output = new ChatMessage[amount];
			for(int k = 0; k < amount; k++){
				output[k] = mapper.readValue(raf.readLine(), ChatMessage.class);
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
			output = new ChatMessage[amount];
		}
		return output;
	}


	/**
	 * Returns messages sent after given time
	 * @param startingTime messages after this time will be returned
	 * @return messages after given time
	 */
	public ChatMessage[] getMessages(LocalDateTime startingTime) {
		if(!isInitialized) return null;
		int amount;
		String tempString;
		ChatMessage tempMessage;

		amount = 0;

		try{
			raf.seek(raf.length() - 1);

			do{
				getToLineStart();
				tempString = raf.readLine();
				tempMessage = mapper.readValue(tempString, ChatMessage.class);
				raf.seek(raf.getFilePointer() - tempString.length() - 1);
				if(tempMessage.getDate().isAfter(startingTime))
					amount++;
			}while (tempMessage.getDate().isAfter(startingTime));

		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		return getMessages(amount);
	}

	/**
	 * Sets the file pointer to the start of the current line
	 * if used at the start of a line, pointer will be set to the start of the previous line
	 */
	private void getToLineStart() {
		try {
			if(!isInitialized) return;

			raf.seek(raf.getFilePointer() - 2);
			while(raf.readByte() != 10){
				raf.seek(raf.getFilePointer() - 2);
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}


	}

	/**
	 * Returns the shared encryption key
	 * @return shared encryption key
	 */
	public long getSharedKey() {
		if(!isInitialized) return -1;

		long key;
		try {
			long pos = raf.getFilePointer();

			raf.seek(0);
			key = Long.parseLong(raf.readLine());
			raf.seek(pos);
		} catch (IOException e) {
			key = 0;
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		return key;
	}

	/**
	 *
	 * @return whether the file exist or not
	 */
	public boolean isInitialized() {
		return isInitialized;
	}

	/**
	 * Writes the given users into the storage file
	 * @param users to be written, only the same amount of users as before
	 */
	public void updateUsers(User[] users) {
		long key = getSharedKey();
		isInitialized = false;
		initializeStorageFile(key, users);
	}

}
