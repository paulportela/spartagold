package spartagold.wallet.backend;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Verify
{

	private static final int NUMOFZEROES = 4;

	/**
	 * Verifies a transaction by checking if the sender has the funds to send the amount
	 * specified in the transaction
	 * @param transaction
	 * @param bc
	 * @return
	 * @throws Exception
	 */
	public static boolean verifyTransaction(Transaction transaction, BlockChain bc) throws Exception
	{
		String senderPubKey = transaction.getSenderPubKey();
		byte[] signed = transaction.getSigned();
		String trans = transaction.toString();
		double amount = transaction.getAmount();

		VerifySignature ver = new VerifySignature(senderPubKey, signed, trans);

		if (ver.isVerified())
		{
			int tempAmount = 0;
			for (int i = 0; i < bc.getChainSize(); i++)
			{
				Block tempBlock = bc.getChain().get(i);
				for (Transaction t : tempBlock.getTransactions())
				{
					if (t.getReceiverPubKey() == senderPubKey
							&& t.isValid() == true)
					{
						tempAmount += t.getAmount();
					}
					if (tempAmount >= amount)
					{
						return true;
					}
				}
			}
		} 
		return false;
	}

	/**
	 * Verify block by rehashing the block's string and the solution.
	 * @param block
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static boolean verifyBlock(Block block) throws NoSuchAlgorithmException
	{
		String zeros = new String(new char[NUMOFZEROES]).replace("\0", "0");
		String solution = block.getSolution();
		String blockString = block.toString();
		String hash = hash(blockString + solution).toString();

		if (zeros.equals(hash))
			return true;
		return false;
	}

	/**
	 * Hash function. ToString objects and concatenate to get hash.
	 * 
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