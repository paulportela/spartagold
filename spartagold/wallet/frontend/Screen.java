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
public class Screen {
   /**
     * display message on the screen
     *
     * @param the message
     */
    public void displayMessage(String message)
    {
        System.out.print(message);
    }//end displayMessage 
    public void displayMessageLine(String messege)
    {
        System.out.println(messege);
    }// end displayMessageLin  
}
