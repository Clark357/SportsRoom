package org.SportsRoom;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;

public class EncryptionInitiator {

	private static long publicKey;
	private static long privateKey;

	public long CreateSharedKey() throws FileNotFoundException {
		long[] primes = new long[2];
		primes = getRandomPrimeNumberPair();
		//TODO: Write a line of code which transmitts the common primes.
		//-----------------------------------------
		//Example: Messenger.send(theReceiver,primes);
		//-----------------------------------------
		long stage1 = pow(primes[1],privateKey)%primes[0];
		//TODO: Write a line of code which transmitts the stage 1 result of the Initiation.
		//-----------------------------------------
		//Example: Messenger.send(theReceiver,stage1);
		//-------------------------------------------
		//TODO: Write the necessary code to obtain the Receiver's stage 1 result.
		long receiverStg1;
		return pow(receiverStg1,privateKey)%primes[0];

	}

	private long[] getRandomPrimeNumberPair() throws FileNotFoundException {
		File file = new File("../resources/Output.txt");
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
        Scanner fileIn2 = new Scanner(new File("../resources/More Primes.txt"));
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
