package org.SportsRoom;

import org.jgroups.*;
import org.jgroups.util.UUID;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.*;

public class Messenger implements Receiver{

	private JChannel superGroup;
	private Storage chatStorage;
	private ArrayList<User> users;
	private ArrayList<ChatMessage> message;
	private MessengerListener listener;
	private User currentClient;

	public void receive(Message msg) { //TODO: Check whether it works
		if(msg.getObject() instanceof ChatMessage) { //TODO: Should be decrypted but for now there is no encryption
			chatStorage.addMessages(new ChatMessage[] {(ChatMessage) msg.getObject()});
			ChatMessage m = (ChatMessage) msg.getObject();
			m.setContent(Encryption.Decrypt(m.getContent(), chatStorage.getSharedKey(MetaSuperGroup.username.hashCode(), MetaSuperGroup.password.hashCode())));
			listener.eventHappened(m);
		} else if(msg.getObject() instanceof RoleUpdateProtocolMessage) {
			users = new ArrayList<>(Arrays.asList(((RoleUpdateProtocolMessage)msg.getObject()).getUsers()));
			listener.eventHappened(users.get(users.indexOf(currentClient)).getRole());
			chatStorage.updateUsers((User[])users.toArray());
		} else if(msg.getObject() instanceof SynchronizationProtocolMessage) {
			if(((SynchronizationProtocolMessage)msg.getObject()).isRequest())
				try {
					superGroup.send(new ObjectMessage(msg.getSrc(), new SynchronizationProtocolMessage(LocalDateTime.now(),
																								false,
																										chatStorage.getMessages(((SynchronizationProtocolMessage)msg.getObject()).getDate()),
																										(User[])chatStorage.getUsers().toArray(),
																										superGroup.getClusterName())));
				} catch(Exception e) {
					System.err.println("Error: Could not send the requested synchronization history.");
				}
			else {
				SynchronizationProtocolMessage history = (SynchronizationProtocolMessage) msg.getObject();

				chatStorage.addMessages(history.getMessages());
				for(ChatMessage c : history.getMessages()) {
					c.setContent(Encryption.Decrypt(c.getContent(), chatStorage.getSharedKey(MetaSuperGroup.username.hashCode(), MetaSuperGroup.password.hashCode())));
					listener.eventHappened(c);
				}

				users = new ArrayList<>(Arrays.asList(((SynchronizationProtocolMessage)msg.getObject()).getUsers()));
				listener.eventHappened(users.get(users.indexOf(currentClient)).getRole());
				chatStorage.updateUsers((User[])users.toArray());
			}
		}
	}

	public void viewAccepted(View v) {
		//TODO: Decide whether a view update needs any action
	}

	// NOTE: It is assumed that whenever this constructor is called the supergroup is already initialized
	public Messenger(String groupName, User client, int numOfUsers, MessengerListener listener) throws Exception { //TODO: Finish the group reconnection constructor by communicating with Storage
		superGroup = new JChannel("src/main/resources/tcp.xml").setName(MetaSuperGroup.metaSuperGroup.getName()).connect(groupName).setReceiver(this);
		this.listener = listener;
		this.currentClient = client;

		chatStorage = new Storage(groupName);
		if(!chatStorage.isInitialized()) {
			chatStorage.initializeStorageFile(-1, (long)MetaSuperGroup.username.hashCode(), (long)MetaSuperGroup.password.hashCode());
		}

		synchronizeHistory();

		for(User u: chatStorage.getUsers())
			listener.eventHappened(u);
	}

	public void synchronizeHistory() throws Exception{
		superGroup.send(new ObjectMessage(superGroup.getView().getMembers().get(0), new SynchronizationProtocolMessage(chatStorage.getMessages(1)[0].getDate(), true, null, null, superGroup.getClusterName())));
	}

	public void sendMessage(ChatMessage msg) throws Exception {
		msg.setContent(Encryption.Encrypt(msg.getContent(), chatStorage.getSharedKey(MetaSuperGroup.username.hashCode(), MetaSuperGroup.password.hashCode())));
		superGroup.send(new ObjectMessage(null, msg));
	}

	public void updateRoles() throws Exception {
		superGroup.send(new ObjectMessage(null, new RoleUpdateProtocolMessage((User[])users.toArray())));
	}

	public ArrayList<User> getUsers() {
		return users;
	}
}

class MetaSuperGroup implements Receiver { //TODO: Group creation function that uses encryption initiator
	static JChannel metaSuperGroup;
	private static MainWindow w;
	public static String username;
	public static String password;

	public static void initMetaSuperGroup(String usernameInput, String passwordInput, MainWindow window) {
		username = usernameInput;
		password = passwordInput;
		w = window;
		try {
			metaSuperGroup = new JChannel("src/main/resources/tcp.xml").setName(username).setReceiver(new MetaSuperGroup()).connect("MetaSuperGroup");

			JMenu about = new JMenu("About");
			about.add(new JMenuItem(new AbstractAction("Copy UUID") {
				public void actionPerformed(ActionEvent e) {
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
							new StringSelection(metaSuperGroup.getAddressAsUUID()),
							new ClipboardOwner() {
								public void lostOwnership(Clipboard clipboard, Transferable contents) {
									//NOTE: We do nothing when we lose the ownership
								}
							}
					);
				}
			}));
			w.getBar().add(about);
		} catch(Exception e) {
			System.err.println("Fatal Error: Could not connect to MetaSuperGroup.");
			System.exit(-1);
		}
	}

	public static long initSuperGroupKey(String groupName, User[] users, String username, String password, int numOfActualUsers) {//TODO: Implement this method
		final long[] sharedKeyReceived = new long[1];

		try {
			new EncryptionInitiator(groupName, users.length, numOfActualUsers, new EncryptionInitiatorListener() {
				public void keyCreated(long sharedKey) {
					sharedKeyReceived[0] = sharedKey;
				}
			});
		} catch (Exception e) {
			System.err.println("Fatal error: Could not initialize the SuperGroup.");
			System.exit(-1);
		}

		return sharedKeyReceived[0];
	}

	public static void initPeerToPeer(String groupName, User[] chatters, User[] storages, MainWindow w) {
		initSuperGroupKey(groupName, chatters, username, password, storages.length);
		for(User u : storages) {
			try {
				metaSuperGroup.send(new ObjectMessage(UUID.fromString(u.getAddress()), new InitiationProtocolMessage(groupName, u.getRole(), storages.length)));
			} catch (Exception e) {
				System.err.println("Fatal error: Could not initialize a peer-to-peer group.");
				System.exit(-1);
			}
		}
	}

	public static void initGroupChat(String groupName, User[] users, MainWindow w) {
		initSuperGroupKey(groupName, users, username, password, users.length);
		for(User u : users) {
			try {
				metaSuperGroup.send(new ObjectMessage(UUID.fromString(u.getAddress()), new InitiationProtocolMessage(groupName, u.getRole(), users.length)));
			} catch (Exception e) {
				System.err.println("Fatal error: Could not initialize a Group Chat group.");
				System.exit(-1);
			}
		}
	}

	public static void initChannel(String groupName, User[] users, MainWindow w) {
		initSuperGroupKey(groupName, users, username, password, users.length);
		for(User u : users) {
			try {
				metaSuperGroup.send(new ObjectMessage(UUID.fromString(u.getAddress()), new InitiationProtocolMessage(groupName, u.getRole(), users.length)));
			} catch (Exception e) {
				System.err.println("Fatal error: Could not initialize a Group Chat group.");
				System.exit(-1);
			}
		}
	}

	public void receive(Message msg) {
		InitiationProtocolMessage m = (InitiationProtocolMessage) msg.getObject();

		w.getPanel().addChatPanel(new MainWindow.ChatPanel(m.getGroupName(), new User(metaSuperGroup.getName(), metaSuperGroup.getAddressAsUUID(), m.getRole()), m.getNumOfUsers()));
	}

	public void viewAccepted(View v) {
		//TODO: Decide whether a view update needs any action
	}
}
