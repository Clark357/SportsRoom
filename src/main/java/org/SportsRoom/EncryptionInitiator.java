package org.SportsRoom;

import org.jgroups.*;

import java.io.*;
import java.util.*;

public class EncryptionInitiator implements Receiver {

	private static long publicKey;
	private static long privateKey;
    private long sharedKey;
    private int keysReceived;
    private long[] primes;
    private JChannel channel;
    private int numOfUsersCommunicating;
    private int numOfActualUsers;
    private EncryptionInitiatorListener listener;

    public EncryptionInitiator(String groupName, int numOfUsers, int numOfActualUsers, long[] primes, EncryptionInitiatorListener listener) throws Exception {
        channel = new JChannel("src/main/resources/tcp.xml").setName(MetaSuperGroup.username).connect(groupName).setDiscardOwnMessages(true);
        keysReceived = 0;
        this.listener = listener;
        this.channel = channel;
        this.primes = primes;
        this.numOfUsersCommunicating = numOfUsers;
        this.numOfActualUsers = numOfActualUsers;

        sharedKey = pow(primes[1],privateKey)%primes[0];
        keysReceived = 1;
    }

    @Override
    public void viewAccepted(View v) {
        System.out.println(v);
        if(v.size() == numOfUsersCommunicating)
            try {
                channel.send(new ObjectMessage(null, new KeyExchangeProtocol(sharedKey, keysReceived)));
            } catch (Exception e) {
                System.err.println("Fatal error: Could not send the initialization message.");
                System.exit(-1);
            }
    }

    @Override
    public void receive(Message msg) {
        System.out.println(msg);
        if(msg.getObject() instanceof KeyExchangeProtocol) {
            KeyExchangeProtocol k = (KeyExchangeProtocol) msg.getObject();
            sharedKey = ( sharedKey * k.getSharedKey() ) % primes[0];
            keysReceived++;
            if(keysReceived == numOfUsersCommunicating) {
                listener.keyCreated(sharedKey);
                Storage chatStorage = new Storage(channel.getClusterName());
                chatStorage.initializeStorageFile(sharedKey, publicKey, privateKey);
                //TODO:Close the storage
                channel.close();
            }
        }
    }

    public int getNumOfUsersCommunicating() {
        return numOfUsersCommunicating;
    }

    public void setPublicPrivateKey(long publicKeyGiven, long privateKeyGiven) {
        publicKey = publicKeyGiven;
        privateKey = privateKeyGiven;
    }

	public static long[] getRandomPrimeNumberPair() throws FileNotFoundException {
		File file = new File("src/main/resources/Output.txt");
        Scanner fileIn = new Scanner(file);
        ArrayList<Long> primes = new ArrayList<Long>();
        ArrayList<Long> allPrimes = new ArrayList<Long>();
        ArrayList<ArrayList<Long>> roots = new ArrayList<ArrayList<Long>>();
        while(fileIn.hasNextLine()){
            String curr = fileIn.nextLine();
            primes.add(Long.parseLong(curr.split(" ")[0]));
            roots.add(new ArrayList<Long>());
            for(int i = 1 ; i<curr.split(" ").length;i++){
                roots.get(roots.size()-1).add(Long.parseLong(curr.split(" ")[i]));
            }
        }
        Scanner fileIn2 = new Scanner(new File("src/main/resources/More Primes.txt"));
        while(fileIn2.hasNextLong()){
            allPrimes.add(fileIn2.nextLong());
        }
        for(ArrayList<Long> i : roots){
            ArrayList<Integer> indexes = new ArrayList<Integer>();
            for(int k = 0;k<i.size();k++){
                if(!allPrimes.contains(i.get(k))){
                    indexes.add(k);
                }
            }
            for(int m = 0; m<indexes.size();m++){
                i.remove((int) indexes.get(indexes.size()-m-1));
            }
        }
		long first = primes.get((int) Math.random()*primes.size());
		long second = roots.get(primes.indexOf(first)).get((int) Math.random()*roots.get(primes.indexOf(first)).size());
		long[] result = {first,second};
		return result;
	}
	private static long pow(long a , long b){
		long result = 1;
		for(int i = 0; i<b;i++){
			result = result*a;
		}
		return result;
	}

}
