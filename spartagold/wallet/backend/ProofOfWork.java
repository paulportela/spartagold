package spartagold.wallet.backend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

public class ProofOfWork {
	
	private List<String> ledgerArr = new ArrayList<String>();
	private String[] ledger;
	private String trans;
	private String senderPubKey;
	private String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	private static final int NUMBER_OF_ZEROES = 3;
	private String signedClearTrans;
	private String clearTrans;
	private String hashID;
	private int amount;
	private String receiverPubKey;
	private String isValid;
	
	public ProofOfWork(String transaction) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader("ledger"));
        String str;
        while((str = in.readLine()) != null){
            ledgerArr.add(str);
        }
        ledger = ledgerArr.toArray(new String[ledgerArr.size()]);
        
        trans = transaction;
        String[] transInfoArray = trans.split("\t");
		signedClearTrans = transInfoArray[0];
		senderPubKey = transInfoArray[1];
		hashID = transInfoArray[2];
		amount = Integer.parseInt(transInfoArray[3]);
		receiverPubKey = transInfoArray[4];
		isValid = transInfoArray[5];
	}
	
	public void findProof() throws UnsupportedEncodingException, Exception {
		//fill list of valid transactions, may need to move to other class
		int tempAmount = 0;
		List<String> validTrans = new ArrayList<String>();
		for (int i = 0; i < ledger.length; i++) {
			String[] ledgerLineInfo = ledger[i].split("\t");
			String lhashID = ledgerLineInfo[0];
			String lamount = ledgerLineInfo[1];
			String lreceiverPubKey = ledgerLineInfo[2];
			String lvalid = ledgerLineInfo[3];
			if (senderPubKey == lreceiverPubKey && lvalid == "valid") {
				tempAmount += Integer.parseInt(lamount);
				validTrans.add(lhashID);
			}
			if (tempAmount >= Integer.parseInt(lamount)){
				i = ledger.length;
			}		
		}
		
		//TODO: needs check to see if isMining=true
		int randomNum = 1 + (int)(Math.random()*10); 
		StringBuffer buffer = new StringBuffer();
		int charactersLength = characters.length();
		for (int i = 0; i < randomNum; i++) {
			double index = Math.random() * charactersLength;
			buffer.append(characters.charAt((int) index));
		}
		
		//SHA256 hash on last transaction on ledger
		MessageDigest md1 = MessageDigest.getInstance("SHA-256");
		md1.update(readLastTrans().getBytes("UTF-8"));
		byte[] digest1 = md1.digest();
		String hashedLastTrans = DatatypeConverter.printHexBinary(digest1);
		
		//concatenate random string with last transaction on ledger and clear trans
		String randString = buffer.toString() + hashedLastTrans; // + clearTrans;
		
		//SHA256 hash on randString
		MessageDigest md2 = MessageDigest.getInstance("SHA-256");
		md2.update(randString.getBytes("UTF-8"));
		byte[] digest2 = md2.digest();
		
		//convert randString to hashed proof, truncated to first three positions
		String proof = DatatypeConverter.printHexBinary(digest2);
		String trunc = proof.substring(0, 3);
		
		//check to see if first three positions are zeroes
		String zeroes = String.format(String.format("%%%ds", NUMBER_OF_ZEROES), " ").replace(" ","0");
		if (trunc.toLowerCase().contains(zeroes)) {
			//TODO: broadcast FOUNDSOLUTION containing proof|cleartrans|hashedLastTrans|miner's pub key|validtrans
		}
	}
	
	public String readLastTrans() throws Exception {
		String sCurrentLine;
		String lastLine = "";
	    BufferedReader br = new BufferedReader(new FileReader("ledger"));
	    while ((sCurrentLine = br.readLine()) != null) {
	        lastLine = sCurrentLine;
	    }
	    br.close();
	    System.out.println("last block: " + lastLine);
		return lastLine;
	}
}
