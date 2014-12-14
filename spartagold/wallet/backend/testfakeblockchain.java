package spartagold.wallet.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class testfakeblockchain {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		System.out.println("");
		System.out.println("Reading public key");
		String pubkey = "";
		Scanner pubIn;
		try
		{
			pubIn = new Scanner(new File("publickey.txt"));
			String pub = pubIn.next();
			pubIn.close();
			pubkey = pub;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Creating block chain");
		BlockChain bc = new BlockChain();
		
		System.out.println("Creating block 1");
		Block b1 = new Block();
		System.out.println("Creating transaction 1 with amount 10");
		Transaction t1 = new Transaction(pubkey, 10);
		t1.setSpent(false);
		b1.addTransaction(t1);
		bc.addBlock(b1);
		
		System.out.println("Creating block 2");
		Block b2 = new Block();
		System.out.println("Creating transaction 2 with amount 11");
		Transaction t2 = new Transaction(pubkey, 11);
		t1.setSpent(false);
		b1.addTransaction(t2);
		bc.addBlock(b2);
		
		System.out.println("Creating block 3");
		Block b3 = new Block();
		System.out.println("Creating transaction 3 with amount 12");
		Transaction t3 = new Transaction(pubkey, 12);
		t1.setSpent(true);
		b1.addTransaction(t3);
		bc.addBlock(b3);
		
		/**
		System.out.println("Reading block chain");
		BlockChain bc = new BlockChain();
		ObjectInputStream in = new ObjectInputStream(new FileInputStream("blockchain"));
		bc = (BlockChain) in.readObject();
		in.close();
		
		System.out.println("Checking block to see if transactions exist");
		for (int i = 0; i < bc.getChainSize(); i++)
		{
			Block tempBlock = bc.getChain().get(i);
			for (Transaction t : tempBlock.getTransactions())
			{
				System.out.println("Transaction ID: " + t.getID());
				System.out.println("Date: " + t.getDate());
				System.out.println("Spent: " + t.isSpent());
			}
		}
		*/
		
		System.out.println("Saving block chain file");
		try
		{
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("blockchain"));
			out.writeObject(bc);
			out.close();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

}
