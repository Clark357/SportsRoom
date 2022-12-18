package org.SportsRoom;

import org.jgroups.*;

import java.time.LocalDateTime;
import java.util.*;

public class Messenger implements Receiver{

	private JChannel superGroup;
	private Storage chatStorage;
	private ArrayList<User> users;
	private ArrayList<ChatMessage> message;
	private MessengerListener listener;

	public void receive(Message msg) { //TODO: Check whether it works
		if(msg.getObject() instanceof ChatMessage) { //TODO: Should be decrypted but for now there is no encryption
			listener.eventHappened(msg.getObject());
			chatStorage.addMessages(new ChatMessage[] {(ChatMessage) msg.getObject()});
		} else if(msg.getObject() instanceof RoleUpdateProtocolMessage) {
			users = new ArrayList<>(Arrays.asList(((RoleUpdateProtocolMessage)msg.getObject()).getUsers()));
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
				for(ChatMessage c : history.getMessages())
					listener.eventHappened(c);
				//TODO:Update user information
			}
		}
	}

	public void viewAccepted(View v) {
		//TODO: Decide whether a view update needs any action
	}

	// NOTE: It is assumed that whenever this constructor is called the supergroup is already initialized
	public Messenger(String groupName ,MessengerListener listener) throws Exception { //TODO: Finish the group reconnection constructor by communicating with Storage
		superGroup = new JChannel().setName(MetaSuperGroup.metaSuperGroup.getName()).connect(groupName).setReceiver(this);
		this.listener = listener;

		chatStorage = new Storage(groupName);

		synchronizeHistory();

		for(User u: chatStorage.getUsers())
			listener.eventHappened(u);
	}

	public void synchronizeHistory() throws Exception{
		superGroup.send(new ObjectMessage(superGroup.getView().getMembers().get(0), new SynchronizationProtocolMessage(chatStorage.getMessages(1)[0].getDate(), true, null, null, superGroup.getClusterName())));
	}

	public void sendMessage(ChatMessage msg) throws Exception {
		superGroup.send(new ObjectMessage(null, msg));
	}

	public void updateRoles() throws Exception {
		superGroup.send(new ObjectMessage(null, new RoleUpdateProtocolMessage((User[])users.toArray())));
	}

	private void createSuperGroup() {
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	public ArrayList<User> getUsers() {
		return users;
	}
}

class MetaSuperGroup implements Receiver { //TODO: Group creation function that uses encryption initiator
	static JChannel metaSuperGroup;
	private static MainWindow w;

	public static void initMetaSuperGroup(String username, MainWindow window) {
		w = window;
		try {
			metaSuperGroup = new JChannel().setName(username).setReceiver(new MetaSuperGroup()).connect("MetaSuperGroup");
		} catch(Exception e) {
			System.err.println("Fatal Error: Could not connect to MetaSuperGroup.");
			System.exit(-1);
		}
	}

	public static void initSuperGroup(String groupName, User[] users, String username, String password) {//TODO: Implement this method
		throw new UnsupportedOperationException("This will be implemented when EncryptionInitiator class is implemented");
	}

	public void receive(Message msg) {
		InitiationProtocolMessage m = (InitiationProtocolMessage) msg.getObject();

		w.getPanel().addChatPanel(new MainWindow.ChatPanel(m.getGroupName(), m.isCanSubmit(), new User(metaSuperGroup.getName(), metaSuperGroup.getAddressAsUUID(), m.isCanSubmit())));
	}

	public void viewAccepted(View v) {
		//TODO: Decide whether a view update needs any action
	}
}
