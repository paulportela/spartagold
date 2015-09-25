package main.java.framework;




import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import peerbase.*;

/**
 * A simple stabilization routine that simply sends "PING" messages to
 * every peer in a node's list of peers, and removes from the list any
 * that fail to accept the connection. Whether peers <i>reply</i> to the
 * actual message or not does not matter to this stabilizer.
 * 
 * @author Nadeem Abdul Hamid
 *
 */
public class SimplePingStabilizer implements StabilizerInterface {
	private Node peer;
	private String msgtype;
	
	public SimplePingStabilizer(Node peer) {
		this(peer, "PING");
	}
	
	public SimplePingStabilizer(Node peer, String msgtype) {
		this.peer = peer;
		this.msgtype = msgtype;
	}
	
	public void stabilizer() {
		List<String> todelete = new ArrayList<String>();
		for (String pid : peer.getPeerKeys()) {
			boolean isconn = false;
			PeerConnection peerconn = null;
			try {
				peerconn = new PeerConnection(peer.getPeer(pid));
				peerconn.sendData(new PeerMessage(msgtype, ""));
				isconn = true;
			}
			catch (IOException e) {
				todelete.add(pid);
			}
			if (isconn)
				peerconn.close();
		}
		
		for (String pid : todelete) {
			peer.removePeer(pid);
		}
	}
}

