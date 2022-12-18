package org.SportsRoom;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try{
            //WARNING: This is not the end code it is for testing only
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            MainWindow n = new MainWindow();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}