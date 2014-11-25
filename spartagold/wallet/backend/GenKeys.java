package spartagold.wallet.backend;

import java.io.*;
import java.security.*;

public class GenKeys {
	
	public GenKeys() {
		/* Generate a DSA signature */
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
			
			SecureRandom random = SecureRandom.getInstance("SHA256PRNG", "SUN");
			keyGen.initialize(1024, random);
			
			KeyPair pair = keyGen.generateKeyPair();
			PrivateKey priv = pair.getPrivate();
			PublicKey pub = pair.getPublic();

			/* Save the private key in a file */
			byte[] privkey = priv.getEncoded();
			FileOutputStream privfos = new FileOutputStream("privatekey");
			privfos.write(privkey);

			privfos.close();

			/* Save the public key in a file */
			byte[] pubkey = pub.getEncoded();
			FileOutputStream pubfos = new FileOutputStream("publickey");
			pubfos.write(pubkey);

			pubfos.close();
		}
		catch (Exception e) {
			System.err.println("Caught exception " + e.toString());
		}
	}
}