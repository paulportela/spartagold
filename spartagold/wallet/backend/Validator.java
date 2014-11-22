package spartagold.wallet.backend;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class contains methods for validating objects.
 *
 */

public class Validator
{
	/**
	 * Validate a block
	 * @param b
	 * @return
	 */
	public boolean validateBlock(Block b)
	{
		//TODO validate Block by rehashing proof and transactions
		return false;
	}
	
	/**
	 * Validate Transaction object
	 * @param trans
	 * @param chain
	 * @return
	 */
	public boolean validateTransaction(Transaction trans, BlockChain chain)
	{
		//TODO validate trans by using chain
		return false;
	}
	
	/**
	 * Hash function. ToString objects and concatenate to get hash.
	 * @param data
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static StringBuilder hash(String data) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(data.getBytes());
		byte[] bytes = md.digest();
		StringBuilder binary = new StringBuilder();
		for (byte b : bytes)
		{
		   int val = b;
		   for (int i = 0; i < 8; i++)
		   {
		      binary.append((val & 128) == 0 ? 0 : 1);
		      val <<= 1;
		   }
		}
		return binary;
	}
}
