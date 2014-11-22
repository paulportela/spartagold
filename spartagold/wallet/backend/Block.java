package spartagold.wallet.backend;

import java.util.ArrayList;

public class Block
{
	private int id;
	private String solution;
	private String minerId;
	private Block previousBlockID;
	private ArrayList<Transaction> trans;
	
	public Block(int id, String solution, String minerId)
	{
		super();
		this.setId(id);
		this.setSolution(solution);
		this.setMinerId(minerId);
		this.setPreviousBlockID(previousBlockID);
		trans = new ArrayList<Transaction>();
	}

	public Block getPreviousBlockID()
	{
		return previousBlockID;
	}

	public void setPreviousBlockID(Block previousBlockID)
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

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
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
