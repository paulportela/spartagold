/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spartagoldcli;

/**
 *
 * @author mojiarmin
 */
public class menu {

    private Screen screen;
    private GetInput getinput;
    //constants corresponding to main menu option
    private static final int getLedger = 1;
    private static final int showLeger = 2;
    private static final int transfer = 3;
    private static final int help = 4;
    private static final int EXIT = 5;

    public menu() {
        screen = new Screen();
        getinput = new GetInput();
    }

    private void performMenuOption() {
        //user not chosen to exit
        boolean userExited = false;
        while (!userExited) {
            //show main menue for selection
            int menuSelection = displayMenu();
            switch (menuSelection) {
                case getLedger:
                    //initialize new object to the chosen type
                    System.out.println("Your Selection:" + menuSelection);
                    break;
                case showLeger:
                    //initialize new object to the chosen type
                    System.out.println("Your Selection:" + menuSelection);
                    break;
                case transfer:
                    //initialize new object to the chosen type
                    System.out.println("Your Selection:" + menuSelection);
                    break;
                case help:
                    //initialize new object to the chosen type
                    System.out.println("Your Selection:" + menuSelection);
                    break;
                //user choose to exit
                case EXIT:
                    screen.displayMessageLine("\n you are exiting\n");
                    userExited = true;
                    break;
                default:
                    screen.displayMessageLine("\nyou did not put valid number "
                            + "try again\n");

            }
        }
    }

    /**
     * display main menu
     */
    public int displayMenu() {
        screen.displayMessage("\n 1:getLedger");
        screen.displayMessage("\n 2:showLedger");
        screen.displayMessage("\n 3:transfer");
        screen.displayMessage("\n 4:help \n");
        screen.displayMessage("\n 5:EXIT \n");

        // return keypad.hashCode();
        return getinput.getInput();

    }
}