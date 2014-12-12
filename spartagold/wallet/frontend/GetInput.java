
package spartagold.wallet.frontend;
import java.util.Scanner;
/**
 *
 * @author mojiarmin
 */
public class GetInput 
{

    private Scanner input;

    public GetInput()
    {
         input = new Scanner(System.in);
    }

    public int getInput()
    {   
    	String x = input.nextLine();
        return Integer.parseInt(x);
    }
    
}
        

