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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
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
import javax.swing.UIManager;

import spartagold.framework.LoggerUtil;
import spartagold.framework.PeerInfo;
import spartagold.wallet.backend.GenKeys;
import spartagold.wallet.backend.Miner;
import spartagold.wallet.backend.SpartaGoldNode;
import spartagold.wallet.backend.Transaction;

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
	private static String myIpAddress;
	private double myBalance;
	private JTable table;
	private Object[][] previousTransactions;
	private String[] transactionColumns = { "Date and Time", "Status", "To/From Address", "Amount" };
	private SpartaGoldNode peer;
	private ArrayList<Transaction> myTransactionsList;

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
					myIpAddress = getIpAddress();
					boolean checkPubKey = new File("publickey.txt").exists();
					boolean checkPrivKey = new File("privatekey.txt").exists();
					if (!checkPubKey && !checkPrivKey)
					{
						LoggerUtil.getLogger().fine("Generating keys...");
						GenKeys.generateKeys();
						LoggerUtil.getLogger().fine("Public and private keys generated.");
					}
					LoggerUtil.getLogger().fine("Connecting to SpartaGold network...");

					WalletGUI window = new WalletGUI("localhost", 9004, 5, new PeerInfo("localhost", 9005));

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

		myTransactionsList = peer.getMyTransactions();
		myBalance = getBalance();

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

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setForeground(new Color(11, 46, 70));
		tabbedPane.setBackground(new Color(11, 46, 70));
		frmSpartagoldWallet.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JPanel send = new JPanel();
		send.setBackground(new Color(11, 46, 70));
		tabbedPane.addTab("Send", null, send, null);
		tabbedPane.setBackgroundAt(0, Color.WHITE);
		send.setLayout(null);

		JPanel transactions = new JPanel();
		transactions.setBackground(new Color(11, 46, 70));
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
		mine.setBackground(new Color(11, 46, 70));
		tabbedPane.addTab("Mine", null, mine, null);
		tabbedPane.setBackgroundAt(2, Color.WHITE);
		mine.setLayout(null);

		taMineFeed = new JTextArea(80, 80);
		taMineFeed.setLineWrap(true);
		taMineFeed.setWrapStyleWord(true);
		taMineFeed.setText("To begin mining, click the gold button.");
		taMineFeed.setRows(80);
		taMineFeed.setBounds(10, 10, 314, 273);
		mine.add(taMineFeed);
		taMineFeed.setEditable(false);
		// taMineFeed.append("paul");
		taMineFeed.setCaretPosition(taMineFeed.getText().length() - 1);

		BufferedImage mineButtonIcon = ImageIO.read(new File("/home/paul/Desktop/spartagold2/img/mineButton.png"));
		JButton btnMine = new JButton(new ImageIcon(mineButtonIcon));
		btnMine.setBorder(BorderFactory.createEmptyBorder());
		btnMine.setContentAreaFilled(false);
		btnMine.setBounds(370, 175, 200, 75);
		mine.add(btnMine);

		btnMine.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				LoggerUtil.getLogger().fine("Mining for Gold selected. Creating Miner object...");
				Miner m = null;
				try
				{
					m = new Miner(peer);
					if (m.hasATransactionToVerify())
					{
						Runnable r = m;
						LoggerUtil.getLogger().fine("Runnable created. Creating thread...");
						Thread t = new Thread(r);
						t.start();
					}
					else
					{
						LoggerUtil.getLogger().fine("Currently there are no transactions to comfirm");
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});

		JPanel panel = new JPanel();
		panel.setBackground(UIManager.getColor("activeCaption"));
		panel.setBounds(20, 23, 571, 136);
		send.add(panel);
		panel.setLayout(null);

		JLabel lblWalletAmount = new JLabel(Double.toString(myBalance));
		lblWalletAmount.setBounds(421, 66, 92, 39);
		panel.add(lblWalletAmount);
		lblWalletAmount.setForeground(Color.BLACK);
		lblWalletAmount.setFont(new Font("Segoe UI Light", Font.BOLD, 27));

		JLabel lblSG2 = new JLabel("SG");
		lblSG2.setBounds(523, 66, 44, 39);
		panel.add(lblSG2);
		lblSG2.setForeground(Color.BLACK);
		lblSG2.setFont(new Font("Segoe UI Semibold", Font.BOLD, 27));

		JLabel lblBalance = new JLabel("Balance:");
		lblBalance.setBounds(445, 29, 92, 26);
		panel.add(lblBalance);
		lblBalance.setForeground(Color.BLACK);
		lblBalance.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));

		BufferedImage sendButtonIcon = ImageIO.read(new File("/home/paul/Desktop/spartagold2/img/sendButton.png"));
		JButton btnSend = new JButton(new ImageIcon(sendButtonIcon));
		btnSend.setBorder(BorderFactory.createEmptyBorder());
		btnSend.setContentAreaFilled(false);
		btnSend.setBounds(292, 66, 100, 38);
		panel.add(btnSend);
		btnSend.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if (tfAmount.getText() != "" && tfAddress.getText() != "")
				{
					LoggerUtil.getLogger().fine("Sending transaction " + tfAmount.getText() + " to " + tfAddress.getText());
					Transaction t = new Transaction(tfAddress.getText(), Double.parseDouble(tfAmount.getText()));
					t.addUnspentIds(peer.getBlockChain());
					System.out.println("####################: " + t.getUnspentIds());
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

		tfAmount = new JTextField();
		tfAmount.setBounds(92, 83, 100, 20);
		panel.add(tfAmount);
		tfAmount.setColumns(10);

		JLabel lblAmount = new JLabel("Amount:");
		lblAmount.setBounds(10, 80, 58, 26);
		panel.add(lblAmount);
		lblAmount.setForeground(Color.BLACK);

		JLabel lblAddress = new JLabel("Address:");
		lblAddress.setBounds(10, 27, 63, 26);
		panel.add(lblAddress);
		lblAddress.setForeground(Color.BLACK);

		JLabel lblSG1 = new JLabel("SG");
		lblSG1.setBounds(202, 80, 20, 26);
		panel.add(lblSG1);
		lblSG1.setForeground(Color.BLACK);

		tfAddress = new JTextField();
		tfAddress.setBounds(92, 30, 300, 20);
		panel.add(tfAddress);
		tfAddress.setColumns(10);

		BufferedImage logo = ImageIO.read(new File("/home/paul/Desktop/spartagold2/img/spartagoldlogo02.png"));
		Image scaledLogo = logo.getScaledInstance(logo.getWidth(), logo.getHeight(), Image.SCALE_SMOOTH);
		JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
		logoLabel.setBounds(79, 170, 450, 112);
		send.add(logoLabel);

		// GUI end
	}

	private void fillTransactionTable()
	{
		previousTransactions = new Object[myTransactionsList.size()][4];
		for (int i = 0; i < myTransactionsList.size(); i++)
		{
			previousTransactions[i][0] = myTransactionsList.get(i).getDate();
			if (myTransactionsList.get(i).getSenderPubKey() == peer.readPubKey())
			{
				previousTransactions[i][1] = "Sent";
				previousTransactions[i][2] = myTransactionsList.get(i).getReceiverPubKey();
			}
			else if (myTransactionsList.get(i).getReceiverPubKey() == peer.readPubKey())
			{
				previousTransactions[i][1] = "Received";
				previousTransactions[i][2] = peer.readPubKey();
			}
			else
			{
				previousTransactions[i][1] = "Unknown";
				previousTransactions[i][2] = myTransactionsList.get(i).getSenderPubKey();
			}
			previousTransactions[i][3] = myTransactionsList.get(i).getAmount();
		}
		LoggerUtil.getLogger().fine("Transaction table filled.");
	}

	private double getBalance()
	{
		double balance = 0;
		for (Transaction t : myTransactionsList)
		{
			if (t.getReceiverPubKey() == peer.readPubKey() && t.isSpent() == false)
				balance += t.getAmount();
		}
		LoggerUtil.getLogger().fine("Balance: " + balance);
		return balance;
	}

	public static String getIpAddress()
	{
		URL myIP;
		try
		{
			myIP = new URL("http://myip.dnsomatic.com/");

			BufferedReader in = new BufferedReader(new InputStreamReader(myIP.openStream()));
			return in.readLine();
		}
		catch (Exception e)
		{
			try
			{
				myIP = new URL("http://api.externalip.net/ip/");

				BufferedReader in = new BufferedReader(new InputStreamReader(myIP.openStream()));
				return in.readLine();
			}
			catch (Exception e1)
			{
				try
				{
					myIP = new URL("http://icanhazip.com/");

					BufferedReader in = new BufferedReader(new InputStreamReader(myIP.openStream()));
					myIpAddress = in.readLine();
					return in.readLine();
				}
				catch (Exception e2)
				{
					e2.printStackTrace();
				}
			}
		}
		return null;
	}
}
