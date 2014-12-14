package spartagold.wallet.backend;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

import spartagold.framework.LoggerUtil;

/**
 * Verifies a signed transaction by comparing the signed byte array to the
 * original transaction string, using the sender's public key. Uses base 64 to
 * decode key.
 * 
 * @author Art Tucay Jr., Paul Portela
 * @version 1.0.0
 */

class VerifySignature
{
	private boolean verifies = false;

	/**
	 * Constructor performs process upon creation.
	 * 
	 * @param senderPubKey
	 *            String of sender's public key
	 * @param signed
	 *            byte array of the signed transaction string
	 * @param trans
	 *            String of transaction object
	 */
	public VerifySignature(String senderPubKey, byte[] signed, String trans)
	{
		/* Verify an EC signature */
		try
		{
			LoggerUtil.getLogger().fine("Verifying signature of " + trans + "...");
			/* import encoded public key */
			Base64 decoder = new Base64();
			byte[] pubArray = decoder.decode(senderPubKey);

			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubArray);

			KeyFactory keyFactory = KeyFactory.getInstance("EC");
			PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);

			/* input the signature bytes */
			byte[] sigToVerify = signed;

			/* create a Signature object and initialize it with the public key */
			Signature sig = Signature.getInstance("SHA1withECDSA");
			sig.initVerify(pubKey);

			/* Update and verify the data */

			byte[] b = trans.getBytes();
			sig.update(b);

			verifies = sig.verify(sigToVerify);

		}
		catch (Exception e)
		{
			System.err.println("Caught exception " + e.toString());
		}
	}

	/**
	 * @return a boolean of verifies instance variable
	 */
	public boolean isVerified()
	{
		LoggerUtil.getLogger().fine("Signature verified.");
		return verifies;
	}

}
