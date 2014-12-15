package spartagold.wallet.backend;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import spartagold.framework.LoggerUtil;

/**
 * Contains a transaction verification method used to check the block chain if a
 * transaction is legitimate, and a block verification method used to check if a
 * solution if legitimate.
 * 
 * @author Art Tucay Jr., Paul Portela
 * @version 1.0.0
 */

public class Verify
{

	public static final int NUMBER_OF_ZEROES = 15;

	/**
	 * Verifies a transaction by checking if the sender has the funds to send
	 * the amount specified in the transaction
	 * 
	 * @param transaction
	 * @param bc
	 * @return a boolean value of the status of verification
	 * @throws Exception
	 */
	public static boolean verifyTransaction(Transaction transaction, BlockChain bc) throws Exception
	{
		String senderPubKey = transaction.getSenderPubKey();
		byte[] signed = transaction.getSigned();
		String trans = transaction.toString();

		VerifySignature ver = new VerifySignature(senderPubKey, signed, trans);

		if (ver.isVerified())
		{
			System.out.println("");
			System.out.println("Verify: temp amount = 0, creating ArrayList unspentIds");
			double tmpAmount = 0;
			ArrayList<String> unspentIds = transaction.getUnspentIds();
			for (int i = 0; i < bc.getChainSize(); i++)
			{
				Block tempBlock = bc.getChain().get(i);
				System.out.println("Verify: Block ID: " + tempBlock.getId());
				System.out.println("Verify: Block solution: " + tempBlock.getSolution());
				for (Transaction t : tempBlock.getTransactions())
				{
					if (unspentIds == null)
						return false;
					System.out.println("Verify: Transaction ID: " + t.getID());
					System.out.println("Verify: Transaction amount: " + t.getAmount());
					System.out.println("Verify: UnspentIds size: " + unspentIds.size());
					for (int j = 0; j < unspentIds.size(); j++)
					{
						System.out.println("Verify: UnspentId: " + unspentIds.get(j));
						if (unspentIds.get(j).equals(t.getID()))
						{
							System.out.println("Verify: unspent ID matches transaction ID");
							if (t.isSpent())
							{
								System.out.println("Verify: transaction spent: " + t.isSpent());
								return false;
							}
							else
							{
								tmpAmount += t.getAmount();
								System.out.println("Verify: temp amount is now: " + tmpAmount);
							}
						}
					}
				}
			}
			if (tmpAmount >= transaction.getAmount())
			{
				System.out.println("Verify: Temp amount >= transaction amount.");
				return true;
			}
		}
		System.out.println("Verify: transaction not verified.");
		return false;
	}

	/**
	 * Verify block by rehashing the block's string and the solution.
	 * 
	 * @param block
	 *            Block object containing solution
	 * @return a boolean value of the status of verification
	 * @throws NoSuchAlgorithmException
	 */
	public static boolean verifyBlock(Block block) throws NoSuchAlgorithmException
	{
		String zeros = new String(new char[NUMBER_OF_ZEROES]).replace("\0", "0");
		String solution = block.getSolution();
		String blockString = block.toString();
		String hash = hash(blockString + solution).toString().substring(0, NUMBER_OF_ZEROES);
		if (zeros.equals(hash))
		{
			LoggerUtil.getLogger().fine("Block verified.");
			return true;
		}
		LoggerUtil.getLogger().fine("Block not verified.");
		return false;
	}

	/**
	 * Hash function in SHA-256. ToString objects and concatenate to get hash.
	 * 
	 * @param data
	 *            String of data to be hashed in SHA-256
	 * @return StringBuilder object containing the hashed data
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