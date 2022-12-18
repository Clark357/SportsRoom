package org.SportsRoom;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class LoginWindow extends JFrame {
    private LoginPage panel;

    public LoginWindow(MainWindow w) {
        super("Login");

        panel = new LoginPage();

        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                if(!panel.loginButton.isEnabled()) {
                    MetaSuperGroup.initMetaSuperGroup(panel.getUsername(), w);

                    w.setVisible(true);
                }else
                    w.dispose();
            }
        });
        setContentPane(panel.$$$getRootComponent$$$());
        setPreferredSize(new Dimension(400,300));
        SwingUtilities.updateComponentTreeUI(this);
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void setCredentials(String username, String password) {
        //TODO: Storage class or Encryption Initiator class communication to save the credentials

        dispose();
    }

    public class LoginPage {
        private JPanel panel1;
        private JTextField UsernameField;
        private JTextField PasswordField;
        private JButton loginButton;

        public LoginPage() {
            loginButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (!UsernameField.getText().isEmpty() && !PasswordField.getText().isEmpty()) {
                        ((JButton) e.getSource()).setEnabled(false);
                        setCredentials(UsernameField.getText(), PasswordField.getText());
                    }
                }
            });
        }

        public String getUsername() {
            return UsernameField.getText();
        }

        public String getPassword() {
            return PasswordField.getText();
        }

        {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
            $$$setupUI$$$();
        }

        /**
         * Method generated by IntelliJ IDEA GUI Designer
         * >>> IMPORTANT!! <<<
         * DO NOT edit this method OR call it in your code!
         *
         * @noinspection ALL
         */
        private void $$$setupUI$$$() {
            panel1 = new JPanel();
            panel1.setLayout(new BorderLayout(0, 0));
            final JPanel panel2 = new JPanel();
            panel2.setLayout(new GridBagLayout());
            panel1.add(panel2, BorderLayout.CENTER);
            UsernameField = new JTextField();
            GridBagConstraints gbc;
            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel2.add(UsernameField, gbc);
            PasswordField = new JTextField();
            PasswordField.setText("");
            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 3;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel2.add(PasswordField, gbc);
            final JLabel label1 = new JLabel();
            label1.setText("Username: ");
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.WEST;
            panel2.add(label1, gbc);
            final JLabel label2 = new JLabel();
            label2.setText("Private Key Keyword: ");
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.anchor = GridBagConstraints.WEST;
            panel2.add(label2, gbc);
            loginButton = new JButton();
            loginButton.setText("Login");
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel2.add(loginButton, gbc);
            final JPanel spacer1 = new JPanel();
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.fill = GridBagConstraints.VERTICAL;
            panel2.add(spacer1, gbc);
            final JLabel label3 = new JLabel();
            label3.setHorizontalAlignment(0);
            label3.setHorizontalTextPosition(0);
            label3.setIcon(new ImageIcon(getClass().getResource("/logo.jpg")));
            label3.setText("");
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            panel2.add(label3, gbc);
        }

        /**
         * @noinspection ALL
         */
        public JComponent $$$getRootComponent$$$() {
            return panel1;
        }

    }
}
