package spartagold.wallet.backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

@SuppressWarnings("serial")
public class Block implements Serializable
{
	private String id;
	private String solution;
	private String minerPubKey;
	private String previousBlockID;
	private ArrayList<Transaction> transactionList;

	private static final double REWARDAMOUNT = 5;

	public Block() throws IOException
	{
		super();
		id = UUID.randomUUID().toString();
		BufferedReader br = new BufferedReader(new FileReader("publickey.txt"));
		minerPubKey = br.readLine();
		br.close();
		transactionList = new ArrayList<Transaction>();
		transactionList.add(new Transaction(minerPubKey, REWARDAMOUNT));
		System.out.println("New block created - ID: " + id);
	}

	public String getPreviousBlockID()
	{
		return previousBlockID;
	}

	public void setPreviousBlockID(String previousBlockID)
	{
		this.previousBlockID = previousBlockID;
	}

	public String getMinerPubKey()
	{
		return minerPubKey;
	}

	public void setMinerPubKey(String minerPubKey)
	{
		this.minerPubKey = minerPubKey;
	}

	public String getSolution()
	{
		return solution;
	}

	public void setSolution(String solution)
	{
		this.solution = solution;
	}

	public String getId()
	{
		return id;
	}

	public ArrayList<Transaction> getTransactions()
	{
		return transactionList;
	}

	public void addTransaction(Transaction t)
	{
		transactionList.add(t);
	}

	@Override
	public String toString()
	{
		return "Block [id=" + id + ", minerPubKey=" + minerPubKey
				+ ", previousBlockID=" + previousBlockID + ", trans="
				+ transactionList + "]";
	}
}