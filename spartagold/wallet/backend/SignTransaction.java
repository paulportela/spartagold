package spartagold.wallet.backend;

import java.io.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

public class SignTransaction {
	
	private String signature;
	
	//TODO: change to take String parameter
	public SignTransaction(String clearTrans) {
		/* Generate a DSA signature */
		try {
			/* Create a Signature object and initialize it with the private key */
			/* import encoded private key */

            FileInputStream keyfis = new FileInputStream("privatekey");
            byte[] encKey = new byte[keyfis.available()];  
            keyfis.read(encKey);

            keyfis.close();

            X509EncodedKeySpec privKeySpec = new X509EncodedKeySpec(encKey);

            KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
            PrivateKey privKey = keyFactory.generatePrivate(privKeySpec);
			Signature dsa = Signature.getInstance("SHA1withDSA", "SUN"); 

			dsa.initSign(privKey);

			/* Update and sign the data */

			FileInputStream fis = new FileInputStream(clearTrans);
			BufferedInputStream bufin = new BufferedInputStream(fis);
			byte[] buffer = new byte[1024];
			int len;
			while (bufin.available() != 0) {
				len = bufin.read(buffer);
				dsa.update(buffer, 0, len);
			};

			bufin.close();

			/* Now that all the data to be signed has been read in, 
			generate a signature for it */

			byte[] realSig = dsa.sign();

			//TODO: change realSig into String

		}
		catch (Exception e) {
			System.err.println("Caught exception " + e.toString());
		}
	}
	
	public String getSignature() { return signature; }
}