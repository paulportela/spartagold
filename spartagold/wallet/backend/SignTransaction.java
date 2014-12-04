package spartagold.wallet.backend;

import java.io.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;

import org.apache.commons.codec.binary.Base64;

public class SignTransaction
{

	public static byte[] sign(String trans)
	{
		/* Generate a DSA signature */
		try
		{
			/* Create a Signature object and initialize it with the private key */
			/* import encoded private key */

			Scanner privIn = new Scanner(new File("privatekey.txt"));
			String priv = privIn.next();
			privIn.close();

			Base64 decoder = new Base64();
			byte[] privArray = decoder.decode(priv);

			X509EncodedKeySpec privKeySpec = new X509EncodedKeySpec(privArray);

			KeyFactory keyFactory = KeyFactory.getInstance("EC");
			PrivateKey privKey = keyFactory.generatePrivate(privKeySpec);

			/* Update and sign the data */

			Signature dsa = Signature.getInstance("SHA1withECDSA");

			dsa.initSign(privKey);
			byte[] strByte = trans.getBytes("UTF-8");
			dsa.update(strByte);

			/*
			 * Now that all the data to be signed has been read in, generate a
			 * signature for it
			 */

			byte[] realSig = dsa.sign();

			return realSig;

		} 
		catch (Exception e)
		{
			System.err.println("Caught exception " + e.toString());
		}
		return null;
	}
}