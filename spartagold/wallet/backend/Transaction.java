package spartagold.wallet.backend;

public class Transaction {
	
	private String trans;
	private String senderPubKey;
	private String signedClearTrans;
	private String clearTrans;
	private String transID;
	private int amount;
	private String receiverPubKey;
	private boolean isValid;
	
	public Transaction(String trans) {
		this.trans = trans;
        String[] transInfoArray = this.trans.split("\t");
		signedClearTrans = transInfoArray[0];
		senderPubKey = transInfoArray[1];
		transID = transInfoArray[2];
		amount = Integer.parseInt(transInfoArray[3]);
		receiverPubKey = transInfoArray[4];
		isValid = Boolean.parseBoolean(transInfoArray[5]);
		clearTrans = transID + "\t" + amount + "\t" + receiverPubKey + "\t" + isValid;
	}
	
	public String getID() { return transID; }
	
	public int getAmount() { return amount; }
	
	public String getReceiverPubKey() { return receiverPubKey; }
	
	public boolean getValid() { return isValid; }
	
	public String getSenderPubKey() { return senderPubKey; }
	
	public String getClearTrans() { return clearTrans; }
	
	public String getSignedClearTrans() { return signedClearTrans; }
	
	public void setID(String transID) { this.transID = transID; }
	
	public void setAmount(int amount) { this.amount = amount; }
	
	public void setReceiverPubKey(String receiverPubKey) { this.receiverPubKey = receiverPubKey; }
	
	public void setValid(boolean isValid) { this.isValid = isValid; }
	
	public void setSenderPubKey(String senderPubKey) { this.senderPubKey = senderPubKey; }
	
	public void setClearTrans(String clearTrans) { this.clearTrans = clearTrans; }
	
	public void setSignedClearTrans(String signedClearTrans) { this.signedClearTrans = signedClearTrans; }

}
