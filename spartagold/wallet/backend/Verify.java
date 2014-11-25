package spartagold.wallet.backend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Verify {

	private static final int NUMOFZEROES = 4;
	
	public static boolean verifyTransaction(Transaction transaction, BlockChain bc) throws Exception {
		
        String senderPubKey = transaction.getSenderPubKey();
        String signed = transaction.getSigned();
        String trans = transaction.getTrans();
        double amount = transaction.getAmount();
        
		VerSig ver = new VerSig(senderPubKey, signed, trans);
		
		if (ver.isVerified()) {
			int tempAmount = 0;
          for(int i = 0; i < bc.getChainSize(); i++) {
            Block tempBlock = bc.getChain().get(i);
            outerloop:
            for(Transaction t : tempBlock.getTransactions()) {
              if(t.getReceiverPubKey == senderPubKey && t.getValid == true){
                tempAmount += t.getAmount;
              }
              if(tempAmount >= amount) {
                break outerloop;
              }
            }
          }
          return true;
        } else return false;
	}
  
  public static boolean verifyBlock(Block block){
    String zeros = new String(new char[NUMOFZEROES]).replace("\0", "0");
	String solution = block.getSolution();
	String blockId = block.getId();
	String hash = hash(solution + blockId).toString();
		
	if(zeros.equals(hash))
		return true;
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