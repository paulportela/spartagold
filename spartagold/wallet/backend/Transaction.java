package spartagold.wallet.backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.UUID;

public class Transaction
{
	private String trans;
	private String senderPubKey;
	private byte[] signed;
	private String id;
	private double amount;
	private String receiverPubKey;
	private boolean isValid;

	public Transaction(String receiverPubKey, double amount)
	{
		this.receiverPubKey = receiverPubKey;
		Scanner in;
		try
		{
			in = new Scanner(new File("publickey.txt"));
			senderPubKey = in.next();
			in.close();
			this.amount = amount;
			isValid = true;
			id = UUID.randomUUID().toString();
			trans = id + this.receiverPubKey + amount;
			signed = SignTransaction.sign(trans);
		} 
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("New transaction created - ID: " + id);

	}

	public String getID()
	{
		return id;
	}

	public double getAmount()
	{
		return amount;
	}

	public String getReceiverPubKey()
	{
		return receiverPubKey;
	}

	public boolean isValid()
	{
		return isValid;
	}

	public String getSenderPubKey()
	{
		return senderPubKey;
	}

	public String toString()
	{
		return trans;
	}

	public byte[] getSigned()
	{
		return signed;
	}

	public void setSenderPubKey(String senderPubKey)
	{
		this.senderPubKey = senderPubKey;
	}

	public void setValid(boolean isValid)
	{
		this.isValid = isValid;
	}

}