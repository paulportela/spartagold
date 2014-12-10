package spartagold.wallet.backend;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Creates ArrayList of blocks with add, get, set, and toString methods.
 * 
 * @author Art Tucay Jr., Paul Portela
 * @version 1.0.0
 */

@SuppressWarnings("serial")
public class BlockChain implements Serializable
{
	private ArrayList<Block> chain;

	public BlockChain()
	{
		super();
		chain = new ArrayList<Block>();
	}

	public void addBlock(Block b)
	{
		chain.add(b);
	}

	public int getChainSize()
	{
		return chain.size();
	}

	public ArrayList<Block> getChain()
	{
		return chain;
	}

	public void setChain(ArrayList<Block> chain)
	{
		this.chain = chain;
	}

	public boolean contains(Block b)
	{
		return this.chain.contains(b);

	}

	@Override
	public String toString()
	{
		return "BlockChain [chain=" + chain + "]";
	}

}