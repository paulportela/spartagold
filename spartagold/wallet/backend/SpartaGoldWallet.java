package spartagold.wallet.backend;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

import javax.swing.*;

import spartagold.framework.LoggerUtil;
import spartagold.framework.PeerInfo;
import spartagold.framework.SimplePingStabilizer;


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
	
	public void sendPersonObject(String s)
	{
		Person p = new Person(s);
		for (PeerInfo pid : peer.getAllPeers()) 
		{
			peer.connectAndSendObject(pid, SpartaGoldNode.PERSON, p);
		}
	}

	public static void main(String[] args) throws IOException
	{
		System.out.println("----------SpartaGold-------------");
		
		int thisNode = Integer.parseInt(args[0]);
		int initialNode = Integer.parseInt(args[1]);

		//LoggerUtil.setHandlersLevel(Level.FINE);
		SpartaGoldWallet s = new SpartaGoldWallet("localhost", initialNode, 5, new PeerInfo("localhost", thisNode));
		
		Scanner in = new Scanner(System.in);
		boolean running = true;
		while (running)
        {
            System.out.println(
                    "Send [P]erson\n"
                    + "[Q]uit");
            String comensar = in.next();
            if(comensar.equals("P"))
            {
                System.out.print("Name: ");
                String nombre = in.next();
                s.sendPersonObject(nombre);
            }
            if(comensar.equals("Q"))
            {
                running = false;
            }
        }
    }
}