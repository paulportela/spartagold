package spartagold.wallet.frontend;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import spartagold.framework.LoggerUtil;
import spartagold.framework.PeerInfo;
import spartagold.wallet.backend.SpartaGoldNode;




public class SpartaGoldApp
{
	private SpartaGoldNode peer;
	
	private static final int FRAME_WIDTH = 565, FRAME_HEIGHT = 265;
	private JButton sendButton;
	private JTextField sendField;
	private JTextField receiveField;
	private JFrame frame;
	
	public SpartaGoldApp(String initialhost, int initialport, int maxpeers, PeerInfo mypd)
	{
		peer = new SpartaGoldNode(maxpeers, mypd);
		peer.buildPeers(initialhost, initialport, 2	);
		
		JButton refresh = new JButton("Refresh");
		refresh.addActionListener(new refreshButtonListener());
		frame = new JFrame(mypd.getId() + ":" + mypd.getHost());
		JPanel buttonPanel = new JPanel();
		sendButton = new JButton("Send Message");
		sendButton.addActionListener(new SendMessageListener());
		receiveField = new JTextField();
		buttonPanel.add(sendButton);
		buttonPanel.add(refresh);
		sendField = new JTextField(20);
		frame.add(sendField, BorderLayout.CENTER);
		frame.add(receiveField, BorderLayout.SOUTH);
		frame.add(buttonPanel, BorderLayout.NORTH);
		frame.pack();
		frame.setVisible(true);
		
		(new Thread() { public void run() { peer.mainLoop(); }}).start();
	}
	class SendMessageListener implements ActionListener 
	{
		public void actionPerformed(ActionEvent e) 
		{
			peer.setSimpleMessage(sendField.getText());
			//peer.broadCastMessage();
		}
	}
	
	class refreshButtonListener implements ActionListener 
	{
		public void actionPerformed(ActionEvent e) 
		{
			receiveField.setText(peer.getSimpleMessage());
			frame.repaint();
		}
	}
	
	public static void main(String[] args)
	{
		int port = 9000;
		if (args.length != 1) 
		{
			System.out.println("Usage: java ... peerbase.sample.FileShareApp <host-port>");
		}
		else 
		{
			port = Integer.parseInt(args[0]);
		}

		LoggerUtil.setHandlersLevel(Level.FINE);
		new SpartaGoldApp(null, 6789, 5, new PeerInfo(null, 2467));
	}
}
