package spartagold.wallet.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.SerializationUtils;

import spartagold.framework.HandlerInterface;
import spartagold.framework.LoggerUtil;
import spartagold.framework.Node;
import spartagold.framework.PeerConnection;
import spartagold.framework.PeerInfo;
import spartagold.framework.PeerMessage;
import spartagold.framework.RouterInterface;

/**
 * Contains all handlers of all types of messages received through the P2P
 * network.
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

	// File Names
	private String blockFileName = "blockchain";

	private boolean mining;

	private ArrayList<Transaction> transactions;
	private ArrayList<Transaction> myTransactions;
	private BlockChain blockChain;

	private String status;

	public SpartaGoldNode(int maxPeers, PeerInfo myInfo)
	{
		super(maxPeers, myInfo);

		// Read blockchain from file
		try
		{
			boolean checkBlockchainFile = new File(blockFileName).exists();
			if (checkBlockchainFile)
			{
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(blockFileName));
				this.blockChain = (BlockChain) in.readObject();
				in.close();
			}
			else
			{
				blockChain = new BlockChain();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		transactions = new ArrayList<Transaction>();
		transactions.add(new Transaction("000", 2));

		myTransactions = new ArrayList<Transaction>();
		initializeMyTransactions();

		mining = false;

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
	 * @param host
	 *            the IP address of the peer
	 * @param port
	 *            the port the peer is listening on
	 * @param hops
	 *            the hops this peer will travel to build its peer list
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

		String resp = this.connectAndSend(pd, INSERTPEER, String.format("%s %s %d", this.getId(), this.getHost(), this.getPort()), true).get(0).getMsgType();
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
			peerconn.sendData(new PeerMessage(REPLY, String.format("%d", peer.getNumberOfPeers())));
			for (String pid : peer.getPeerKeys())
			{
				peerconn.sendData(new PeerMessage(REPLY, String.format("%s %s %d", pid, peer.getPeer(pid).getHost(), peer.getPeer(pid).getPort())));
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
			}
			else
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
			System.out.println("SolutionFoundHandler: FOUNDSOLUTION message received. Deserializing...");
			Block b = (Block) SerializationUtils.deserialize(msg.getMsgDataBytes());

			boolean solution = false;
			try
			{
				System.out.println("SolutionFoundHandler: Verifying block...");
				solution = Verify.verifyBlock(b);
				System.out.println("SolutionFoundHandler: Block verification: " + solution);
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
				if (!blockChain.contains(b))
				{
					System.out.println("SolutionFoundHandler: Block not in block chain yet.");
					mining = false;

					// remove transaction from unconfirmed transactions
					System.out.println("SolutionFoundHandler: Removing second transaction (non-reward) from this.transactions.");
					transactions.remove(b.getTransactions().get(1));

					System.out.println("SolutionFoundHandler: Creating unspentIds list from second transaction.");
					ArrayList<String> unspentIds = b.getTransactions().get(1).getUnspentIds();
					for (int i = 0; i < blockChain.getChainSize(); i++)
					{
						Block tempBlock = blockChain.getChain().get(i);
						System.out.println("SolutionFoundHandler: Block ID: " + tempBlock.getId());
						System.out.println("SolutionFoundHandler: Block solution: " + tempBlock.getSolution());
						for (Transaction t : tempBlock.getTransactions())
						{
							System.out.println("SolutionFoundHandler: Transaction ID: " + t.getID());
							System.out.println("SolutionFoundHandler: Transaction amount: " + t.getAmount());
							System.out.println("SolutionFoundHandler: All unspentIds: " + unspentIds);
							for (String id : unspentIds)
							{
								System.out.println("SolutionFoundHandler: Unspent ID: " + id);
								if (id.equals(t.getID()))
								{
									System.out.println("SolutionFoundHandler: Unspent ID matches transaction ID.");
									t.setSpent(true);
									System.out.println("SolutionFoundHandler: Transaction set to spent.");
								}
							}
						}
					}
					System.out.println("SolutionFoundHandler: Adding block to block chain.");
					blockChain.addBlock(b);
					for (PeerInfo pid : peer.getAllPeers())
					{
						System.out.println("Broadcasting to " + pid.toString());
						peer.connectAndSendObject(pid, FOUNDSOLUTION, b);
					}
					System.out.println("Miner.java: my balance: " + getBalance());
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
			System.out.println("TransactionHandler: TRANSACTION message received. Deserializing...");
			Transaction t = (Transaction) SerializationUtils.deserialize(msg.getMsgDataBytes());

			boolean valid = false;
			try
			{
				System.out.println("TransactionHandler: Verifying transaction...");
				valid = Verify.verifyTransaction(t, blockChain);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			if (!transactions.contains(t) && valid)
			{
				System.out.println("TransactionHandler: Transaction is verified and not added to transactions.");
				transactions.add(t);
				System.out.println("TransactionHandler: Added transaction amount: " + t.getAmount());
				// Read in public key
				String pub = readPubKey();
				if (pub.equals(t.getReceiverPubKey()) || pub.equals(t.getSenderPubKey()))
				{
					System.out.println("TransactionHandler: my public key matches receiver or sender public key.");
					System.out.println("TransactionHandler: Adding transaction to myTransactions");
					myTransactions.add(t);
				}

				for (PeerInfo pid : peer.getAllPeers())
				{
					LoggerUtil.getLogger().fine("Broadcasting to " + pid.toString());
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
			// TODO Compare sizes of blockchain also figure out how to send a
			// request ledger when gui starts up
			peerconn.sendData(new PeerMessage(REPLY, dataObject));
		}
	}

	public Transaction getTransaction()
	{
		if (!transactions.isEmpty())
			return transactions.get(0);
		else
			return null;
	}

	public BlockChain getBlockChain()
	{
		return blockChain;
	}

	public void setBlockchain(BlockChain bc)
	{
		this.blockChain = bc;
	}

	public void saveBlockchain()
	{
		try
		{
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(blockFileName));
			out.writeObject(this.blockChain);
			out.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public ArrayList<Transaction> getMyTransactions()
	{
		return myTransactions;
	}

	public void initializeMyTransactions()
	{
		String pub = readPubKey();
		for (int i = 0; i < blockChain.getChainSize(); i++)
		{
			Block tempBlock = blockChain.getChain().get(i);
			for (Transaction t : tempBlock.getTransactions())
			{
				if (pub.equals(t.getReceiverPubKey()) || pub.equals(t.getSenderPubKey()))
					myTransactions.add(t);
			}
		}
	}

	public String readPubKey()
	{
		Scanner pubIn;
		try
		{
			pubIn = new Scanner(new File("publickey.txt"));
			String pub = pubIn.next();
			pubIn.close();
			return pub;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public boolean isMining()
	{
		return mining;
	}

	public void setMining(boolean b)
	{
		mining = b;
	}

	public void setStatus(String s)
	{
		status = s;
	}

	public String getStatus()
	{
		return status;
	}

	public double getBalance()
	{
		double balance = 0;
		for (int i = 0; i < blockChain.getChainSize(); i++)
		{
			Block tempBlock = blockChain.getChain().get(i);
			for (Transaction t : tempBlock.getTransactions())
			{
				if (t.getReceiverPubKey().equals(readPubKey()) && t.isSpent() == false)
					balance += t.getAmount();
			}
		}
		return balance;
	}

	public void addMyTransaction(Transaction t)
	{
		myTransactions.add(t);
	}
}
