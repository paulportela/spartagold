package spartagold.wallet.backend;

public class Transaction {

	private String trans;
	private String senderPubKey;
	private String signed;
	private String id;
	private double amount;
	private String receiverPubKey;
	private boolean isValid;

	public Transaction(String receiverPubKey, double amount) {
		this.receiverPubKey = receiverPubKey;
		this.amount = amount;
		isValid = true;
		transID = UUID.randomUUID().toString();
		trans = id + this.receiverPubKey + amount;
		signed = SignTransaction.sign(trans);

	}

	public String getID() { return id; }

	public int getAmount() { return amount; }

	public String getReceiverPubKey() { return receiverPubKey; }

	public boolean isValid() { return isValid; }

	public String getSenderPubKey() { return senderPubKey; }

	@override
	public String toString() { return trans; }

	public String getSigned() { return signed; }

	public void setValid(boolean isValid) { this.isValid = isValid; }

}