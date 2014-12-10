package spartagold.wallet.backend;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import spartagold.framework.HandlerInterface;
import spartagold.framework.LoggerUtil;
import spartagold.framework.Node;
import spartagold.framework.PeerConnection;
import spartagold.framework.PeerInfo;
import spartagold.framework.PeerMessage;
import spartagold.framework.RouterInterface;
import org.apache.commons.lang3.SerializationUtils;

/**
 * Contains all handlers of all types of messages received through the P2P network.
 * 
 * @author Art Tucay Jr., Paul Portela
 * @version 1.0.0
 */

@SuppressWarnings("serial")
public class SpartaGoldNode extends Node implements Serializable
{
	// Message Types
	public static final String INSERTPEER = "JOIN";
	public static final String LISTPEER = "LIST";
	public static final String PEERNAME = "NAME";
	public static final String PEERQUIT = "QUIT";
	public static final String FOUNDSOLUTION = "HSOL";
	public static final String TRANSACTION = "TRAN";
	public static final String GETBLOCKCHAIN = "CHIN";
	public static final String PERSON = "PERS";

	public static final String REPLY = "REPL";
	public static final String ERROR = "ERRO";

	//private boolean mining;

	private ArrayList<Transaction> transactions;
	private BlockChain blockChain;

	public SpartaGoldNode(int maxPeers, PeerInfo myInfo)
	{
		super(maxPeers, myInfo);
		transactions = new ArrayList<>();
		blockChain = new BlockChain();
		//mining = false;

		this.addRouter(new Router(this));

		// Handlers
		this.addHandler(INSERTPEER, new JoinHandler(this));
		this.addHandler(PEERNAME, new NameHandler(this));
		this.addHandler(PEERQUIT, new QuitHandler(this));
		this.addHandler(LISTPEER, new ListHandler(this));

		this.addHandler(TRANSACTION, new TransactionHandler(this));
		this.addHandler(FOUNDSOLUTION, new SolutionFoundHandler(this));
		this.addHandler(GETBLOCKCHAIN, new BlockChainHandler(this));

	}

	/**
	 * This connects with a peer and then it requests the list of known of that
	 * peer. Then it connects with those peers doing a depth first search in
	 * that list.
	 * 
	 * @param host the IP address of the peer
	 * @param port the port the peer is listening on
	 * @param hops the hops this peer will travel to build its peer list
	 */
	public void buildPeers(String host, int port, int hops)
	{
		LoggerUtil.getLogger().fine("build peers");

		if (this.maxPeersReached() || hops <= 0)
			return;
		PeerInfo pd = new PeerInfo(host, port);
		List<PeerMessage> resplist = this.connectAndSend(pd, PEERNAME, "", true);
		if (resplist == null || resplist.size() == 0)
			return;
		String peerid = resplist.get(0).getMsgData();
		LoggerUtil.getLogger().fine("contacted " + peerid);
		pd.setId(peerid);

		String resp = this.connectAndSend(pd, INSERTPEER, String.format("%s %s %d", this.getId(), this.getHost(),
								this.getPort()), true).get(0).getMsgType();
		if (!resp.equals(REPLY) || this.getPeerKeys().contains(peerid))
			return;

		this.addPeer(pd);

		// do recursive depth first search to add more peers
		resplist = this.connectAndSend(pd, LISTPEER, "", true);

		if (resplist.size() > 1)
		{
			resplist.remove(0);
			for (PeerMessage pm : resplist)
			{
				String[] data = pm.getMsgData().split("\\s");
				String nextpid = data[0];
				String nexthost = data[1];
				int nextport = Integer.parseInt(data[2]);
				if (!nextpid.equals(this.getId()))
					buildPeers(nexthost, nextport, hops - 1);
			}
		}
	}

	private class JoinHandler implements HandlerInterface
	{
		private Node peer;

		public JoinHandler(Node peer)
		{
			this.peer = peer;
		}

		public void handleMessage(PeerConnection peerconn, PeerMessage msg)
		{
			if (peer.maxPeersReached())
			{
				LoggerUtil.getLogger().fine("maxpeers reached " + peer.getMaxPeers());
				peerconn.sendData(new PeerMessage(ERROR, "Join: " + "too many peers"));
				return;
			}

			String[] data = msg.getMsgData().split("\\s");

			// parse arguments into PeerInfo structure
			PeerInfo info = new PeerInfo(data[0], data[1], Integer.parseInt(data[2]));

			if (peer.getPeer(info.getId()) != null)
				peerconn.sendData(new PeerMessage(ERROR, "Join: " + "peer already inserted"));
			else if (info.getId().equals(peer.getId()))
				peerconn.sendData(new PeerMessage(ERROR, "Join: " + "attempt to insert self"));
			else
			{
				peer.addPeer(info);
				peerconn.sendData(new PeerMessage(REPLY, "Join: " + "peer added: " + info.getId()));
			}
		}
	}

	/**
	 * Sends this node's peer information to the requester.
	 */
	private class NameHandler implements HandlerInterface
	{
		private Node peer;

		public NameHandler(Node peer)
		{
			this.peer = peer;
		}

		public void handleMessage(PeerConnection peerconn, PeerMessage msg)
		{
			peerconn.sendData(new PeerMessage(REPLY, peer.getId()));
		}
	}

	/**
	 * Sends this peer's list of peers.
	 */
	private class ListHandler implements HandlerInterface
	{
		private Node peer;

		public ListHandler(Node peer)
		{
			this.peer = peer;
		}

		public void handleMessage(PeerConnection peerconn, PeerMessage msg)
		{
			peerconn.sendData(new PeerMessage(REPLY, String.format("%d",
					peer.getNumberOfPeers())));
			for (String pid : peer.getPeerKeys())
			{
				peerconn.sendData(new PeerMessage(REPLY, String.format(
						"%s %s %d", pid, peer.getPeer(pid).getHost(), peer
								.getPeer(pid).getPort())));
			}
		}
	}

	private class Router implements RouterInterface
	{
		private Node peer;

		public Router(Node peer)
		{
			this.peer = peer;
		}

		public PeerInfo route(String peerid)
		{
			if (peer.getPeerKeys().contains(peerid))
				return peer.getPeer(peerid);
			else
				return null;
		}
	}

	/**
	 * Removes any peer from its peers list that have log off.
	 */
	private class QuitHandler implements HandlerInterface
	{
		private Node peer;

		public QuitHandler(Node peer)
		{
			this.peer = peer;
		}

		public void handleMessage(PeerConnection peerconn, PeerMessage msg)
		{
			String pid = msg.getMsgData().trim();
			if (peer.getPeer(pid) == null)
			{
				// Doesn't do anything
				return;
			} else
			{
				peer.removePeer(pid);
			}
		}
	}

	/**
	 * Handles a solution of the proof-of-work by verifying it.
	 */
	private class SolutionFoundHandler implements HandlerInterface
	{
		private Node peer;

		public SolutionFoundHandler(Node peer)
		{
			this.peer = peer;
		}

		public void handleMessage(PeerConnection peerconn, PeerMessage msg)
		{
			System.out.println("FOUNDSOLUTION message received. Deserializing...");
			Block b = (Block) SerializationUtils.deserialize(msg.getMsgDataBytes());
			
			boolean solution = false;
			try
			{
				System.out.println("Verifying block...");
				solution = Verify.verifyBlock(b);
			} 
			catch (NoSuchAlgorithmException e)
			{
				e.printStackTrace();
			}

			if (!solution)
			{
				peerconn.sendData(new PeerMessage(ERROR, "Not a solution"));
			}
			else
			{
				if(!blockChain.contains(b))
				{
					
					blockChain.addBlock(b);
					for (PeerInfo pid : peer.getAllPeers())
					{
						System.out.println("Broadcasting to " + pid.toString());
						peer.connectAndSendObject(pid, FOUNDSOLUTION , b);
					}
				}
			}
		}
	}

	private class TransactionHandler implements HandlerInterface
	{
		private Node peer;

		public TransactionHandler(Node peer)
		{
			this.peer = peer;
		}

		public void handleMessage(PeerConnection peerconn, PeerMessage msg)
		{
			System.out.println("TRANSACTION message received. Deserializing...");
			Transaction t = (Transaction) SerializationUtils.deserialize(msg.getMsgDataBytes());
			
			boolean valid = false;
			try
			{
				System.out.println("Verifying transaction...");
				valid = Verify.verifyTransaction(t, blockChain);
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			if (!transactions.contains(t) && valid)
			{
				transactions.add(t);
				for (PeerInfo pid : peer.getAllPeers())
				{
					System.out.println("Broadcasting to " + pid.toString());
					peer.connectAndSendObject(pid, TRANSACTION, t);
				}
			} 
			else
			{
				peerconn.sendData(new PeerMessage(ERROR, "Transaction not verified."));
			}
		}
	}

	/**
	 * Sending someone my BlockChain
	 *
	 */
	private class BlockChainHandler implements HandlerInterface
	{
		@SuppressWarnings("unused")
		private Node peer;

		public BlockChainHandler(Node peer)
		{
			this.peer = peer;
		}

		public void handleMessage(PeerConnection peerconn, PeerMessage msg)
		{
			byte[] dataObject = SerializationUtils.serialize(blockChain);
			//Compare sizes of blockchain also figure out how to send a request ledger when gui starts up
			peerconn.sendData(new PeerMessage(REPLY, dataObject));
		}
	}
	
	public Transaction getTransaction() 
	{
		if(!transactions.isEmpty()) return transactions.get(0);
		else return null;
	}
	
	public BlockChain getBlockChain()
	{
		return blockChain;
	}
}
