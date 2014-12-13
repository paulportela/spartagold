package spartagold.wallet.backend;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import spartagold.framework.LoggerUtil;

/**
 * Initiates proof-of-work and saves solution into a block object. Adds transaction
 * to block.
 * 
 * @author Art Tucay Jr., Paul Portela
 * @version 1.0.0
 */

public class Miner implements Runnable
{

	/**
	 * To run this you do the following: Runnable r = new Miner(); Thread t =
	 * new Thread(r); t.start(); //this executes the run method in Miner class.
	 */

	private Block block;
	private String solution;
	private Transaction change;
	private Transaction unconfirmedTransaction;

	public Miner(Transaction t) throws IOException
	{
		this.unconfirmedTransaction = t;
		block = new Block();
		LoggerUtil.getLogger().fine("Miner object created.");
	}

	public void run()
	{
		try
		{
			block.addTransaction(unconfirmedTransaction);
			double total = unconfirmedTransaction.getTransactionTotal() - unconfirmedTransaction.getAmount();
			//t.setAmount(t.getAmount() - Block.FEE);
			if(total > 0)
			{
				this.change = new Transaction(unconfirmedTransaction.getSenderPubKey(), total);
				block.addTransaction(change);
			}
			
			solution = "";
			LoggerUtil.getLogger().fine("Finding solution...");
			solution = ProofOfWork.findProof(block.toString());
			block.setSolution(solution);
			LoggerUtil.getLogger().fine("Solution has been set in block.");
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