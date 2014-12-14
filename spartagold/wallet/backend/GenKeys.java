package spartagold.wallet.backend;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;

/**
 * Generates public and private key pair with Eliptic Curve (EC), then encodes
 * keys into base 64 and saves each key into a local text file for readability.
 * 
 * @author Art Tucay Jr., Paul Portela
 * @version 1.0.0
 */

public class GenKeys
{

	public static void generateKeys()
	{
		/* Generate a DSA signature */
		try
		{
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

			keyGen.initialize(256, random);

			KeyPair pair = keyGen.generateKeyPair();
			PrivateKey priv = pair.getPrivate();
			PublicKey pub = pair.getPublic();

			/* Save the private key in a file */
			byte[] privArray = priv.getEncoded();
			Base64 encoder1 = new Base64();
			String s1 = encoder1.encodeToString(privArray);
			BufferedWriter writer1 = new BufferedWriter(new FileWriter("privatekey.txt"));
			writer1.write(s1);
			writer1.close();

			/* Save the public key in a file */
			byte[] pubArray = pub.getEncoded();
			Base64 encoder2 = new Base64();
			String s2 = encoder2.encodeToString(pubArray);
			BufferedWriter writer2 = new BufferedWriter(new FileWriter("publickey.txt"));
			writer2.write(s2);
			writer2.close();
		}
		catch (Exception e)
		{
			System.err.println("Caught exception " + e.toString());
		}
	}
}