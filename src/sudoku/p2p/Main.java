package sudoku.p2p;

import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import sudoku.dialog.SudokuDialog;

@SuppressWarnings("serial")
public class Main extends SudokuDialog implements NetworkAdapter.MessageListener{
	private ImageIcon NETWORK_OFF, NETWORK_ON;
	private JButton networkButton;
	private NetworkAdapter network;// = new NetworkAdapter();
	@Override
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

		//private NetworkAdapter network;

	public static void main(String[] args){
		new Main();
	}

	@Override
	public void messageReceived(sudoku.p2p.NetworkAdapter.MessageType type, int x, int y, int z, int[] others) {
		// TODO Auto-generated method stub
		
	}
	
}
