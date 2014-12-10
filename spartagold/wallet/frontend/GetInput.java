package spartagold.wallet.frontend;


<<<<<<< HEAD
=======
package spartagold.wallet.frontend;
>>>>>>> 5cdab83919b01deea187ad3914bb6d1ba1204760
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
        

