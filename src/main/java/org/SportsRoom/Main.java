package org.SportsRoom;

import javax.swing.*;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        try{
            //WARNING: This is not the end code it is for testing only
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            MainWindow n = new MainWindow();
            while(MetaSuperGroup.metaSuperGroup == null) TimeUnit.MILLISECONDS.sleep(100);
//            if(MetaSuperGroup.metaSuperGroup.getView().size() != 1)
//                MetaSuperGroup.initPeerToPeer("Hey Hey", new User[] { new User("mikiyas", input.next(), Role.MODERATOR), new User(MetaSuperGroup.username, MetaSuperGroup.metaSuperGroup.getAddressAsUUID(), Role.WRITER)}, new User[] { new User("mikiyas", input.next(), Role.MODERATOR), new User(MetaSuperGroup.username, MetaSuperGroup.metaSuperGroup.getAddressAsUUID(), Role.WRITER)}, n);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}