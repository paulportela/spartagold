package spartagold.wallet.backend;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import spartagold.framework.LoggerUtil;

/**
 * Performs the proof-of-work algorithm on a block string. Creates a random
 * string, then concatenates random string to block string and an increasing
 * long number. If a hash of this concatenation contains a number of zeroes
 * equal to NUMBER_OF_ZEROES, solution is found.
 * 
 * @author Art Tucay Jr., Paul Portela
 * @version 1.0.0
 */

public class ProofOfWork extends Thread
{

	public static String findProof(String blockString) throws UnsupportedEncodingException, Exception
	{
		String randomString = UUID.randomUUID().toString();

		long i = 0;
		boolean isFound = false;
		String zeroes = String.format(String.format("%%%ds", Verify.NUMBER_OF_ZEROES), " ").replace(" ", "0");

		String concatString = blockString + randomString;

		while (!isFound)
		{
			String hashedString = Verify.hash(concatString + i).substring(0, Verify.NUMBER_OF_ZEROES);
			System.out.println("Checking hash: " + hashedString);
			if (hashedString.equals(zeroes))
			{
				isFound = true;
			}
			else
			{
				i++;
			}
		}
		LoggerUtil.getLogger().fine("Solution found: " + randomString + i);
		return randomString + i;
	}
}