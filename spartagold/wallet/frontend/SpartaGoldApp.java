package spartagold.wallet.frontend;

import java.util.List;
import java.util.Scanner;

import spartagold.wallet.backend.SpartaGoldNode;
import spartagold.framework.*;


public class SpartaGoldApp
{
	private SpartaGoldNode peer;
	
	
	public SpartaGoldApp(String initialhost, int initialport, int maxpeers, PeerInfo mypd)
	{
		peer = new SpartaGoldNode(maxpeers, mypd);
		peer.buildPeers(initialhost, initialport, 2	);
		
		
		(new Thread() { public void run() { peer.mainLoop(); }}).start();
		
		List<PeerMessage> fileData = null;
		Scanner in = new Scanner(System.in);
		String s = in.next();
		if(s.equals("get"))
		{
			for (String pid : peer.getPeerKeys()) 
			{
				fileData = peer.sendToPeer(pid, SpartaGoldNode.FILEGET, "text.txt", true);
			}
			System.out.println(fileData);
		}
	}
	
	public static void main(String[] args)
	{
		//LoggerUtil.setHandlersLevel(Level.FINE);
		Scanner in = new Scanner(System.in);
		System.out.println("Enter your port: ");
		int myPort = in.nextInt();
		System.out.println("Enter your peer's port: ");
		int peerPort = in.nextInt();
		new SpartaGoldApp("localhost", peerPort, 5, new PeerInfo("localhost", myPort));
		in.close();
	}
}
