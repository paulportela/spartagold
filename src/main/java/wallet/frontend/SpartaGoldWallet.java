package main.java.wallet.frontend;


import java.io.*;
import javax.swing.*;

import main.java.framework.PeerInfo;
import main.java.wallet.backend.SpartaGoldNode;


@SuppressWarnings("serial")
public class SpartaGoldWallet extends JFrame
{
	private SpartaGoldNode peer;


	private SpartaGoldWallet(String initialhost, int initialport, int maxpeers, PeerInfo mypd)
	{
		peer = new SpartaGoldNode(maxpeers, mypd);
		peer.buildPeers(initialhost, initialport, 2);

		(new Thread() { public void run() { peer.mainLoop(); }}).start();
		
	}

	public static void main(String[] args) throws IOException
	{
		System.out.println("----------SpartaGold-------------");
		
		if(args.length != 2)
		{
			System.out.println("java SpartaGoldWallet port1 port2");
		}
		else
		{
			int thisNode = Integer.parseInt(args[0]);
			int initialNode = Integer.parseInt(args[1]);
	
			//LoggerUtil.setHandlersLevel(Level.FINE);
			new SpartaGoldWallet("localhost", initialNode, 5, new PeerInfo("localhost", thisNode));
		}
    }
}