package spartagold.wallet.frontend;

import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import spartagold.framework.PeerInfo;
import spartagold.wallet.backend.GenKeys;
import spartagold.wallet.backend.Miner;
import spartagold.wallet.backend.SpartaGoldNode;
import spartagold.wallet.backend.Transaction;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

public class WalletGUI
{

	private JFrame frmSpartagoldWallet;
	private JTextField tfAmount;
	private JTextField tfAddress;

	private static String myIpAddress;
	private double myBalance;
	private JTable table;
	private Object[][] previousTransactions = {};
	private String[] transactionColumns = { "Date", "Address", "Amount" };
	private SpartaGoldNode peer;

	/**
	 * Launch the application.
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
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
						System.out.println("Generating keys...");
						GenKeys.generateKeys();
						System.out.println("Public and private keys generated.");
					}
					System.out.println("Connecting to SpartaGold network...");
					WalletGUI window = new WalletGUI("localhost", 9000, 5,new PeerInfo("localhost", 9002));
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
		(new Thread(){public void run(){peer.mainLoop();}}).start();

		//TODO: compare block chain size

		// GUI start
		frmSpartagoldWallet = new JFrame();
		frmSpartagoldWallet.setBackground(new Color(11, 46, 70));
		frmSpartagoldWallet.getContentPane().setBackground(new Color(11, 46, 70));
		frmSpartagoldWallet.setTitle("SpartaGold Wallet");
		frmSpartagoldWallet.setResizable(false);
		frmSpartagoldWallet.setBounds(100, 100, 625, 350);
		frmSpartagoldWallet.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frmSpartagoldWallet.setUndecorated(true);

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

		
		BufferedImage mineButtonIcon = ImageIO.read(new File("/home/paul/workspace/P2P/src/spartagold/img/mineButton.png"));
		JButton btnMine = new JButton(new ImageIcon(mineButtonIcon));
		btnMine.setBorder(BorderFactory.createEmptyBorder());
		btnMine.setContentAreaFilled(false);
		btnMine.setBounds(196, 132, 200, 75);
		mine.add(btnMine);
		btnMine.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				System.out.println("Mining for Gold selected. Creating Miner object...");
				Miner m = null;
				try
				{
					m = new Miner(peer.getTransaction(), peer.getBlockChain());
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
				Runnable r = m;
				Thread t = new Thread(r);
				// TODO: find a way to stop mining process when solution found from someone else
				t.start();
				System.out.println("Mining has begun.");
				for (PeerInfo pid : peer.getAllPeers())
				{
					System.out.println("Broadcasting to " + pid.toString());
					peer.connectAndSendObject(pid, SpartaGoldNode.FOUNDSOLUTION, m.getBlock());
				}
			}
		});
		

		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				//TODO: stop mining
			}
		});
		btnStop.setBounds(248, 218, 89, 23);
		mine.add(btnStop);

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

		BufferedImage sendButtonIcon = ImageIO.read(new File("/home/paul/workspace/P2P/src/spartagold/img/sendButton.png"));
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
					System.out.println("Sending transaction " + tfAmount.getText() + " to " + tfAddress.getText());
					Transaction msgdata = new Transaction(tfAddress.getText(), Double.parseDouble(tfAmount.getText()));
					for (PeerInfo pid : peer.getAllPeers())
					{
						System.out.println("Broadcasting to " + pid.toString());
						peer.connectAndSendObject(pid, SpartaGoldNode.TRANSACTION, msgdata);
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
		
		
		BufferedImage logo = ImageIO.read(new File("/home/paul/workspace/P2P/src/spartagold/img/spartagoldlogo02.png"));
		Image scaledLogo = logo.getScaledInstance(logo.getWidth(), logo.getHeight(), Image.SCALE_SMOOTH);
		JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
		logoLabel.setBounds(79, 170, 450, 112);
		send.add(logoLabel);
		

		// GUI end
	}

	public static String getIpAddress()
	{
		URL myIP;
		try
		{
			myIP = new URL("http://myip.dnsomatic.com/");

			BufferedReader in = new BufferedReader(new InputStreamReader(
					myIP.openStream()));
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
