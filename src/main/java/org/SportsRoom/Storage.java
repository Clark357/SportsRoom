package org.SportsRoom;

import java.io.*;
import java.util.*;
import java.time.*;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Storage {
	private File groupInfo;//TODO will this be needed?
	private final RandomAccessFile raf;
	private static ObjectMapper mapper;
	private boolean isInitialized;

	/**
	 * Constructs a storage with given fileName
	 * @param filePath: path where the storage file exits or where it will be created
	 * @throws FileNotFoundException
	 */
	public Storage(String filePath) throws IOException {
		mapper = new ObjectMapper();
		groupInfo = new File(filePath);
		isInitialized = !groupInfo.createNewFile(); //Checks if the storage already exists or is not
		// yet initialized with initializeStorageFile()
		raf = new RandomAccessFile(groupInfo, "rw");
	}

	/**
	 * Writes given messages into storage, sorted by time
	 * @param input:An array of ChatMessage objects sorted by time
	 */
	public void addMessages(ChatMessage[] input) throws IOException {
		if(!isInitialized) return;
		ChatMessage[] localMessages;
		ChatMessage[] output;

		localMessages = getMessages(input[0].getDate());

		output = mergeMessages(localMessages, input);

		if(output.length != 0){
			raf.seek(raf.length() - 2);
			for(int i = 0; i < output.length; i++){
				getToLineStart();
			}
			for (ChatMessage chatMessage : output) {
				raf.writeBytes(mapper.writeValueAsString(chatMessage) + "\n");
			}
		}

	}

	/**
	 * Creates a new storage file with given users and shared key
	 * @param sharedKey for encryption
	 * @param users of the groupchat
	 * @throws IOException
	 */
	public void initializeStorageFile(Long sharedKey, User[] users) throws IOException {
		raf.seek(0);
		raf.writeBytes(sharedKey + "\n");
		for (User user : users) {
			raf.writeBytes(mapper.writeValueAsString(user) + "\n");
		}
		raf.writeBytes("*****");
		isInitialized = true;
	}

	/**
	 * Reads and returns users in the file
	 * @return users in the file
	 * @throws IOException
	 */
	public ArrayList<User> getUsers() throws IOException {
		if(!isInitialized) return null;
		ArrayList<User> output;
		String temp;

		raf.seek(0);
		raf.readLine(); //Skips the shared key
		output = new ArrayList<>();
		temp = raf.readLine(); // gets the first user

		while(!temp.equals("*****")){
			output.add(mapper.readValue(temp, User.class));
			temp = raf.readLine();
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
	 * @throws IOException
	 */
	public ChatMessage[] getMessages(int amount) throws IOException {
		if(!isInitialized) return null;
		ChatMessage[] output;

		output = new ChatMessage[amount];
		raf.seek(raf.length() - 1);

		for(int i = 0; i < amount; i++){
			getToLineStart();
		}
		for(int k = 0; k < amount; k++){
			output[k] = mapper.readValue(raf.readLine(), ChatMessage.class);
		}
		return output;
	}


	/**
	 * Returns messages sent after given time
	 * @param startingTime messages after this time will be returned
	 * @return messages after given time
	 * @throws IOException
	 */
	public ChatMessage[] getMessages(LocalDateTime startingTime) throws IOException {
		if(!isInitialized) return null;
		int amount;
		String tempString;
		ChatMessage tempMessage;

		raf.seek(raf.length() - 1);
		amount = 0;

		do{
			getToLineStart();
			tempString = raf.readLine();
			tempMessage = mapper.readValue(tempString, ChatMessage.class);
			raf.seek(raf.getFilePointer() - tempString.length() - 1);
			if(tempMessage.getDate().isAfter(startingTime))
				amount++;
		}while (tempMessage.getDate().isAfter(startingTime));

		return getMessages(amount);
	}

	/**
	 * Sets the file pointer to the start of the current line
	 * if used at the start of a line, pointer will be set to the start of the previous line
	 * @throws IOException
	 */
	private void getToLineStart() throws IOException {
		if(!isInitialized) return;
		raf.seek(raf.getFilePointer() - 2);

		while(raf.readByte() != 10){
			raf.seek(raf.getFilePointer() - 2);
		}
	}

	/**
	 * Returns the shared encryption key
	 * @return shared encryption key
	 * @throws IOException
	 */
	public long getSharedKey() throws IOException {
		if(!isInitialized) return -1;
		long pos = raf.getFilePointer();
		long key;

		raf.seek(0);
		key = Long.parseLong(raf.readLine());
		raf.seek(pos);
		return key;
	}

	/**
	 *
	 * @return whether the file exist or not
	 */
	public boolean isInitialized() {
		return isInitialized;
	}
}
