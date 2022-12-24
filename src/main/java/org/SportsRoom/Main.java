package org.SportsRoom;

import javax.swing.*;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        new File("src/data").mkdirs();
        new File("src/info").mkdirs();
        Scanner input = new Scanner(System.in);
        try{
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            MainWindow n = new MainWindow();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}