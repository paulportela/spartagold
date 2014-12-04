package spartagold.wallet.backend;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

class VerifySignature
{
	private boolean verifies = false;

	public VerifySignature(String senderPubKey, byte[] signed, String trans)
	{
		/* Verify a DSA signature */
		try
		{
			System.out.println("Verifying signature of " + trans + "...");
			/* import encoded public key */
			Base64 decoder = new Base64();
			byte[] pubArray = decoder.decode(senderPubKey);

			PKCS8EncodedKeySpec pubKeySpec = new PKCS8EncodedKeySpec(pubArray);

			KeyFactory keyFactory = KeyFactory.getInstance("EC");
			PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);

			/* input the signature bytes */
			byte[] sigToVerify = signed;

			/* create a Signature object and initialize it with the public key */
			Signature sig = Signature.getInstance("SHA1withECDSA", "SUN");
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

	public boolean isVerified()
	{
		System.out.println("Signature verified.");
		return verifies;
	}

}
