package org.SportsRoom;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;

public class EncryptionInitiator {

	private static long publicKey;
	private static long privateKey;

	public long CreateSharedKey() {
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	private long[] getRandomPrimeNumberPair() throws FileNotFoundException {
		File file = new File("../resources/Output.txt");
        Scanner userIn = new Scanner(System.in);
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

}
