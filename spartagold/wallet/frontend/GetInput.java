/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package spartagold.wallet.frontend;
import java.util.Scanner;
/**
 *
 * @author mojiarmin
 */
public class GetInput {
    //reads data from the command line
    private Scanner input;
    //no argument constructor initialize the scanner
    public GetInput(){
         input = new Scanner(System.in);
    }
    /**
     * get input
     * @return an integer value entered by user
     */
    public int getInput()
    {   String x = input.nextLine();
       
        return Integer.parseInt(x);
    }
    
}
        

