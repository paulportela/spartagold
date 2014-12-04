package spartagold.wallet.backend;

import java.io.IOException;

public class Miner implements Runnable
{

	/**
	 * To run this you do the following: Runnable r = new Miner(); Thread t =
	 * new Thread(r); t.start(); //this executes the run method in Miner class.
	 */

	private Block block;
	private String solution;

	public Miner(Transaction t) throws IOException
	{
		block = new Block();
		block.addTransaction(t);
		solution = "";
	}

	public void run()
	{
		try
		{
			solution = ProofOfWork.findProof(block.toString());
			block.setSolution(solution);
		} 
		catch (Exception e)
		{
			System.err.println("Caught exception " + e.toString());
		}
	}

	public Block getBlock()
	{
		return block;
	}

	public void addTransactionToBlock(Transaction trans)
	{
		block.addTransaction(trans);
	}
}