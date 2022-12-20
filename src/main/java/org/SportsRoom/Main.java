package org.SportsRoom;

import javax.swing.*;
import org.jgroups.util.UUID;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        try{
            //WARNING: This is not the end code it is for testing only
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            MainWindow n = new MainWindow();
            while(MetaSuperGroup.metaSuperGroup == null) TimeUnit.MILLISECONDS.sleep(100);
            if(MetaSuperGroup.metaSuperGroup.getView().size() != 1)
                MetaSuperGroup.initGroupChat("Hey Hey", new User[] { new User("mikiyas", "1480f98b-a937-41fb-97f1-f268247821a1", Role.MODERATOR), new User(MetaSuperGroup.username, MetaSuperGroup.metaSuperGroup.getAddressAsUUID(), Role.WRITER)}, n);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}