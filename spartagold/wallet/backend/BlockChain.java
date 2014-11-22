package spartagold.wallet.backend;

import java.util.ArrayList;

public class BlockChain
{
	private ArrayList<Block> chain;

	public BlockChain(ArrayList<Block> chain)
	{
		super();
		this.setChain(chain);
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
