package spartagold.wallet.backend;

import java.io.IOException;

import spartagold.framework.LoggerUtil;
import spartagold.framework.PeerInfo;

/**
 * Initiates proof-of-work and saves solution into a block object. Adds
 * transaction to block.
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
	private SpartaGoldNode peer;

	public Miner(SpartaGoldNode peer) throws IOException
	{
		this.peer = peer;
		this.unconfirmedTransaction = peer.getTransaction();
		System.out.println("Miner.java: peer.getTransaction() called");
		block = new Block();
		this.solution = null;
		System.out.println("Miner object created.");
	}

	public void run()
	{
		System.out.println("Mining has begun.");
		peer.setStatus("Mining has begun.");
		try
		{
			block.addTransaction(unconfirmedTransaction);
			LoggerUtil.getLogger().fine("Transaction added to block.");
			double total = unconfirmedTransaction.getTransactionTotal() - unconfirmedTransaction.getAmount();
			// t.setAmount(t.getAmount() - Block.FEE);
			System.out.println("Miner.java: unconfirmedTransaction.getTransactionTotal() - unconfirmedTransaction.getAmount() " + total);
			if (total > 0)
			{
				change = new Transaction(unconfirmedTransaction.getSenderPubKey(), total);
				block.addTransaction(change);
				System.out.println("Change transaction added to block.");
			}

			LoggerUtil.getLogger().fine("Finding solution...");
			peer.setStatus("Finding solution...");
			solution = ProofOfWork.findProof(block.toString());
			block.setSolution(solution);
			LoggerUtil.getLogger().fine("Solution has been set in block.");
			peer.setStatus("Solution has been found. You were awarded: " + Block.REWARDAMOUNT);
			for (PeerInfo pid : peer.getAllPeers())
			{
				LoggerUtil.getLogger().fine("Broadcasting...");
				peer.connectAndSendObject(pid, SpartaGoldNode.FOUNDSOLUTION, block);
			}
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

	public boolean hasATransactionToVerify()
	{
		if (unconfirmedTransaction == null)
			return false;
		else
			return true;
	}

}