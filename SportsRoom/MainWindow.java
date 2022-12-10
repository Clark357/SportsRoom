package SportsRoom;

import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

public class MainWindow extends JFrame{

	public MainWindow() { //TODO: Test Whether this is the best way to construct the window
		super("SportsRoom");
		setVisible(false);
		setContentPane(new MainMenu().getPanel());
		setPreferredSize(new Dimension(600, 400));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SwingUtilities.updateComponentTreeUI(this);
		pack();

		new LoginWindow(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				setVisible(true);
			}
		});
	}

	public MainWindow(MainMenu m) {
		this();
		setContentPane(m.getPanel());
	}

	public static class MainMenu {

		private JPanel panel1;
		private JTabbedPane Tabs;
		private JScrollPane NewsScroller;

		public MainMenu() {
			//TODO: Set up the constructor
		}

		public void addChatPanel(ChatPanel panel) {
			Tabs.addTab(panel.getGroupName(), panel.getPanel());
		}

		public JPanel getPanel() {
			return panel1;
		}
	}

	public static class ChatPanel {
		private String groupName;
		private JPanel panel1;
		private JTextField UserInput;
		private JButton SubmitButton;
		private JTextPane ChatConversation;
		private JList UserList;

		private Timer updateTimer;
		private Messenger chatMessenger;

		public ChatPanel(String groupName ,boolean canSubmit) {
			this.groupName = groupName;

			if(!canSubmit) {
				UserInput.setText("You are not allowed to send messages in this channel.");
				UserInput.setEditable(false);
				SubmitButton.setEnabled(false);
			}

			SubmitButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(!UserInput.getText().isEmpty()) {
						//TODO: Messenger class communication code
					}
				}
			});

			chatMessenger = new Messenger(groupName, new MessengerListener() {//TODO: Test whether this listener works
				public void eventHappened(Object o) {
					if(o instanceof User) {
						UserList.add(new JLabel(((User)o).getUsername()));
					} else if(o instanceof Integer) {
						UserList.remove(((Integer)o));
					} else if(o instanceof ChatMessage) {
						ChatMessage message = (ChatMessage) o;
						message.setContent(Parser.parse(message.getContent()));
						addMessage(message);
					}
				}
			});
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
			chatText = chatText.substring(0, start + 1) + "\n<blockquote>\n" + message.getContent() + "\n</blockquote>\n<i>" + message.getUser().getUsername() + " - " + dateOfMessage + "</i>" + chatText.substring(end);
			ChatConversation.setText(chatText);
		}

		public String getGroupName() {
			return groupName;
		}

		public JPanel getPanel() {
			return panel1;
		}
	}
}
