package spartagold.wallet.backend;

import java.io.IOException;

public class Miner implements Runnable
{

	/**
	 * To run this you do the following: Runnable r = new Miner(); Thread t =
	 * new Thread(r); t.start(); //this executes the run method in Miner class.
	 */

	private Block block;
	private BlockChain chain;
	private String solution;

	public Miner(Transaction t, BlockChain bc) throws IOException
	{
		this.chain = bc;
		block = new Block();
		block.addTransaction(t);
		solution = "";
		System.out.println("Miner object created.");
	}

	public void run()
	{
		try
		{
			System.out.println("Finding solution...");
			solution = ProofOfWork.findProof(block.toString());
			block.setSolution(solution);
			System.out.println("Solution has been set in block.");
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