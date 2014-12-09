package spartagold.wallet.backend;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Performs the proof-of-work algorithm on a block string. Creates a random string, then concatenates
 * random string to block string and an increasing long number. If a hash of this concatenation 
 * contains a number of zeroes equal to NUMBER_OF_ZEROES, solution is found.
 * 
 * @author Art Tucay Jr., Paul Portela
 * @version 1.0.0
 */

public class ProofOfWork extends Thread
{

	private static final int NUMBER_OF_ZEROES = 5;

	public static String findProof(String blockString) throws UnsupportedEncodingException, Exception
	{
		String randomString = UUID.randomUUID().toString();

		long i = 0;
		boolean isFound = false;
		String zeroes = String.format(String.format("%%%ds", NUMBER_OF_ZEROES), " ").replace(" ", "0");

		String concatString = blockString + randomString;

		while (!isFound)
		{
			System.out.println("Checking hash.");
			String hashedString = Verify.hash(concatString + i).substring(0, NUMBER_OF_ZEROES);
			if (hashedString.equals(zeroes))
			{
				isFound = true;
			} 
			else
			{
				i++;
			}
		}
		System.out.println("Solution found: " + randomString + i);
		return randomString + i;
	}
}