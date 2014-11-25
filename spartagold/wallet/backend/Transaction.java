package spartagold.wallet.backend;

import java.io.Serializable;
import java.util.UUID;

public class Transaction implements Serializable
{
	private String id;
	private double amount;
	private String receiver;
	
	public Transaction(double amount, String receiver)
	{
		this.id = UUID.randomUUID().toString();;
		this.amount = amount;
		this.receiver = receiver;
	}

	public double getAmount()
	{
		return amount;
	}

	public void setAmount(double amount)
	{
		this.amount = amount;
	}

	public String getId()
	{
		return id;
	}

	@Override
	public String toString()
	{
		return "Transaction [id=" + id + ", amount=" + amount + "]";
	}
	
}
