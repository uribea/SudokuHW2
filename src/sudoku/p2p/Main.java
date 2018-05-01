package sudoku.p2p;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
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
	private JTextArea infoArea;
	private JToolBar toolBar; //= super.createToolBar();
	//private boolean boo = false;
	
	protected JToolBar createToolBar() {
		//JToolBar 
		toolBar = super.createToolBar();
		NETWORK_OFF = createImageIcon("wifi-orange-right-hi.png");
		NETWORK_ON = createImageIcon("blue.png");
		networkButton = new JButton(NETWORK_OFF);
		//System.out.println(boo);
		networkButton.addActionListener(this::networkButtonClicked);
		networkButton.setToolTipText("Pair");
		networkButton.setFocusPainted(false);
		toolBar.add(networkButton, toolBar.getComponentCount() - 1);
		return toolBar;
	}
	
	//checks if the port given is available
	public static boolean portAvailable(int port) {
    ServerSocket ss = null;
    DatagramSocket ds = null;
    try {
        ss = new ServerSocket(port);
        ss.setReuseAddress(true);
        ds = new DatagramSocket(port);
        ds.setReuseAddress(true);
        return true;
    } catch (IOException e) {
    } finally {
        if (ds != null) {
            ds.close();
        }

        if (ss != null) {
            try {
                ss.close();
            } catch (IOException e) {
                /* should not be thrown */
            }
        }
    }

    return false;
}
	private ServerSocket s;
	private void networkButtonClicked(ActionEvent e) { 
		try{
			s.close();
			frame.dispose();
		}catch(Exception e1){
			
		}
		int port;
		port = 8000;
		while(!portAvailable(port)){
			++port;
		}
		final int finalPort = port;
		createNetGui(finalPort);
		new Thread(() -> {
			try {
				System.out.println("Server started....");
				
				//ServerSocket
				s = new ServerSocket(finalPort);
				System.out.println("port for server" + finalPort);
				while(true) {
					Socket incoming = s.accept();
					infoArea.append("Pairing network \n");
					System.out.println("Pairing network....");
					pairAsServer(incoming);
					infoArea.append(incoming.toString() + "\n");
				}
			} catch(Exception ex) {}
		}).start();

	}
	

	private void closingButtonClicked(ActionEvent e){//, JFrame frame) {
		try {
			s.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		frame.dispose();
	}
	
	private JTextField peerNameText;
	private JTextField peerPortNumberText;
	private JFrame frame;
	private void createNetGui(int port) {

		InetAddress i = null;
		String hostName = null;
		String addr = null;
		try {
			i = InetAddress.getLocalHost();
			hostName = i.getHostName();
			addr = i.getHostAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}



		//JFrame 
		frame = new JFrame("Connection");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setPreferredSize(new Dimension(400, 500));
		//frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS ));
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);



		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 16, 0, 16));
		//mainPanel.setPreferredSize(new Dimension(300, 450));
		//mainPanel.setLayout(new GridLayout(0,1,10,10));
		mainPanel.setLayout(new FlowLayout());



		JPanel hostPanel = new JPanel();
		hostPanel.setLayout(new FlowLayout(2,8,5));
		hostPanel.setBorder(BorderFactory.createTitledBorder("Host"));
		hostPanel.setPreferredSize(new Dimension(350, 125));



		JPanel peerPanel = new JPanel();
		peerPanel.setLayout(new FlowLayout(2,8,5));
		peerPanel.setBorder(BorderFactory.createTitledBorder("Peer"));
		peerPanel.setPreferredSize(new Dimension(350, 125));



		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(0,15,5));



		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout());
		bottomPanel.setPreferredSize(new Dimension(350, 125));


		//NOTHARDCODDEDANYMOREYAY
		JLabel hostNameLabel = new JLabel("Host Name:");
		JTextField hostNameText = new JTextField(hostName,20);
		hostNameText.setEditable(false);
		//hostNameText.setPreferredSize(new Dimension(120, 20));
		JLabel hostIpNameLabel = new JLabel("IP Number:");
		JTextField hostIpNameText= new JTextField(addr,20);
		hostIpNameText.setEditable(false);
		JLabel hostPortNumberLabel = new JLabel("Port Number:");
		JTextField hostPortNumberText= new JTextField(Integer.toString(port), 20);
		hostPortNumberText.setEditable(false);

		//FIXME
		//peer's values  changing them does not actually change value (i think)
		JLabel peerNameLabel = new JLabel("Host Name/IP:");
		//JTextField 
		peerNameText= new JTextField("10.0.3.7",20);
		JLabel peerPortNumberLabel = new JLabel("Port Number:");
		//JTextField 
		peerPortNumberText = new JTextField("8001", 20);



		JButton connectButton = new JButton("Connect"); //start a client - add listeners
		connectButton.addActionListener(e-> {connectClicked(e);});
		
		JButton disconnectButton = new JButton("Disconnect");
		
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(e -> closingButtonClicked(e));// frame));



		infoArea = new JTextArea(6,30);
		JScrollPane scroll = new JScrollPane (infoArea);
	    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	          scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
 
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.add(hostPanel);
		mainPanel.add(peerPanel);
		//mainPanel.add(infoArea);
		mainPanel.add(scroll);
		mainPanel.add(bottomPanel);



		hostPanel.add(hostNameLabel);
		hostPanel.add(hostNameText);
		hostPanel.add(hostIpNameLabel);
		hostPanel.add(hostIpNameText);
		hostPanel.add(hostPortNumberLabel);
		hostPanel.add(hostPortNumberText);



		peerPanel.add(peerNameLabel);
		peerPanel.add(peerNameText);
		peerPanel.add(peerPortNumberLabel);
		peerPanel.add(peerPortNumberText);
		buttonPanel.add(connectButton);
		buttonPanel.add(disconnectButton);
		peerPanel.add(buttonPanel);
		
		disconnectButton.addActionListener(e -> disconnectButtonClicked());// frame));

		bottomPanel.add(closeButton);


		frame.pack();
		frame.setVisible(true);
	}





	

	 
	private Object disconnectButtonClicked() {
		// TODO Auto-generated method stub
		return null;
	}

	private void connectClicked(ActionEvent e) { 

		new Thread(() -> {
			try {
				Socket socket = new Socket();
				infoArea.append("Pairing.... \n"); //change
				System.out.println("Pairing....");
				//FIXME
				socket.connect(new InetSocketAddress(peerNameText.getText(), Integer.parseInt(peerPortNumberText.getText())), 5000);//?hardcoded //timeout in millis
				pairAsClient(socket);
			} catch(Exception ex) {}



		}).start();



	}
	
	@Override
	public void setValues() {
			board.setCoordinates(values);
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
	private void pairAsClient(Socket socket) { //synch
		   network = new NetworkAdapter(socket);
		   network.setMessageListener(this); // see the next slide
		   network.writeJoin();
		   network.receiveMessages(); // loop till disconnected
		   //?network.recieveMessagesAsync();
	}		
	
	private void pairAsServer(Socket socket) { //synch
		   network = new NetworkAdapter(socket);
		   network.setMessageListener(this); // see the next slide
		   //?network.writeJoin();
		   network.receiveMessages(); // loop till disconnected
		  // network.receiveMessagesAsync();
		}
		//private NetworkAdapter network;
	
	
	
	public static void main(String[] args){
		new Main();
	}
	
	public void addMessage(String msg){
		
	}
	
	@Override
	public void messageReceived(sudoku.p2p.NetworkAdapter.MessageType type, int x, int y, int z, int[] others) {
		System.out.println(type);
		switch(type) {
		case FILL:
			//TODO
			//peer filled the square(x,y) with the number z
			//super.fillNumber(x,y,z);
			//super.numberClicked(z);
			System.out.println("FILL:");
			values[0] = x;
			values[1] = y;
			values[2] = z;
			infoArea.append("<= "+type +" "+ x  +" "+ y +" "+  z + "\n");
			boardPanel.highlightPeer(x,y);
			board.setCoordinates(values);
			repaint();
			break;



		case JOIN:
			//create board to share"
			waitingForJoin();
			infoArea.append("<= "+type + "\n");
			System.out.println("JOIN");
			//networkButton.setIcon(NETWORK_ON);
			int[] square = board.getSquares();
	    	if( JOptionPane.showConfirmDialog(msgBar, "Would you like to share your game", "Share", JOptionPane.YES_NO_OPTION) == 0){
	    		wait.dispose();
	    		network.writeJoinAck(board.size, square);
				networkButton.setIcon(NETWORK_ON);
				infoArea.append("=> "+"JOIN_ACK: " + 1 + ","+board.size+",");
				for(int i: board.getSquares())
					infoArea.append(i+",");
	    	}
	    	else{
	    		network.writeJoinAck();
	    		infoArea.append("=> "+"JOIN_ACK: " + 0 + " \n");
	    	}
			//network.writeJoin();
			break;
		case JOIN_ACK:
			System.out.println("JOIN_ACK");
			networkButton.setIcon(NETWORK_ON);
			infoArea.append("<= "+type + x +" "+ y +" ");
				for(int i = 0; i < others.length; i++)
					infoArea.append(others[i] + " ");
				infoArea.append(" \n");
			newClicked(y, others);
			//create board to share
			//Board newGame = new Board(board.getBoardSize());
			//newGame.clone();
			
			//network.writeJoinAck(board.getSize());
			
			//bo
			break;
		case NEW:
			System.out.println("NEW");
			infoArea.append("<= "+type + x );
				for(int i : others)// = 0; i < others.length; i++)
					infoArea.append(others[i] + " ");
				infoArea.append(" \n");
				if( JOptionPane.showConfirmDialog(msgBar, "Would you like to start a New Game", "New Game", JOptionPane.YES_NO_OPTION) == 0){
		    		network.writeNewAck(true);
		    		infoArea.append("=> "+"new_ACK: " + 1 + " \n");
		    		newClicked(x, others);
				}
		    	else{
		    		network.writeNewAck(false);
		    		infoArea.append("=> "+"new_ACK: " + 0 + " \n");
		    	}
			
		case CLOSE:
			networkButton.setIcon(NETWORK_OFF);
			break;
			
		default:
			break;
		}

		
	}
	
	JFrame wait;
	private void waitingForJoin() {
		 wait = new JFrame("Share");

		ImageIcon waiting = createImageIcon("searching.gif");
	    wait.add(new JLabel("Waiting for other player"), BorderLayout.NORTH);
	    wait.add(new JLabel(waiting));

	    wait.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    wait.setSize(350, 300);
	    wait.setVisible(true);
	}
}
