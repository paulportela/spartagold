package spartagold.wallet.backend;

public class VerifyTransaction {

	private String trans;
	private String senderPubKey;
	private String signedClearTrans;
	private String clearTrans;
	private String hashID;
	private int amount;
	private String receiverPubKey;
	private String isValid;
	
	public VerifyTransaction(String transaction) {
		
		trans = transaction;
		String[] transInfoArray = trans.split("\t");
		signedClearTrans = transInfoArray[0];
		senderPubKey = transInfoArray[1];
		hashID = transInfoArray[2];
		amount = Integer.parseInt(transInfoArray[3]);
		receiverPubKey = transInfoArray[4];
		isValid = transInfoArray[5];
		clearTrans = hashID + "\t" + amount + "\t" + receiverPubKey + "\t" + isValid;
	}
	
	public void verify() throws Exception {
		
		VerSig ver = new VerSig(senderPubKey, signedClearTrans, clearTrans);
		if (ver.isVerified()) {
			//TODO: read each line on ledger, if valid & senderPubKey=pubKey, tempamount+=line.amount. if tempamount>=amount, add to verTransList
			
		}
	}
}
