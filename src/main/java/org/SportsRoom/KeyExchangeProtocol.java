package org.SportsRoom;

import java.io.Serializable;

public class KeyExchangeProtocol implements Serializable {
    long sharedKey;
    int numOfKeysCombined;

    public KeyExchangeProtocol(long sharedKey, int numOfKeysCombined) {
        this.sharedKey = sharedKey;
        this.numOfKeysCombined = numOfKeysCombined;
    }

    public int getNumOfKeysCombined() {
        return numOfKeysCombined;
    }

    public long getSharedKey() {
        return sharedKey;
    }
}
