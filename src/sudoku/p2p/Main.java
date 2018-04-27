package sudoku.p2p;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.text.DefaultCaret;

import sudoku.dialog.SudokuDialog;



import sudoku.dialog.SudokuDialog;

@SuppressWarnings("serial")
public class Main extends SudokuDialog implements NetworkAdapter.MessageListener{
	private ImageIcon NETWORK_OFF, NETWORK_ON;
	private JButton networkButton;
	private NetworkAdapter network;// = new NetworkAdapter();

	private JButton connectButton;
	private JTextField serverEdit;
	private JTextField portEdit;
	private JTextArea msgDisplay;
	private JTextField msgEdit;
	private JButton sendButton;

	protected JToolBar createToolBar() {
		JToolBar toolBar = super.createToolBar();
		NETWORK_OFF = createImageIcon("wifi-orange-right-hi.png");
		networkButton = new JButton(NETWORK_OFF);
		networkButton.addActionListener(this::networkButtonClicked);
		networkButton.setToolTipText("Pair");
		networkButton.setFocusPainted(false);
		toolBar.add(networkButton, toolBar.getComponentCount() - 1);
		return toolBar;
	}
	private void networkButtonClicked(ActionEvent e) { 
		createNetGui();
	}
	
	
	
	private void createNetGui() {
		JFrame net = new JFrame("Connect");
		JPanel connectPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		connectButton = new JButton("Connect");
		connectButton.setFocusPainted(false);
		serverEdit = new JTextField("localhost", 18);
		portEdit = new JTextField("8000", 4);
		connectPanel.add(connectButton);
		connectPanel.add(serverEdit);
		connectPanel.add(portEdit);
		
		msgDisplay = new JTextArea(10, 30);
		msgDisplay.setEditable(false);
		DefaultCaret caret = (DefaultCaret)msgDisplay.getCaret();
		JScrollPane msgScrollPane = new JScrollPane(msgDisplay);
		
		JPanel sendPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        msgEdit = new JTextField("Enter a message.", 27);
        sendButton = new JButton("Send");
        sendButton.setFocusPainted(false);
        sendPanel.add(msgEdit);
        sendPanel.add(sendButton);
        
        setLayout(new BorderLayout());
        net.add(connectPanel, BorderLayout.NORTH);
        net.add(msgScrollPane, BorderLayout.CENTER);
        net.add(sendPanel, BorderLayout.SOUTH);
        
        net.pack();
        net.setLocationByPlatform(true);
        net.setVisible(true);
        net.setResizable(true);
        serverEdit.requestFocus();
	}


	 
	private void networkButtonConnectClicked(ActionEvent e) { 
		   
		new Thread(()-> {
			      try {
			        Socket socket = new Socket();
			        network = new NetworkAdapter(socket);
			        socket.connect(new InetSocketAddress("127.0.0.1", 8000), 5000); // timeout in millis
			        pairAsClient(socket);
			      } catch (Exception e1) { }
			    }).start();
			}

	
  
	public void setCoordinates() {
		    //super.setCoordinates();
		    //network;
			if (network != null) { network.writeFill(values[0], values[1], values[2]); } 
	}

		/*   //Called when a message is received from the peer.
	  public void messageReceived(MessageType type, int x, int y, int z, int[] others) {
	    switch (type) {
	      case FILL:
	        // peer filled the square (x, y) with the number z
	        super.setCoordinates();
	        break;
	      //…
	    }
	  }
	  */
	private void pairAsClient(Socket socket) {
		   network = new NetworkAdapter(socket);
		   network.setMessageListener(this); // see the next slide
		   network.writeJoin();
		   network.receiveMessages(); // loop till disconnected
	}		
	
	private void pairAsServer(Socket socket) {
		   network = new NetworkAdapter(socket);
		   network.setMessageListener(this); // see the next slide
		   //?network.writeJoin();
		   network.receiveMessages(); // loop till disconnected
		}
		//private NetworkAdapter network;

	public static void main(String[] args){
		new Main();
	}

	@Override
	public void messageReceived(sudoku.p2p.NetworkAdapter.MessageType type, int x, int y, int z, int[] others) {
		// TODO Auto-generated method stub
		
	}
	
}
