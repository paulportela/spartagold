package spartagold.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import spartagold.wallet.backend.SignTransaction;

public class testsignature
{

	public static void main(String[] args)
	{
		System.out.println("Signing string: qwerty");
		String str = "qwerty";
		byte[] signed = SignTransaction.sign(str);
		String pubkey = "";
		System.out.println("Reading in public key");
		Scanner pubIn;
		try
		{
			pubIn = new Scanner(new File("publickey.txt"));
			String pub = pubIn.next();
			pubIn.close();
			pubkey = pub;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		System.out.println("Verifying signature");
		VerifySignature ver = new VerifySignature(pubkey, signed, str);
		System.out.println("Signature verified: " + ver.isVerified());
	}

}
