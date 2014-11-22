package spartagold.wallet.backend;

import java.io.Serializable;

public class Transaction implements Serializable
{
	private int id;
	private double amount;
	
	public Transaction(int id, double amount)
	{
		this.setId(id);
		this.setAmount(amount);
	}

	public double getAmount()
	{
		return amount;
	}

	public void setAmount(double amount)
	{
		this.amount = amount;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	@Override
	public String toString()
	{
		return "Transaction [id=" + id + ", amount=" + amount + "]";
	}
	
}
