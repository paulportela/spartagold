/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spartagold.wallet.frontend;

import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 *
 * @author mojiarmin
 */
public class SpartaGoldCLI {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        /*InputStreamReader ir = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(ir);
        System.out.println("Please Enter any String::");
        String inputString = br.readLine();
        System.out.println("You Entered ::" + inputString);*/
        
        menu goldMenu = new menu();
        goldMenu.displayMenu();
    }
}



