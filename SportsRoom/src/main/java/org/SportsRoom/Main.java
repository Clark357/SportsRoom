package org.SportsRoom;

import javax.swing.*;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        try{
            //WARNING: This is not the end code it is for testing only
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            MainWindow.MainMenu n = new MainWindow.MainMenu();
            MainWindow m = new MainWindow(n);
            MainWindow.ChatPanel p = new MainWindow.ChatPanel("Hey hey", true);
            n.addChatPanel(p);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}