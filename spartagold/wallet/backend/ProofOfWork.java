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

public class ProofOfWork extends Thread {
	
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
	
	public String findProof() throws UnsupportedEncodingException, Exception {

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
		String trunc = proof.substring(0, NUMBER_OF_ZEROES);
		
		//check to see if first positions are zeroes
		String zeroes = String.format(String.format("%%%ds", NUMBER_OF_ZEROES), " ").replace(" ","0");
		if (trunc.toLowerCase().contains(zeroes)) {
			return proof + "\t" + clearTrans + "\t" + hashedLastTrans + "\t" + getMyPubKey + "\t" + validtrans; //validtrans = list of ids
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
