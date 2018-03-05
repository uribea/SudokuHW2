package sudoku.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.io.File;
import javax.sound.sampled.*;

import sudoku.model.Board;

/**
 * A dialog template for playing simple Sudoku games.
 * You need to write code for three callback methods:
 * newClicked(int), numberClicked(int) and boardClicked(int,int).
 *
 * @author Yoonsik Cheon.
 * edited by: Daniel Almeraz and Alan Uribe
 */
@SuppressWarnings("serial")
public class SudokuDialog extends JFrame {

    /** Default dimension of the dialog. */
    private final static Dimension DEFAULT_SIZE = new Dimension(310, 430);

    /** used to find the location of the image*/
    private final static String IMAGE_DIR = "/image/";

    /** Sudoku board. */
    private Board board;

    /** Special panel to display a Sudoku board. */
    private BoardPanel boardPanel;

    /** Message bar to display various messages. */
    private JLabel msgBar = new JLabel("");

    /** Create a new dialog. */
    public SudokuDialog() {
    	this(DEFAULT_SIZE);
    }
    
    /** holds the values being attempted to be added to the board*/
    int[] values = { -1, -1, -1};
    
    /** Create a new dialog of the given screen dimension. 
     * @param dim dimensions
     * */
    public SudokuDialog(Dimension dim) {
        super("Sudoku");
        setSize(dim);
        board = new Board(9);
        boardPanel = new BoardPanel(board, this::boardClicked);
        configureUI();
        //setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        //setResizable(false);
    }

    /**
     * Callback to be invoked when a square of the board is clicked.
     * @param x 0-based row index of the clicked square.
     * @param y 0-based column index of the clicked square.
     */
     private void boardClicked(int x, int y) {
        
    	// WRITE YOUR CODE HERE ...
    	boardPanel.highlightBlock(x,y);
        values[0]= x;
        values[1]= y;
    	showMessage(String.format("Board clicked: x = %d, y = %d",  x, y));
    }
    
    /**
     * Callback to be invoked when a number button is clicked.
     * @param number Clicked number (1-9), or 0 for "X".
     */
    private void numberClicked(int number) {
    	if(values[0]!= -1 && values[1] != -1){
    		values[2] = number;
    		if (board.validCoordinates(values)){
    			board.setCoordinates(values);//send data to board
    			//rest values
    			values[0] = -1;
    			values[1] = -1;
    			boardPanel.highlightBlockOff();
    			boardPanel.repaint();
    		}
    		else{
    			errSound();
    		}
    	}
    	else{
    		errSound();
    	}
        showMessage("Number clicked: " + number);
        if(board.isSolved()){
        	int answer = JOptionPane.showConfirmDialog(msgBar, "Congratulations!!!! Continue?");
        	if( answer == 0) newClicked(board.size);
        	else if ( answer == 1) System.exit(0);
        }
        
    }
    
    /**
     * Callback to be invoked when a new button is clicked.
     * If the current game is over, start a new game of the given size;
     * otherwise, prompt the user for a confirmation and then proceed
     * accordingly.
     * @param size Requested puzzle size, either 4 or 9.
     */
    private void newClicked(int size) {
    	showMessage("New clicked: " + size);
    	if( JOptionPane.showConfirmDialog(msgBar, "Would You Like To Play A New Game") == 0){
    		board = new Board(size); //FIXME ADDED
    		boardPanel.setBoard(board);
    	}
    	repaint();
    }

    /**
     * Display the given string in the message bar.
     * @param msg Message to be displayed.
     */
    private void showMessage(String msg) {
        msgBar.setText(msg);
    }

    /** Configure the UI. */
    private void configureUI() {
    	
        setIconImage(createImageIcon("sudoku.png").getImage());
        setLayout(new BorderLayout());
        
        JPanel buttons = makeControlPanel();
        // boarder: top, left, bottom, right
        buttons.setBorder(BorderFactory.createEmptyBorder(10,16,0,16));
        add(buttons, BorderLayout.NORTH);
        
        JPanel board = new JPanel();
        board.setBorder(BorderFactory.createEmptyBorder(10,16,0,16));
        board.setLayout(new GridLayout(1,1));
        board.add(boardPanel);
        add(board, BorderLayout.CENTER);
        
        msgBar.setBorder(BorderFactory.createEmptyBorder(10,16,10,0));
        add(msgBar, BorderLayout.SOUTH);
    }
      
    /** Create a control panel consisting of new and number buttons. */
    private JPanel makeControlPanel() {
    	
    	JPanel newButtons = new JPanel(new FlowLayout());
        JButton new4Button = new JButton("New (4x4)");
        for (JButton button: new JButton[] { new4Button, new JButton("New (9x9)") }) {
        	button.setFocusPainted(false);
            button.addActionListener(e -> {
                newClicked(e.getSource() == new4Button ? 4 : 9);
            });
            newButtons.add(button);
    	}
    	newButtons.setAlignmentX(LEFT_ALIGNMENT);
        
    	// buttons labeled 1, 2, ..., 9, and X.
    	JPanel numberButtons = new JPanel(new FlowLayout());
    	int maxNumber = board.size + 1;
    	for (int i = 1; i <= maxNumber; i++) {
            int number = i % maxNumber;
            JButton button = new JButton(number == 0 ? "X" : String.valueOf(number));
            button.setFocusPainted(false);
            button.setMargin(new Insets(0,2,0,2));
            button.addActionListener(e -> numberClicked(number));
    		numberButtons.add(button);
    	}
    	numberButtons.setAlignmentX(LEFT_ALIGNMENT);

    	JPanel content = new JPanel();
    	content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        content.add(newButtons);
        content.add(numberButtons);
        return content;
    }

    /** Create an image icon from the given image file.
     *  @param filename 
     */
    private ImageIcon createImageIcon(String filename) {
        URL imageUrl = getClass().getResource(IMAGE_DIR + filename);
        if (imageUrl != null) {
            return new ImageIcon(imageUrl);
        }
        return null;
    }


    /** Play error sound*/
    private static void errSound() {        
    	try{
    		AudioInputStream audioIS = AudioSystem.getAudioInputStream(new File("beep-01a.wav"));
            Clip sClip = AudioSystem.getClip();  
            sClip.open(audioIS);
            sClip.start();

        while (!sClip.isRunning())
        	Thread.sleep(10);
            while (sClip.isRunning())
            	Thread.sleep(10);
                sClip.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    
     /**
      *  main method 
      * @param args array of string arguments
      * */
    public static void main(String[] args) {
        new SudokuDialog();
    }
}
