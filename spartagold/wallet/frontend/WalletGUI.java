package spartagold.wallet.frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import spartagold.framework.LoggerUtil;
import spartagold.framework.PeerInfo;
import spartagold.wallet.backend.GenKeys;
import spartagold.wallet.backend.Miner;
import spartagold.wallet.backend.SpartaGoldNode;
import spartagold.wallet.backend.Transaction;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.URL;

/**
 * Front end graphical user interface that accesses all functionality of the
 * SpartaGold Wallet, then broadcasts messages to known peers. A connection is
 * established at the start of the Wallet, along with a unique public and
 * private key.
 * 
 * @author Art Tucay Jr., Paul Portela
 * @version 1.0.0
 */

public class WalletGUI
{
	private JTextArea taMineFeed;
	private JFrame frmSpartagoldWallet;
	private JTextField tfAmount;
	private JTextField tfAddress;
	// private static String myIpAddress;
	private double myBalance;
	private JTable table;
	private Object[][] previousTransactions;
	private String[] transactionColumns = { "Date and Time", "Status", "To/From Address", "Amount" };
	private SpartaGoldNode peer;

	private JLabel lblBalance;
	private JLabel lblWalletAmount;
	private JPanel transactions;
	private JTabbedPane tabbedPane;

	/**
	 * Launch the application.
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		LoggerUtil.setHandlersLevel(Level.FINE);
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					// myIpAddress = getIpAddress();
					boolean checkPubKey = new File("publickey.txt").exists();
					boolean checkPrivKey = new File("privatekey.txt").exists();
					if (!checkPubKey && !checkPrivKey)
					{
						LoggerUtil.getLogger().fine("Generating keys...");
						GenKeys.generateKeys();
						LoggerUtil.getLogger().fine("Public and private keys generated.");
					}
					LoggerUtil.getLogger().fine("Connecting to SpartaGold network...");

					WalletGUI window = new WalletGUI("localhost", 9008, 5, new PeerInfo("localhost", 9009));

					window.frmSpartagoldWallet.setVisible(true);

				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @throws Exception
	 */
	public WalletGUI(String initialhost, int initialport, int maxpeers, PeerInfo mypd) throws Exception
	{
		peer = new SpartaGoldNode(maxpeers, mypd);
		peer.buildPeers(initialhost, initialport, 2);
		(new Thread()
		{
			public void run()
			{
				peer.mainLoop();
			}
		}).start();

		new javax.swing.Timer(3000, new RefreshListener()).start();

		// //Requesting blockchain from peers
		// for (PeerInfo pid : peer.getAllPeers())
		// {
		// List<PeerMessage> msg = peer.connectAndSend(pid,
		// SpartaGoldNode.GETBLOCKCHAIN, null, true);
		// BlockChain bc = (BlockChain)
		// SerializationUtils.deserialize(msg.get(0).getMsgDataBytes());
		// if(peer.getBlockChain().getChainSize() < bc.getChainSize())
		// peer.setBlockchain(bc);
		// }

		// GUI start
		frmSpartagoldWallet = new JFrame();
		frmSpartagoldWallet.setBackground(new Color(11, 46, 70));
		frmSpartagoldWallet.getContentPane().setBackground(new Color(11, 46, 70));
		frmSpartagoldWallet.setTitle("SpartaGold Wallet");
		frmSpartagoldWallet.setResizable(false);
		frmSpartagoldWallet.setBounds(100, 100, 625, 350);
		frmSpartagoldWallet.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frmSpartagoldWallet.setUndecorated(true);

		frmSpartagoldWallet.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				LoggerUtil.getLogger().fine("Saving block chain...");
				peer.saveBlockchain();
			}
		});

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setForeground(new Color(11, 46, 70));
		tabbedPane.setBackground(new Color(11, 46, 70));
		frmSpartagoldWallet.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JPanel send = new JPanel();
		send.setBackground(Color.BLACK);
		tabbedPane.addTab("Send", null, send, null);
		tabbedPane.setBackgroundAt(0, Color.WHITE);
		send.setLayout(null);

		transactions = new JPanel();
		transactions.setBackground(Color.BLACK);
		tabbedPane.addTab("Transactions", null, transactions, null);
		tabbedPane.setBackgroundAt(1, Color.WHITE);
		transactions.setLayout(null);

		fillTransactionTable();
		table = new JTable(previousTransactions, transactionColumns);
		table.setShowHorizontalLines(false);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(5, 5, 605, 250);
		table.setFillsViewportHeight(true);
		transactions.add(scrollPane);

		JPanel mine = new JPanel();
		mine.setBackground(Color.BLACK);
		tabbedPane.addTab("Mine", null, mine, null);
		tabbedPane.setBackgroundAt(2, Color.WHITE);
		mine.setLayout(null);

		BufferedImage pickaxe1 = ImageIO.read(new File("/home/paul/workspace/P2P/src/spartagold/img/pickaxe2.gif"));
		Image scaledPickaxe1 = pickaxe1.getScaledInstance(pickaxe1.getWidth(), pickaxe1.getHeight(), Image.SCALE_SMOOTH);
		JLabel pickaxeLabel = new JLabel(new ImageIcon(scaledPickaxe1));
		pickaxeLabel.setBounds(370, -18, 200, 200);
		mine.add(pickaxeLabel);

		BufferedImage mineBG = ImageIO.read(new File("/home/paul/workspace/P2P/src/spartagold/img/defaultbackground.png"));
		Image scaledMineBG = mineBG.getScaledInstance(mineBG.getWidth(), mineBG.getHeight(), Image.SCALE_SMOOTH);

		BufferedImage transactionBG = ImageIO.read(new File("/home/paul/workspace/P2P/src/spartagold/img/defaultbackground.png"));
		Image scaledTransactionBG = transactionBG.getScaledInstance(transactionBG.getWidth(), transactionBG.getHeight(), Image.SCALE_SMOOTH);
		JLabel transactionBGLabel = new JLabel(new ImageIcon(scaledTransactionBG));
		transactionBGLabel.setBounds(2, 2, 610, 290);
		transactions.add(transactionBGLabel);

		taMineFeed = new JTextArea(80, 80);
		taMineFeed.setLineWrap(true);
		taMineFeed.setWrapStyleWord(true);
		taMineFeed.setText("To begin mining, click the gold button.");
		taMineFeed.setRows(80);
		taMineFeed.setBounds(10, 10, 314, 273);
		mine.add(taMineFeed);
		taMineFeed.setEditable(false);
		// taMineFeed.append("");
		taMineFeed.setCaretPosition(taMineFeed.getText().length() - 1);

		BufferedImage mineButtonIcon = ImageIO.read(new File("/home/paul/workspace/P2P/src/spartagold/img/mineButton.png"));
		JButton btnMine = new JButton(new ImageIcon(mineButtonIcon));
		btnMine.setBorder(BorderFactory.createEmptyBorder());
		btnMine.setContentAreaFilled(false);
		btnMine.setBounds(370, 175, 200, 75);
		mine.add(btnMine);
		JLabel mineBGLabel = new JLabel(new ImageIcon(scaledMineBG));
		mineBGLabel.setBounds(2, 2, 610, 290);
		mine.add(mineBGLabel);

		btnMine.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				peer.addMyTransaction(new Transaction("000", 23));
				System.out.println("Mining for Gold selected. Creating Miner object...");
				Miner m = null;
				System.out.println("Miner.java: my balance: " + peer.getBalance());
				try
				{
					m = new Miner(peer);
					System.out.println("WalletGUI trying to mine if this is true: " + m.hasATransactionToVerify());
					if (m.hasATransactionToVerify())
					{
						Runnable r = m;
						System.out.println("Runnable created. Creating thread...");
						Thread t = new Thread(r);
						t.start();
					}
					else
					{
						System.out.println("Currently there are no transactions to comfirm");
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});

		BufferedImage sendBackground = ImageIO.read(new File("/home/paul/workspace/P2P/src/spartagold/img/sendbackground.png"));
		Image scaledSendBackground = sendBackground.getScaledInstance(sendBackground.getWidth(), sendBackground.getHeight(), Image.SCALE_SMOOTH);

		BufferedImage logo = ImageIO.read(new File("/home/paul/workspace/P2P/src/spartagold/img/spartagoldlogo02.png"));
		Image scaledLogo = logo.getScaledInstance(logo.getWidth(), logo.getHeight(), Image.SCALE_SMOOTH);
		JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
		logoLabel.setBounds(79, 170, 450, 112);
		send.add(logoLabel);

		JLabel lblSG2 = new JLabel("SG");
		lblSG2.setBounds(541, 83, 44, 39);
		send.add(lblSG2);
		lblSG2.setForeground(Color.WHITE);
		lblSG2.setFont(new Font("Segoe UI Semibold", Font.BOLD, 27));

		lblBalance = new JLabel("Balance:");
		lblBalance.setBounds(455, 42, 109, 26);
		send.add(lblBalance);
		lblBalance.setForeground(Color.WHITE);
		lblBalance.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));

		BufferedImage sendButtonIcon = ImageIO.read(new File("/home/paul/workspace/P2P/src/spartagold/img/sendButton.png"));
		JButton btnSend = new JButton(new ImageIcon(sendButtonIcon));
		btnSend.setBounds(291, 80, 100, 38);
		send.add(btnSend);
		btnSend.setBorder(BorderFactory.createEmptyBorder());
		btnSend.setContentAreaFilled(false);

		tfAmount = new JTextField();
		tfAmount.setBounds(110, 100, 100, 20);
		send.add(tfAmount);
		tfAmount.setColumns(10);

		JLabel lblAmount = new JLabel("Amount:");
		lblAmount.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblAmount.setBounds(35, 95, 82, 26);
		send.add(lblAmount);
		lblAmount.setForeground(Color.WHITE);

		JLabel lblAddress = new JLabel("Address:");
		lblAddress.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblAddress.setBounds(35, 50, 75, 26);
		send.add(lblAddress);
		lblAddress.setForeground(Color.WHITE);

		JLabel lblSG1 = new JLabel("SG");
		lblSG1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblSG1.setBounds(229, 95, 20, 26);
		send.add(lblSG1);
		lblSG1.setForeground(Color.WHITE);

		tfAddress = new JTextField();
		tfAddress.setBounds(110, 55, 285, 20);
		send.add(tfAddress);
		tfAddress.setColumns(10);

		lblWalletAmount = new JLabel(Double.toString(myBalance));
		lblWalletAmount.setBounds(421, 85, 122, 39);
		send.add(lblWalletAmount);
		lblWalletAmount.setForeground(Color.WHITE);
		lblWalletAmount.setFont(new Font("Segoe UI Light", Font.BOLD, 24));
		JLabel sendBackgroundLabel = new JLabel(new ImageIcon(scaledSendBackground));
		sendBackgroundLabel.setBounds(2, 2, 610, 290);
		send.add(sendBackgroundLabel);

		btnSend.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if (tfAmount.getText() != "" && tfAddress.getText() != "")
				{
					LoggerUtil.getLogger().fine("Sending transaction " + tfAmount.getText() + " to " + tfAddress.getText());
					Transaction t = new Transaction(tfAddress.getText(), Double.parseDouble(tfAmount.getText()));
					t.addUnspentIds(peer.getBlockChain());
					for (PeerInfo pid : peer.getAllPeers())
					{
						LoggerUtil.getLogger().fine("Broadcasting...");
						peer.connectAndSendObject(pid, SpartaGoldNode.TRANSACTION, t);
					}
				}
				tfAmount.setText("");
				tfAddress.setText("");
			}
		});

		// GUI end
	}

	private void fillTransactionTable()
	{
		previousTransactions = new Object[peer.getMyTransactions().size()][4];
		for (int i = 0; i < peer.getMyTransactions().size(); i++)
		{
			previousTransactions[i][0] = peer.getMyTransactions().get(i).getDate();
			if (peer.getMyTransactions().get(i).getSenderPubKey().equals(peer.readPubKey()))
			{
				previousTransactions[i][1] = "Sent";
				previousTransactions[i][2] = peer.getMyTransactions().get(i).getReceiverPubKey();
			}
			else if (peer.getMyTransactions().get(i).getReceiverPubKey().equals(peer.readPubKey()))
			{
				previousTransactions[i][1] = "Received";
				previousTransactions[i][2] = peer.readPubKey();
			}
			else
			{
				previousTransactions[i][1] = "Unknown";
				previousTransactions[i][2] = peer.getMyTransactions().get(i).getSenderPubKey();
			}
			previousTransactions[i][3] = peer.getMyTransactions().get(i).getAmount();
		}
		System.out.println("Transaction table filled.");
	}

	class RefreshListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			lblWalletAmount.setText(Double.toString(peer.getBalance()));

			fillTransactionTable();
			table = new JTable(previousTransactions, transactionColumns);
			table.setShowHorizontalLines(false);

			JScrollPane scrollPane = new JScrollPane(table);
			scrollPane.setBounds(5, 5, 605, 250);
			table.setFillsViewportHeight(true);
			transactions.add(scrollPane);
			System.out.println(peer.getMyTransactions());
		}
	}

	/**
	 * public static String getIpAddress() { URL myIP; try { myIP = new
	 * URL("http://myip.dnsomatic.com/");
	 * 
	 * BufferedReader in = new BufferedReader(new
	 * InputStreamReader(myIP.openStream())); return in.readLine(); } catch
	 * (Exception e) { try { myIP = new URL("http://api.externalip.net/ip/");
	 * 
	 * BufferedReader in = new BufferedReader(new
	 * InputStreamReader(myIP.openStream())); return in.readLine(); } catch
	 * (Exception e1) { try { myIP = new URL("http://icanhazip.com/");
	 * 
	 * BufferedReader in = new BufferedReader(new
	 * InputStreamReader(myIP.openStream())); myIpAddress = in.readLine();
	 * return in.readLine(); } catch (Exception e2) { e2.printStackTrace(); } }
	 * } return null; }
	 */
}
