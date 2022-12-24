package org.SportsRoom;

import com.sun.tools.javac.Main;
import org.jgroups.demos.Chat;
import org.jgroups.util.UUID;

import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MainWindow extends JFrame{
	private MainMenu panel;
	private JMenuBar bar;
	private JMenu leave;

	public MainWindow() { //TODO: Test Whether this is the best way to construct the window
		super("SportsRoom");

		panel = new MainMenu();

		setVisible(false);
		setContentPane(panel.getPanel());
		setPreferredSize(new Dimension(600, 400));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SwingUtilities.updateComponentTreeUI(this);
		pack();

		bar = new JMenuBar();
		JMenu edit = new JMenu("Edit");
		leave = new JMenu("Leave Chats");

		MainWindow w = this;
		edit.add(new AbstractAction("Create New...") {
			public void actionPerformed(ActionEvent e) {
				new CreateNew(panel.getGroups(), w);
			}
		});

		//TODO:These are for demonstration purposes. This should be automated to search for all the groups
//		leave.add("LeBron Channel");
//		leave.add("Isaac");
//		leave.add("Group Chat");

		edit.add(new AbstractAction("Manage Groups") {
			public void actionPerformed(ActionEvent e) {
				new ManageGroups(panel.getGroups());
			}
		});
		edit.add(leave);
		bar.add("Edit", edit);
		setJMenuBar(bar);

		new LoginWindow(this);
	}

	public MainWindow(MainMenu m) { //TODO: Open the previously opened groups by communicating with the Storage class
		this();
		setContentPane(m.getPanel());
		panel = m;
	}

	public MainMenu getPanel() {
		return panel;
	}

	public JMenuBar getBar() {
		return bar;
	}

	public JMenu getLeave() {
		return leave;
	}

	public void initChatPanels() {
		for(Storage s : Storage.getChatNames(MetaSuperGroup.username.hashCode(), MetaSuperGroup.password.hashCode())) {
			Role role = null;
			for(User u : s.getUsers())
				if(u.getUsername().equals(MetaSuperGroup.username))
					role = u.getRole();
			if(role != null)
				getPanel().addChatPanel(new ChatPanel(s.getFileName(), new User(MetaSuperGroup.username, MetaSuperGroup.metaSuperGroup.getAddressAsUUID(), role), s.getUsers().size(), this));
		}
	}

	public void removeLeave(String groupName) {
		int indexOfTab = -1;
		for(int i = 0; i < getPanel().groups.size(); i++)
			if(getPanel().groups.get(i).groupName.equals(groupName))
				indexOfTab = i;
		leave.remove(indexOfTab);
	}

	public static class MainMenu {

		private JPanel panel1;
		private JTabbedPane Tabs;
		private JScrollPane NewsScroller;
		private JTextPane NewsHTML;
		private News newsAggregator;
		private ArrayList<ChatPanel> groups;

		public MainMenu() {
			groups = new ArrayList<>();
		}

		public void initNews() {
			newsAggregator = new News();
			newsAggregator.importNews();
			NewsHTML.setText(newsAggregator.writeHtml());
			NewsHTML.setCaretPosition(0);
			NewsHTML.addHyperlinkListener(new HyperlinkListener() {
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
						try {
							Desktop.getDesktop().browse(e.getURL().toURI());
						} catch (URISyntaxException | IOException ex) {
							System.err.println("Error: Could not open the link the user clicked.");
						}
				}
			});
		}

		public void addChatPanel(ChatPanel panel) {
			Tabs.addTab(panel.getGroupName(), panel.getPanel());
			groups.add(panel);
		}

		public void removeChatPanel(String panelName) {
			int indexOfTab = -1;
			for(int i = 0; i < groups.size(); i++)
				if(groups.get(i).groupName.equals(panelName))
					indexOfTab = i;
			if(indexOfTab != -1) {
				groups.get(indexOfTab).close();
				Tabs.remove(indexOfTab + 1); // Since we have News as the first tab.
				groups.remove(indexOfTab);
			}
		}

		public JPanel getPanel() {
			return panel1;
		}

		public ArrayList<ChatPanel> getGroups() {
			return groups;
		}
	}

	public static class ChatPanel {
		private String groupName;
		private JPanel panel1;
		private JTextField UserInput;
		private JButton SubmitButton;
		private JTextPane ChatConversation;
		private JList UserList;
		private DefaultListModel UserListModel;

		private Timer updateTimer;
		private Messenger chatMessenger;

		public ChatPanel(String groupName , User u, int numOfUsers, MainWindow w) {
			this.groupName = groupName;
			UserListModel = new DefaultListModel();
			UserList.setModel(UserListModel);

			if(u.getRole() == Role.READER) {
				UserInput.setText("You are not allowed to send messages in this channel.");
				UserInput.setEditable(false);
				SubmitButton.setEnabled(false);
			}

			w.getLeave().add(new AbstractAction(groupName) {
				public void actionPerformed(ActionEvent e) {
					w.removeLeave(groupName);
					w.getPanel().removeChatPanel(groupName);
				}
			});

			try{
				chatMessenger = new Messenger(groupName, u, numOfUsers, new MessengerListener() {//TODO: Test whether this listener works
					public void eventHappened(Object o) {
						if(o instanceof User) {
							UserListModel.add(UserListModel.size() == 0? 0 : UserListModel.size()-1, ((User)o).getUsername());
						} else if(o instanceof Integer) {
							UserListModel.remove(((Integer)o));
						} else if(o instanceof ChatMessage) {
							ChatMessage message = (ChatMessage) o;
							message.setContent(Parser.parse(message.getContent()));
							addMessage(message);
						} else if(o instanceof Role) {
							if((Role)o == Role.READER) {
								UserInput.setText("You are not allowed to send messages in this channel.");
								UserInput.setEditable(false);
								SubmitButton.setEnabled(false);
							} else {
								UserInput.setText("");
								UserInput.setEditable(true);
								SubmitButton.setEnabled(true);
							}
						}
					}
				});
			} catch (Exception e) { //NOTE: Does it need to be fatal error
				System.err.println("Fatal error: Could not open the Super Group connection.");
				System.exit(-1);
			}

			SubmitButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(!UserInput.getText().isEmpty()) {
						//TODO: Messenger class communication code (Should be encrypted)
						try{
							chatMessenger.sendMessage(new ChatMessage(LocalDateTime.now(), u, UserInput.getText()));
							UserInput.setText("");
						} catch(Exception exception) {
							System.err.println("Error: Could not send the chat message.");
						}
					}
				}
			});
		}

		public void close() {
			chatMessenger.close();
		}

		private void addMessage(ChatMessage message) {
			int start;
			int end;
			String dateOfMessage;
			String chatText;

			start = ChatConversation.getText().lastIndexOf("</i>");
			if(start == -1) {
				start = ChatConversation.getText().lastIndexOf("<body>");
			}
			end = ChatConversation.getText().lastIndexOf("</body>");
			chatText = ChatConversation.getText();

			dateOfMessage = message.getDate().getYear() + "." + message.getDate().getMonthValue() + "." + message.getDate().getDayOfMonth()
					+ " " + message.getDate().getHour() + ":" + String.format("%02d", message.getDate().getMinute());
			chatText = chatText.substring(0, start + 1) + "\n<blockquote>\n<p>" + message.getContent() + "</p>\n</blockquote>\n<i>" + message.getUser().getUsername() + " - " + dateOfMessage + "</i>" + chatText.substring(end);
			ChatConversation.setText(chatText);
		}

		public String getGroupName() {
			return groupName;
		}

		public JPanel getPanel() {
			return panel1;
		}

		public Messenger getChatMessenger() {
			return chatMessenger;
		}
	}

	public class ManageGroups extends JDialog{

		private JPanel contentPane;
		private JButton buttonOK;
		private JButton buttonCancel;
		private JComboBox GroupSelection;
		private JButton UpgradeButton;
		private JButton DowngradeButton;
		private JList Moderators;
		private JList Viewers;
		private JList Writers;
		private HashMap<String, ChatPanel> groupSelectionList;
		private HashMap<String, User> userList;

		public ManageGroups(ArrayList<ChatPanel> groups) {
			setContentPane(contentPane);
			setModal(true);
			getRootPane().setDefaultButton(buttonOK);

			groupSelectionList = new HashMap<String, ChatPanel>();
			userList = new HashMap<String, User>();

			if(groups != null)
				for(ChatPanel p : groups) { //TODO:Implement a way to prevent non-moderators from moderating random groups
					boolean isMod = false;
					for(User u : p.getChatMessenger().getUsers())
						if(u.getUsername().equals(MetaSuperGroup.username) && u.getRole() == Role.MODERATOR)
							isMod = true;
					if(isMod) {
						GroupSelection.add(new JLabel(p.groupName));
						groupSelectionList.put(p.groupName, p);
					}
				}
			GroupSelection.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					initManagementView();
				}
			});

			ListSelectionListener selectionListener = new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if(e.getSource() != Moderators)
						Moderators.clearSelection();
					if(e.getSource() != Writers)
						Writers.clearSelection();
					if(e.getSource() != Viewers)
						Viewers.clearSelection();
				}
			};

			Moderators.addListSelectionListener(selectionListener);
			Writers.addListSelectionListener(selectionListener);
			Viewers.addListSelectionListener(selectionListener);

			ActionListener upgrade = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(!Writers.isSelectionEmpty()) {
						User u = userList.get(Writers.getSelectedValue().toString());
						u.upgradeRole();
						Writers.remove(Writers.getSelectedIndex());
						Moderators.add(new JLabel(u.getAddress()));
					} else if(!Viewers.isSelectionEmpty()) {
						User u = userList.get(Viewers.getSelectedValue().toString());
						u.upgradeRole();
						Viewers.remove(Viewers.getSelectedIndex());
						Writers.add(new JLabel(u.getAddress()));
					}
				}
			};

			ActionListener downgrade = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(!Writers.isSelectionEmpty()) {
						User u = userList.get(Writers.getSelectedValue().toString());
						u.downgradeRole();
						Writers.remove(Writers.getSelectedIndex());
						Viewers.add(new JLabel(u.getAddress()));
					} else if(!Moderators.isSelectionEmpty()) { //TODO: Implement a way to prevent the moderator from downgrading themselves
						User u = userList.get(Moderators.getSelectedValue().toString());
						u.upgradeRole();
						Moderators.remove(Moderators.getSelectedIndex());
						Writers.add(new JLabel(u.getAddress()));
					}
				}
			};

			UpgradeButton.addActionListener(upgrade);
			DowngradeButton.addActionListener(downgrade);
			buttonOK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(groups != null) {
						try{
							groupSelectionList.get(GroupSelection.getSelectedItem().toString()).getChatMessenger().updateRoles();
						} catch(Exception exception) {
							System.err.println("Fatal error: Could not update the User roles.");
							System.exit(-1);
						}

					}
				}
			});

			setPreferredSize(new Dimension(200,300));
			setTitle("Manage Groups");
			SwingUtilities.updateComponentTreeUI(this);
			pack();
			setVisible(true);
		}

		private void initManagementView() {
			ChatPanel p = groupSelectionList.get(GroupSelection.getSelectedItem().toString());
			for(User u : p.getChatMessenger().getUsers()) {
				userList.put(u.getAddress(), u);
				switch (u.getRole()) {
					case MODERATOR:
						Moderators.add(new JLabel(u.getAddress()));
						break;

					case WRITER:
						Writers.add(new JLabel(u.getAddress()));
						break;

					case READER:
						Viewers.add(new JLabel(u.getAddress()));
						break;
				}
			}

		}
	}

	public class CreateNew extends JDialog{
		private JPanel contentPane;
		private JTextField myChatTextField;
		private JComboBox comboBox1;
		private JTextArea Superset;
		private JButton buttonOK;
		private JButton buttonCancel;
		private String[] addresses;
		private String[] usernames;
		private final int GROUP_CHAT = 0;
		private final int CHANNEL = 1;
		private final int PEER_TO_PEER = 2;

		public CreateNew(ArrayList<ChatPanel> groups, MainWindow w) {
			setContentPane(contentPane);
			setModal(true);
			getRootPane().setDefaultButton(buttonOK);

			Superset.append(MetaSuperGroup.username + ":" + MetaSuperGroup.metaSuperGroup.getAddressAsUUID() + "\n");

			buttonOK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(isValid(Superset) && isValid(myChatTextField, groups)) {
						User[] users = new User[addresses.length];

						for(int i = 0; i < addresses.length; i++)
							users[i] = new User(usernames[i], addresses[i], i == 0 ? Role.MODERATOR: Role.WRITER);

						if(comboBox1.getSelectedIndex() == GROUP_CHAT)
							MetaSuperGroup.initGroupChat(myChatTextField.getText(), users, w);
						else if(comboBox1.getSelectedIndex() == CHANNEL) {
							for(int i = 1; i < users.length; i++)
								users[i].downgradeRole();
							MetaSuperGroup.initChannel(myChatTextField.getText(), users, w);
						} else if(comboBox1.getSelectedIndex() == PEER_TO_PEER) {
							for(int i = 2; i < users.length; i++)
								users[i].downgradeRole();
							MetaSuperGroup.initPeerToPeer(myChatTextField.getText(), Arrays.copyOf(users, 2), users, w);
						}

						dispose();
					}
				}
			});

			buttonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});

			setPreferredSize(new Dimension(436,297));
			setTitle("Create New...");
			SwingUtilities.updateComponentTreeUI(this);
			pack();
			setVisible(true);
		}

		private boolean isValid(JTextArea area) {
			addresses = area.getText().split("\n");
			usernames = new String[addresses.length];
			for(int i = 0; i < addresses.length; i++)
				try {
					String[] split = addresses[i].split(":");
					usernames[i] = split[0];
					addresses[i] = split[1];
					UUID.fromString(addresses[i]);
				} catch(Exception e) {
					return false;
				}

			return true;
		}

		private boolean isValid(JTextField chatName, ArrayList<ChatPanel> groups) {
			for(ChatPanel p : groups)
				if(p.groupName.equals(chatName.getText()))
					return false;
			return true;
		}

		public JPanel getPanel() {
			return contentPane;
		}
	}
}
