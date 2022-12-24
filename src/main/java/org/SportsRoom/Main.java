package org.SportsRoom;

import javax.swing.*;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        try{
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            MainWindow n = new MainWindow();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}