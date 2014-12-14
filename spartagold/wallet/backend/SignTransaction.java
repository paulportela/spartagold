package spartagold.wallet.backend;

import java.io.File;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Scanner;

import org.apache.commons.codec.binary.Base64;

import spartagold.framework.LoggerUtil;

/**
 * Signs a transaction string using an EC private key and SHA-1. Decodes private
 * key with base 64.
 * 
 * @author Art Tucay Jr., Paul Portela
 * @version 1.0.0
 */

public class SignTransaction
{

	public static byte[] sign(String trans)
	{
		/* Generate a DSA signature */
		try
		{
			/* Create a Signature object and initialize it with the private key */
			/* import encoded private key */
			LoggerUtil.getLogger().fine("Signing transaction with private key...");
			Scanner privIn = new Scanner(new File("privatekey.txt"));
			String priv = privIn.next();
			privIn.close();

			Base64 decoder = new Base64();
			byte[] privArray = decoder.decode(priv);

			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(privArray);

			KeyFactory keyFactory = KeyFactory.getInstance("EC");
			PrivateKey privKey = keyFactory.generatePrivate(privKeySpec);

			/* Update and sign the data */

			Signature ecdsa = Signature.getInstance("SHA1withECDSA");

			ecdsa.initSign(privKey);
			byte[] strByte = trans.getBytes("UTF-8");
			ecdsa.update(strByte);

			/*
			 * Now that all the data to be signed has been read in, generate a
			 * signature for it
			 */

			byte[] realSig = ecdsa.sign();
			LoggerUtil.getLogger().fine("Transaction signed with private key.");
			return realSig;

		}
		catch (Exception e)
		{
			System.err.println("Caught exception " + e.toString());
		}
		return null;
	}
}