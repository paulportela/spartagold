package spartagold.wallet.backend;

import java.security.*;
import java.security.spec.*;
import org.apache.commons.codec.binary.Base64;

class VerifySignature
{
	private boolean verifies = false;

	public VerifySignature(String senderPubKey, byte[] signed, String trans)
	{
		/* Verify a DSA signature */
		try
		{
			/* import encoded public key */
			Base64 decoder = new Base64();
			byte[] pubArray = decoder.decode(senderPubKey);

			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubArray);

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
		return verifies;
	}

}
