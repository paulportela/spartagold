package spartagold.wallet.backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Block implements Serializable
{
	private String id;
	private String solution;
	
	//TODO change miderId to something else
	private String minerId;
	
	private String previousBlockID;
	private ArrayList<Transaction> trans;
	
	private static final int REWARDAMOUNT = 5;
	
	public Block(String solution, String minerId)
	{
		super();
		this.id = UUID.randomUUID().toString();
		this.solution = solution;
		this.minerId = UUID.randomUUID().toString();
		
		trans = new ArrayList<Transaction>();
		trans.add(new Transaction(REWARDAMOUNT, minerId));
	}

	public String getPreviousBlockID()
	{
		return previousBlockID;
	}

	public void setPreviousBlockID(String previousBlockID)
	{
		this.previousBlockID = previousBlockID;
	}

	public String getMinerId()
	{
		return minerId;
	}

	public void setMinerId(String minerId)
	{
		this.minerId = minerId;
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
		return trans;
	}

	public void addTransaction(Transaction t)
	{
		trans.add(t);
	}

	@Override
	public String toString()
	{
		return "Block [id=" + id + ", solution=" + solution + ", minerId="
				+ minerId + ", previousBlockID=" + previousBlockID + ", trans="
				+ trans + "]";
	}
	
	
}
