package spartagold.wallet.backend;

import java.io.Serializable;
import java.util.ArrayList;

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

	@Override
	public String toString()
	{
		return "BlockChain [chain=" + chain + "]";
	}
	
	
}
