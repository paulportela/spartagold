package spartagold.wallet.backend;

import java.io.IOException;

public class GenesisBlock
{
	private BlockChain bc;
	private Block b;
	private Transaction t;

	public GenesisBlock() throws IOException
	{
		this.bc = new BlockChain();
		this.b = new Block();
		String pub = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEjRod7Cu2XtTn+sZMOuM9gELSaLMJuXKXyUokJCJSoiUiuhAj7yTPTlvBitSn/zbM+/1crjmqlMklBCN+0CPjsQ==";
		this.t = new Transaction(pub, 0);
		b.addTransaction(t);
		bc.addBlock(b);
	}

	public BlockChain getGenesisBlock()
	{
		return bc;
	}
}
