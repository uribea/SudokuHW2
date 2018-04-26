package sudoku.p2p;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import sudoku.dialog.SudokuDialog;

public class Main extends SudokuDialog implements NetworkAdapter.MessageListener{
	private ImageIcon NETWORK_OFF, NETWORK_ON;
	private JButton networkButton;

	@Override
	protected JToolBar createToolBar() {
		JToolBar toolBar = super.createToolBar();
		NETWORK_OFF = createImageIcon("wifi-red.png");
		networkButton = new JButton(NETWORK_OFF);
		networkButton.addActionListener(this::networkButtonClicked);
		networkButton.setToolTipText("Pair");
		networkButton.setFocusPainted(false);
		toolBar.add(networkButton, toolBar.getComponentCount() - 1);
		return toolBar;
	}
	 
	private void networkButtonClicked(ActionEvent e) { 
		
		//…
	}
  
	public void setCoordinates() {
		    super.setCoordinates();
		    if (network != null) { network.writeFill(values[0], values[1], values[2]); } 
	}

		  /** Called when a message is received from the peer. */
	  public void messageReceived(MessageType type, int x, int y, int z, int[] others) {
	    switch (type) {
	      case FILL:
	        // peer filled the square (x, y) with the number z
	        super.setCoordinates();
	        break;
	      //…
	    }
	  }
	

	public static void main(String[] args){
		new Main();
	}
	
}
