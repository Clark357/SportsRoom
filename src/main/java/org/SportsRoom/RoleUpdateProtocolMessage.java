package org.SportsRoom;

import java.io.Serializable;

public class RoleUpdateProtocolMessage implements Serializable {
    private User[] users;

    public RoleUpdateProtocolMessage(User[] users) {
        this.users = users;
    }

    public User[] getUsers() {
        return users;
    }
}
