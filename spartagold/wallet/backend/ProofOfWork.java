package spartagold.wallet.backend;


import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class ProofOfWork extends Thread
{

	private static final int NUMBER_OF_ZEROES = 15;

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