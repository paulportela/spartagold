package spartagold.wallet.backend;

public class Transacation
{
	private int id;
	private double input;
	private double output;
	
	public Transacation(int id, double input, double output)
	{
		super();
		this.id = id;
		this.input = input;
		this.output = output;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public double getInput()
	{
		return input;
	}

	public void setInput(double input)
	{
		this.input = input;
	}

	public double getOutput()
	{
		return output;
	}

	public void setOutput(double output)
	{
		this.output = output;
	}
	
	
}
