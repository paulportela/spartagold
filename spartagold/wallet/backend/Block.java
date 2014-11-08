package spartagold.wallet.backend;

public class Block
{
	private int id;
	private Transacation[] transactions;
	private int difficulty;
	private String solver;
	private String previous;
	
	public Block(int id, Transacation[] transactions, int difficulty, String solver)
	{
		super();
		this.id = id;
		this.transactions = transactions;
		this.difficulty = difficulty;
		this.solver = solver;
	}
	
	public int numberOfTransactions()
	{
		return transactions.length;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public Transacation[] getTransactions()
	{
		return transactions;
	}

	public void setTransactions(Transacation[] transactions)
	{
		this.transactions = transactions;
	}

	public int getDifficulty()
	{
		return difficulty;
	}

	public void setDifficulty(int difficulty)
	{
		this.difficulty = difficulty;
	}

	public String getSolver()
	{
		return solver;
	}

	public void setSolver(String solver)
	{
		this.solver = solver;
	}
	
	public void setPreviousBlock(Block b)
	{
		//This should be the hash of the previous block not string
		previous = b.toString();
	}
	
}
