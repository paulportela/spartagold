package spartagold.wallet.backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class VerifyProof {
	
	//NO INSTANCE VARIABLES
	private String proof;
	private String hashID;
	private String amount;
	private String receiverPubKey;
	private String valid;
	private List<Transaction> ledgerArr = new ArrayList<Transaction>();
	private List<Transaction> verTransList = new ArrayList<Transaction>();
	private static final int NUMBER_OF_ZEROES = 3;
	
	/*
	 * 	takes in string solution (string contains proof, clearTrans, hashedLastTrans, minerPubKey, validtrans?)
	opens verTransList in reader
	opens ledger in reader
	saves each line as type Transaction in arraylist
	save each part of solution in separate variables
	truncate proof to first 3 characters
	if truncated proof contains zeroes:
		for verTransList loop:
			for ledger loop:
				if verTransList.get(i).gettransID == ledger.get(j).gettransID
					ledger.get(j).setValid(false)
					tempAmount += ledger.get(j).amount
	 */

	public static boolean verify(String solution) {
		
		BufferedReader lin = new BufferedReader(new FileReader("ledger"));
        String str1;
        while((str1 = lin.readLine()) != null){
        	Transaction trans = new Transaction(str1);
            ledgerArr.add(trans);
        }
        
        BufferedReader vin = new BufferedReader(new FileReader("ledger"));
        String str2;
        while((str2 = vin.readLine()) != null){
        	Transaction trans = new Transaction(str2);
            ledgerArr.add(trans);
        }
        
		String trunc = proof.substring(0, 3);
		String zeroes = String.format(String.format("%%%ds", NUMBER_OF_ZEROES), " ").replace(" ","0");
		if (trunc.toLowerCase().contains(zeroes)) {
			int tempAmount = 0;
			for (int i = 0; i < validTrans.length; i++) { //fetch validTrans list
				for (int j = 0; j < ledger.length; j++) { // fetch ledger
					
					//TODO: fetch validTrans hashID
					String[] validTransInfo = validTrans[i].split("\t");
					String vhashID validTransInfo[?];
					
					String[] ledgerLineInfo = ledger[j].split("\t");
					String lhashID = ledgerLineInfo[0];
					String lamount = ledgerLineInfo[1];
					String lvalid = ledgerLineInfo[3];
					
					if (vhashID == lhashID) {
						//TODO: find out how to overwrite just the valid/invalid section of specific line
						tempAmount += Integer.parseInt(lamount);
					}
				}
			}
			
			if (tempAmount > clearTrans.amount) {
				//TODO: add new transaction to bottom of ledger
			}
		} else {
			//TODO: send message ERROR to miner
		}
	}
	
	
}
