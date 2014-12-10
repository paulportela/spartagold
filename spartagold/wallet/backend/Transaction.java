package spartagold.wallet.backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

/**
 * Transaction object which contains all necessary information to conduct a valid transaction.
 * Contains a get method for all instance variables, and a set function for variables allowed
 * to change.
 * 
 * @author Art Tucay Jr., Paul Portela
 * @version 1.0.0
 */

public class Transaction
{
	private String trans;
	private String senderPubKey;
	private byte[] signed;
	private String id;
	private double amount;
	private String receiverPubKey;
	private boolean isValid;
	private ArrayList<String> validIds;

	/**
	 * Constructor initializes instance variables with parameters.
	 *
	 * @param receiverPubKey		String of receiver's public key
	 * @param amount				double number of amount being sent
	 */

	public Transaction(String receiverPubKey, double amount)
	{
		this.validIds = new ArrayList<String>();
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

	public void addValidId(String s)
	{
		validIds.add(s);
	}
}