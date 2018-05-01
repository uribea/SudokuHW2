package sudoku.p2p;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import sudoku.dialog.SudokuDialog;
import sudoku.model.Board;
import sudoku.model.Solver;
import sudoku.dialog.SudokuDialog;

@SuppressWarnings("serial")
public class Main extends SudokuDialog implements NetworkAdapter.MessageListener{
	private ImageIcon NETWORK_OFF, NETWORK_ON;
	private JButton networkButton;
	private NetworkAdapter network;

	private JButton connectButton;
	private JTextField serverEdit;
	private JTextField portEdit;
	private JTextArea msgDisplay;
	private JTextField msgEdit;
	private JButton sendButton;
	private JTextArea infoArea;
	private JToolBar toolBar;
	
	protected JToolBar createToolBar() {
		//JToolBar 
		toolBar = super.createToolBar();
		NETWORK_OFF = createImageIcon("wifi-orange-right-hi.png");
		NETWORK_ON = createImageIcon("blue.png");
		networkButton = new JButton(NETWORK_OFF);
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
	private int finalPort = 8000;
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
		finalPort = port;
		createNetGui(finalPort);
		new Thread(() -> {
			try {
				System.out.println("Server started....");
				
				//ServerSocket
				s = new ServerSocket(finalPort);
				System.out.println("port for server" + finalPort);
				while(true) {
					System.out.println("hi1");
					Socket incoming = s.accept();
					System.out.println("hi2");
					infoArea.append("Pairing network \n");
					System.out.println("hi3");
					System.out.println("Pairing network....");
					pairAsServer(incoming);
					infoArea.append(incoming.toString() + "\n");
				}
			} catch(Exception ex) {}
		}).start();

	}
	

	private void closingButtonClicked(ActionEvent e){
		try {
			s.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		frame.dispose();
	}
	private JTextField hostIpNameText;
	private JTextField hostPortNumberText;
	private JTextField peerNameText;
	private JTextField peerPortNumberText;
	private JFrame frame;
	InetAddress inet;
	String hostName;
	String addr;
	private void createNetGui(int port) {

		//InetAddress 
		inet = null; 
		hostName = null;
		addr = null;
		try {
			inet = InetAddress.getLocalHost();
			hostName = inet.getHostName();
			addr = inet.getHostAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}



		//JFrame 
		frame = new JFrame("Connection");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setPreferredSize(new Dimension(400, 500));
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);



		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 16, 0, 16));
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


		JLabel hostNameLabel = new JLabel("Host Name:");
		JTextField hostNameText = new JTextField(hostName,20);
		hostNameText.setEditable(false);
		JLabel hostIpNameLabel = new JLabel("IP Number:");
		JTextField hostIpNameText= new JTextField(addr,20);
		hostIpNameText.setEditable(false);
		JLabel hostPortNumberLabel = new JLabel("Port Number:");
		JTextField hostPortNumberText= new JTextField(Integer.toString(port), 20);
		hostPortNumberText.setEditable(false);

		JLabel peerNameLabel = new JLabel("Host Name/IP:");
		peerNameText= new JTextField("10.0.3.7",20);
		JLabel peerPortNumberLabel = new JLabel("Port Number:");
		peerPortNumberText = new JTextField("8001", 20);



		JButton connectButton = new JButton("Connect"); //start a client - add listeners
		connectButton.addActionListener(e-> {connectClicked(e);});
		
		JButton disconnectButton = new JButton("Disconnect");
		
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(e -> closingButtonClicked(e));



		infoArea = new JTextArea(6,30);
		JScrollPane scroll = new JScrollPane (infoArea);
	    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	          scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
 
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.add(hostPanel);
		mainPanel.add(peerPanel);
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
		
		disconnectButton.addActionListener(e -> disconnectButtonClicked());

		bottomPanel.add(closeButton);


		frame.pack();
		frame.setVisible(true);
	}



	@Override
	protected void newClicked(int size) {
    	showMessage("New clicked: " + size);
    	if( JOptionPane.showConfirmDialog(msgBar, "Would You Like To Play A New Game") == 0){
    		board = new Board(size); 
    		boardPanel.setBoard(board);
            solver = new Solver(board,size);
            if(network != null) network.writeNew(board.size, board.getSquares());

    	}
    	repaint();
    
		
	}


	 
	private void disconnectButtonClicked() {
		try {
			network.close();
			infoArea.append("Disconnected \n");
			return;
		} catch(Exception ex) {
			
		}
	}

	@SuppressWarnings("resource")
	private void connectClicked(ActionEvent e) { 

		new Thread(() -> {
			try {
				Socket socket = new Socket();
				infoArea.append("Pairing.... \n");
				System.out.println("Pairing....");
				String s = peerNameText.getText();
				int portn = Integer.parseInt(peerPortNumberText.getText());
				if(s.equals(inet.getHostAddress()) && portn == finalPort){
					JOptionPane.showMessageDialog(null, "Dont Connect to Self");
					disconnectButtonClicked();
					infoArea.append("Disconnected \n");
					return;
				}
				socket.connect(new InetSocketAddress(s, portn), 5000);
				pairAsClient(socket);
			} catch(Exception ex) {}



		}).start();



	}
	
	@Override
	protected void solve(){
		if(solver.solve()){
			System.out.println("SOLVE");
			showMessage("Solved");
			if (network != null){
				System.out.println("studf");
				int[] b = board.getSquares();
				System.out.println(b.length);
				System.out.println("bsquare");
				for(int i = 0; i < b.length; i+=4){
					System.out.println("studf3456");
					network.writeFill(b[i], b[i+1], b[i+2]); 
					System.out.println(i+3);
			
				}
			}
			repaint();
			congratulate();
		}
		else {showMessage("Not Solvable");
			errSound();
		}
		repaint();
	}
	
	@Override
	public void congratulate() {
		JOptionPane.showMessageDialog(null, "Congratulations!");
	}
	
	@Override
	public void setValues(boolean boo) {
			board.setCoordinates(values, boo);
			if (network != null) { network.writeFill(values[0], values[1], values[2]); } 
	}

	private void pairAsClient(Socket socket) { //sync
		   network = new NetworkAdapter(socket);
		   network.setMessageListener(this);
		   network.writeJoin();
		   network.receiveMessages(); // loop till disconnected
	}		
	
	private void pairAsServer(Socket socket) { //sync
		   network = new NetworkAdapter(socket);
		   network.setMessageListener(this);
		   network.receiveMessages(); // loop till disconnected
		}
	
	
	
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
			System.out.println("FILL:");
			int xtemp = values[0];
			int ytemp = values[1];
			values[0] = x;
			values[1] = y;
			values[2] = z;
			infoArea.append("<= "+type +" "+ x  +" "+ y +" "+  z + "\n");
			boardPanel.highlightPeer(x,y);
			board.setCoordinates(values, true);

			values[0] = xtemp;
			values[1] = ytemp;
			repaint();
	        if(board.isSolved())
	        	congratulate();
			
			break;



		case JOIN:
			//create board to share"
			waitingForJoin();
			infoArea.append("<= "+type + "\n");
			System.out.println("JOIN");
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
	    		wait.dispose();
	    		network.writeJoinAck();
	    		infoArea.append("=> "+"JOIN_ACK: " + 0 + " \n");
	    	}
			break;
		case JOIN_ACK:
			System.out.println("JOIN_ACK");
			if(x == 0){ 
				System.out.println("Reject JOIN");
				disconnectButtonClicked();
				break;}
			networkButton.setIcon(NETWORK_ON);
			infoArea.append("<= "+type + x +" "+ y +" ");
				for(int i = 0; i < others.length; i++)
					infoArea.append(others[i] + " ");
				infoArea.append(" \n");
			newClicked(y, others);
			break;
		case NEW:
			System.out.println("NEW");
			infoArea.append("<= "+type + x +" "+ y +" ");
			for(int i = 0; i < others.length; i++)
				infoArea.append(others[i] + " ");
			infoArea.append(" \n");
				if( JOptionPane.showConfirmDialog(msgBar, "Would you like to start a New Game with Player", "New Game", JOptionPane.YES_NO_OPTION) == 0){
					newClicked(x, others);
		    		infoArea.append("=> "+"new_ACK: " + 1 + " \n");
				}
		    	else{
		    		try {
		    			network.close();
		    			infoArea.append("Disconnected \n");
		    			return;
		    		} catch(Exception ex) {
		    			
		    		}
		    		infoArea.append("=> "+"new_ACK: " + 0 + " \n");
		    	}
				break;
				
		case NEW_ACK: 
			System.out.println("NEW_ACK");
			infoArea.append("<= "+type + x +" "+ y +" ");
				for(int i = 0; i < others.length; i++)
					infoArea.append(others[i] + " ");
				infoArea.append(" \n");
			newClicked(y, others);
			break;
			
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

		ImageIcon waiting = createImageIcon("91.gif");
	    wait.add(new JLabel("Waiting for other player"), BorderLayout.NORTH);
	    wait.add(new JLabel(waiting));

	    wait.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    wait.setSize(350, 300);
	    wait.setVisible(true);
	}
}
