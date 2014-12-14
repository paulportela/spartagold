package spartagold.wallet.frontend;


public class Menu
{

	private Screen screen;
	private GetInput getinput;
	// constants corresponding to main menu option
	private static final int CreateAndSendTransaction = 1;
	private static final int Mine = 2;
	private static final int CheckBalance = 3;
	private static final int help = 4;
	private static final int EXIT = 5;

	public Menu()
	{
		screen = new Screen();
		getinput = new GetInput();
	}

	public void performMenuOption()
	{
		// user not chosen to exit
		boolean userExited = false;
		while (!userExited)
		{
			// show main menu for selection
			int menuSelection = displayMenu();
			switch (menuSelection)
			{
			case CreateAndSendTransaction:
				// initialize new object to the chosen type
				System.out.println("Your Selection:" + menuSelection);
				break;
			case Mine:
				// initialize new object to the chosen type
				System.out.println("Your Selection:" + menuSelection);
				break;
			case CheckBalance:
				// initialize new object to the chosen type
				System.out.println("Your Selection:" + menuSelection);
				break;
			case help:
				// initialize new object to the chosen type
				System.out.println("Your Selection:" + menuSelection);
				break;
			// user choose to exit
			case EXIT:
				screen.displayMessageLine("\n you are exiting\n");
				userExited = true;
				break;
			default:
				screen.displayMessageLine("\nyou did not put valid number " + "try again\n");

			}
		}
	}

	/**
	 * display main menu
	 */
	public int displayMenu()
	{
		screen.displayTitle("Welcome To SpartaGold\n" + "This is your wallet choose an action\n");
		screen.displayMessage("\n 1:Update Ledger");
		screen.displayMessage("\n 2:ShowLedger");
		screen.displayMessage("\n 3:Transfer");
		screen.displayMessage("\n 4:Help");
		screen.displayMessage("\n 5:EXIT \n");

		return getinput.getInput();
	}
}
