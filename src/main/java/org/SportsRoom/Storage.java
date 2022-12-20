package org.SportsRoom;

import java.io.*;
import java.util.*;
import java.time.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.*;

public class Storage {
	private RandomAccessFile raf;
	private String fileName;
	private static ObjectMapper mapper;
	private boolean isInitialized;

	/**
	 * Constructs a storage with given fileName
	 * If used for a new group, call initializeStorageFile() right after it
	 * @param fileName: name of the storage file
	 */
	public Storage(String fileName) {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());// For serializing LocalDateTime variables
		this.fileName = fileName;
		File groupInfo = new File("src/info/" + fileName + "info.json");//holds key and users
		File groupMessages = new File("src/data/" + fileName + "data.json");// holds messages
		try {
			isInitialized = !groupInfo.createNewFile() && !groupMessages.createNewFile(); //Checks if the storage already exists or is not
			// yet initialized with initializeStorageFile()
			raf = new RandomAccessFile(groupMessages, "rw");
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
			raf = null;
		}
	}

	/**
	 *
	 * @return A string array with the names of existing files in the data directory that the user can decryption
	 */
	public ArrayList<String> getChatNames(long publicKey, long privateKey){
		File parent = new File("src\\data");
		String[] allChats = parent.list();
		ArrayList<String> output;

		output = new ArrayList<>();

		for (int i = 0; i < Objects.requireNonNull(allChats).length; i++) {
			try {
				RandomAccessFile r = new RandomAccessFile("src\\data\\" + allChats[i], "r");
				long key = getSharedKey(publicKey, privateKey);
				String temp = r.readLine();//get sample message
				ChatMessage auth = mapper.readValue(temp,ChatMessage.class);

				if(Encryption.Decrypt(auth.getContent(), key).equals("This is the start of your conversation"))
					output.add(allChats[i].substring(0, allChats[i].length() - 9));
			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
				System.exit(-1);
			} catch (IOException e) {
				System.err.println(e.getMessage());
				System.exit(-1);
			}

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
	 */
	public void initializeStorageFile(long sharedKey, long publicKey, long privateKey) {
		if(isInitialized) return;

		RandomAccessFile infoRaf;

		try {
			infoRaf = new RandomAccessFile("src/info/" + fileName + "info.json", "rw");
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			infoRaf = null;
			System.exit(-1);
		}
		String hash;
		hash = "" + publicKey + privateKey;
		int hashKey = hash.hashCode();
		if(hashKey < 0) hashKey *= -1;
		hashKey %= 101;
		try {
			infoRaf.seek(0);
			infoRaf.writeBytes(mapper.writeValueAsString(Encryption.Encrypt("" + sharedKey, hashKey))+ "\n");
			infoRaf.close();
			raf.writeBytes(mapper.writeValueAsString(new ChatMessage(LocalDateTime.parse("2000-01-01T01:01:01"), new User("SportsRoom", "0",Role.MODERATOR), Encryption.Encrypt("This is the start of your conversation",sharedKey))) + "\n*****\n");
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

		RandomAccessFile infoRaf;
		ArrayList<User> output;
		String temp;

		output = new ArrayList<>();
		try {
			infoRaf = new RandomAccessFile("src/info/" + fileName + "info.json", "rw");
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			infoRaf = null;
			System.exit(-1);
		}
		try {
			infoRaf.seek(0);
			infoRaf.readLine(); //Skips the shared key
			temp = infoRaf.readLine(); // gets the first user

			while(temp != null) {
				output.add(mapper.readValue(temp, User.class));
				temp = infoRaf.readLine();
			}
			infoRaf.close();
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
		output = new ChatMessage[arr1.length + arr2.length];
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
					break;
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
				if(tempString.charAt(2) == '*') break;
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
	public long getSharedKey(long publicKey, long privateKey) {
		if(!isInitialized) return -1;

		RandomAccessFile infoRaf;
		long key;
		String hash;

		hash = "" + publicKey + privateKey;
		int hashKey = hash.hashCode();
		if(hashKey < 0) hashKey *= -1;
		hashKey %= 101;
		try {
			infoRaf = new RandomAccessFile("src/info/" + fileName + "info.json", "rw");
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			infoRaf = null;
			System.exit(-1);
		}
		try {
			infoRaf.seek(0);
			String temp = infoRaf.readLine();
			String tempFixed = "";
			for (int i = 1; i < temp.length() - 3; i++) {
				if(temp.substring(i, i+2).equals('\\' + "n")){
					tempFixed += "\n";
					i++;
				}
				else {
					tempFixed += temp.charAt(i) ;
				}
			}
			key = Long.parseLong(Encryption.Decrypt(tempFixed +"\n", hashKey));
			infoRaf.close();
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
		if(!isInitialized) return;

		RandomAccessFile infoRaf;

		try {
			infoRaf = new RandomAccessFile("src/info/" + fileName + "info.json", "rw");
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			infoRaf = null;
			System.exit(-1);
		}
		try {
			infoRaf.seek(0);
			infoRaf.readLine();
			for (User user : users) {
				infoRaf.writeBytes(mapper.writeValueAsString(user) + "\n");
			}
			infoRaf.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}

	public static void deleteStorage(String fileName) {
		new File("src/info/" + fileName + "info.json").delete();
		new File("src/data/" + fileName + "data.json").delete();
	}
	public void closeStorage() {
		try {
			raf.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}
	public static void main(String[] args) throws IOException {
		Storage s1 = new Storage("s1");
		long x = 123L;
		long y = 789L;
		User u1 = new User("a", "12",Role.MODERATOR);
		User u2 = new User("b", "13",Role.MODERATOR);
		User u3 = new User("c", "14",Role.MODERATOR);
		s1.initializeStorageFile(234L,x,y);
		s1.updateUsers(new User[]{u1,u2,u3});
		LocalDateTime l = LocalDateTime.of(2022,10,20,20,12,23);

		s1.addMessages(new ChatMessage[]{new ChatMessage(l.minusDays(1),u1,"ilk"),new ChatMessage(l.plusHours(10),u2,"iki"),new ChatMessage(l.plusDays(3),u3,"üç")});

		System.out.println(s1.getSharedKey(x,y));
		System.out.println(Arrays.toString(s1.getMessages(5)));
		System.out.println(Arrays.toString(s1.getMessages(l)));
		System.out.println(s1.getUsers());
		System.out.println(s1.getChatNames(x,y));
		//mapper = new ObjectMapper();
		//mapper.registerModule(new JavaTimeModule());
		//System.out.println(mapper.writeValueAsString(new ChatMessage(LocalDateTime.parse("2000-01-01T01:01:01"), new User("SportsRoom", "0",Role.MODERATOR), Encryption.Encrypt("This is the start of your conversation",5L))));

		//System.out.println(Encryption.Encrypt("This is the start of your conversation", 5));
	}
}
