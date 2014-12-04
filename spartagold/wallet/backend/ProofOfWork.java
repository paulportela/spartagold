package spartagold.wallet.backend;


import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class ProofOfWork extends Thread
{

	private static final int NUMBER_OF_ZEROES = 5;

	public static String findProof(String blockString) throws UnsupportedEncodingException, Exception
	{
		String randomString = UUID.randomUUID().toString();

		int i = 0;
		boolean isFound = false;
		String zeroes = String.format(String.format("%%%ds", NUMBER_OF_ZEROES), " ").replace(" ", "0");

		String concatString = blockString + randomString;

		while (!isFound)
		{
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

		return randomString + i;
	}
}