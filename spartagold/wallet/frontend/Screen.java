package spartagold.wallet.frontend;

public class Screen
{
	/**
	 * display message on the screen
	 * 
	 * @param the
	 *            message
	 */
	boolean displayedTitle = false;

	public void displayMessage(String message)
	{
		System.out.print(message);
	}

	public void displayMessageLine(String messege)
	{
		System.out.println(messege);
	}

	public void displayTitle(String s)
	{
		if (!displayedTitle)
		{
			System.out.println(s);
			displayedTitle = true;
		}
	}
}
