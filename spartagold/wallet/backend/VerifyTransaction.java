package spartagold.wallet.backend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class VerifyTransaction {

	private List<Transaction> ledgerArr = new ArrayList<Transaction>();
	private Transaction transaction;
	private String senderPubKey;
	private String signedClearTrans;
	private String clearTrans;
	private int amount;
	
	/*
	 * 	takes Transaction type transaction
	used to verify every transaction received
	-needs to always listen or something, adds to list after each verification
	once verified, add to a list of verified transactions verTransList (for loop described below)
	max cap on verTransList = ?
	call getSenderPubKey, getReceiverPubKey, getSignedClearTrans, getClearTrans
	use senderPubKey on getSignedClearTrans, compare to clearTrans
	verify for loop:
		take each line of ledger, turn it into Transaction type
		if this.senderPubKey == ledger.receiverPubKey && ledger.isValid == true
			tempAmount += ledger.amount
			add to verTransList
	 */
	
	public VerifyTransaction(Transaction transaction) {
		
		this.transaction = transaction;
		senderPubKey = transaction.getSenderPubKey();
		signedClearTrans = transaction.getSignedClearTrans();
		clearTrans = transaction.getClearTrans();
		amount = transaction.getAmount();
		
		//TODO: check length of verTransList, if >=10, do not add more
	}
	
	public boolean verify() throws Exception {
		
		FileInputStream pubKeyfis = new FileInputStream("publickey");
        
		VerSig ver = new VerSig(pubKeyfis, signedClearTrans, clearTrans);
		
		if (ver.isVerified()) {
			int tempAmount = 0;
			BufferedReader in = new BufferedReader(new FileReader("ledger"));
	        String str;
	        while((str = in.readLine()) != null){
	        	Transaction trans = new Transaction(str);
	            ledgerArr.add(trans);
	        }
	        for (int i = 0; i < ledgerArr.size(); i++) {
	        	if (senderPubKey == ledgerArr.get(i).getReceiverPubKey() && ledgerArr.get(i).isValid()) {
	        		tempAmount += ledgerArr.get(i).getAmount();
	        		BufferedWriter out = new BufferedWriter(new FileWriter("verTransList", true));
		            out.append(clearTrans + "\r\n");
		            out.close();
	        	}
	        	if (tempAmount >= amount) {
	        		i = ledgerArr.size();
	        	}
	        }
	        
	        return true;
		} else return false;
	}
}
