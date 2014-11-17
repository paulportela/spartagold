package spartagold.wallet.backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class VerifyProof {
	
	private String proof;
	private String hashID;
	private String amount;
	private String receiverPubKey;
	private String valid;
	private List<String> ledgerArr = new ArrayList<String>();
	private String[] ledger;
	private static final int NUMBER_OF_ZEROES = 3;

	public VerifyProof(String solution) {
		
		BufferedReader in = new BufferedReader(new FileReader("ledger"));
        String str;
        while((str = in.readLine()) != null){
            ledgerArr.add(str);
        }
        ledger = ledgerArr.toArray(new String[ledgerArr.size()]);
        
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
