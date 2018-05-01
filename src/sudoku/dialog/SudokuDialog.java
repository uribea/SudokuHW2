package sudoku.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import java.io.File;
import javax.sound.sampled.*;

import sudoku.model.Board;
import sudoku.model.Solver;

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

    private JPanel buttons, menu;
    
    /** Sudoku board. */
    protected Board board;
    protected Solver solver;

    /** Special panel to display a Sudoku board. */
    public BoardPanel boardPanel;
    
    /**used to display the hits on the bottom of the panel*/
    private boolean hintTog;

    /** Message bar to display various messages. */
    protected JLabel msgBar = new JLabel("");

    /** Create a new dialog. */
    public SudokuDialog() {
    	this(DEFAULT_SIZE);
    }
    
    /** holds the values being attempted to be added to the board*/
    protected int[] values = { -1, -1, -1};
    
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
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        solver = new Solver(board,board.size);
        //setResizable(false);
    }

    /**
     * Callback to be invoked when a square of the board is clicked.
     * @param x 0-based row index of the clicked square.
     * @param y 0-based column index of the clicked square.
     */
     private void boardClicked(int x, int y) {
        
    	boardPanel.highlightBlock(x,y);
        values[0]= x;
        values[1]= y;
        
        menu.remove(buttons);
        buttons = makeControlPanel();
        menu.add(buttons, BorderLayout.SOUTH);
    	
        String s = " ";
        for (int i = 0; i <= board.size; i++) {
        	if(board.validCoordinates(new int[]{values[0], values[1], i}))
        			s= s+i+",";
        }
        if(hintTog)
    	showMessage(String.format("hint = {%s}", s));
        else showMessage(String.format("Board clicked: x = %d, y = %d,",  x, y));
    }
    public void setValues(boolean boo){
    	 board.setCoordinates(values, boo);
    }
    /**
     * Callback to be invoked when a number button is clicked.
     * @param number Clicked number (1-9), or 0 for "X".
     */
    private void numberClicked(int number) {
    	if(values[0]!= -1 && values[1] != -1){
    		values[2] = number;
    		if (board.validCoordinates(values)){
    			setValues(true);//send data to board
    			//reset values
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
        if(board.isSolved())
        	congratulate();
        
        
    }
    /**
     * congradulates the user upon completion
     */
    protected void congratulate() {
    	int answer = JOptionPane.showConfirmDialog(msgBar, "Congratulations!!!! Continue?");
    	if( answer == 0) newClicked(board.size);
    	else if ( answer == 1) System.exit(0);
		
	}

	/**
     * Callback to be invoked when a new button is clicked.
     * If the current game is over, start a new game of the given size;
     * otherwise, prompt the user for a confirmation and then proceed
     * accordingly.
     * @param size Requested puzzle size, either 4 or 9.
     */
    protected void newClicked(int size) {
    	showMessage("New clicked: " + size);
    	if( JOptionPane.showConfirmDialog(msgBar, "Would You Like To Play A New Game") == 0){
    		board = new Board(size); 
    		boardPanel.setBoard(board);
            solver = new Solver(board,size);

    	}
    	repaint();
    }
    protected void newClicked(int size, int[] others) {
    	board = new Board(size, others); 
    	boardPanel.setBoard(board);
        solver = new Solver(board,size);    	
    	repaint();
    }
    /**
     * undos to the previous board construction
     */
    private void undo(){
    	if (board.undoExists()) {
    		values = board.undo();
    		setValues(false);
    		}
    	else errSound();
    	repaint();
    }
    /**
     * redos to the next position of the board
     * undos an undo
     */
    private void redo(){
    	if (board.redoExists()) {
    		values = board.redo();
    		setValues(false);
    		}
    	else errSound();
       	repaint();
    }

    /**
     * Display the given string in the message bar.
     * @param msg Message to be displayed.
     */
    protected void showMessage(String msg) {
        msgBar.setText(msg);
    }

    /** Configure the UI. */
    private void configureUI() {
    	
        setIconImage(createImageIcon("sudoku.png").getImage());
        setLayout(new BorderLayout());
        
        setMinimumSize(new Dimension(350, 540));
        
        menu = makeMenus();
        
        buttons = makeControlPanel();
        // border: top, left, bottom, right
        //buttons.setBorder(BorderFactory.createEmptyBorder(10,16,0,16));
        menu.add(buttons, BorderLayout.SOUTH);
        
        //menu.setBorder(BorderFactory.createEmptyBorder(10,16,0,16));
        add(menu, BorderLayout.NORTH);
        

        JPanel board = new JPanel();
        board.setBorder(BorderFactory.createEmptyBorder(10,16,0,16));
        board.setLayout(new GridLayout(1,1));
        
        board.add(boardPanel);
        add(board, BorderLayout.CENTER);
        
        msgBar.setBorder(BorderFactory.createEmptyBorder(10,16,10,0));
        add(msgBar, BorderLayout.SOUTH);
    }
      
    /**
     * makes the needed tool bar
     * @return Jpanel menu
     */
	private JPanel makeMenus() {
    	JMenuBar menubar = new JMenuBar();
        JMenu menu = new JMenu("GAME");
        //menu.setLayout(new BorderLayout(menu, BorderLayout.NORTH));
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription("Game Menu");

        menubar.add(menu);//, BorderLayout.NORTH);
        
        JMenuItem menuPlay = new JMenuItem("New Game", KeyEvent.VK_N);
		menuPlay.setIcon(createImageIcon("play.png"));
		menuPlay.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
		menuPlay.getAccessibleContext().setAccessibleDescription("Play a new game");
        
		JMenuItem meSolvable = new JMenuItem("Check if Game Solvable", KeyEvent.VK_C);
		meSolvable.setIcon(createImageIcon("checkmark.png"));
		meSolvable.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		meSolvable.getAccessibleContext().setAccessibleDescription("Check if game is solveable");
		
		JMenuItem meSolve = new JMenuItem("Solve Game", KeyEvent.VK_S);
		meSolve.setIcon(createImageIcon("redChek.png"));
		meSolve.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		meSolve.getAccessibleContext().setAccessibleDescription("Solve game");
		
		menu.add(menuPlay);
		menu.add(meSolvable);
		menu.add(meSolve);
		
		for (JMenuItem button: new JMenuItem[] { menuPlay, meSolvable, meSolve }) {
        	button.setFocusPainted(false);
            button.addActionListener(e -> {
            	if(e.getSource() == menuPlay)
            		newClicked(board.size);
            	if(e.getSource() == meSolvable)
            		isSolvable();
            	if(e.getSource() == meSolve)
            		solve();
//                newClicked(e.getSource() == new9Game ? 9 : 0);
            });
		}
		
		JToolBar toolbar = createToolBar();
		JPanel Menus = new JPanel(new BorderLayout());
        //Menus.setLayout(new BoxLayout(Menus, BoxLayout.PAGE_AXIS));
        
        Menus.add(menubar, BorderLayout.NORTH);
        Menus.add(toolbar, BorderLayout.CENTER);
        
		return Menus;
	}
		
	protected JToolBar createToolBar() {
        /*JToolBar toolbar = new JToolBar("Menu toolbar");
        JButton newchoiceButton = new JButton("choice");
        toolbar.add(newchoiceButton);
        */
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        //panel.add(toolBar, BorderLayout.PAGE_START);
    	JButton new9Game = new JButton(createImageIcon("play.png"));
		new9Game.setToolTipText("Play new game");
		toolbar.add(new9Game);
		
		JButton solveGame = new JButton(createImageIcon("redchek.png"));
		solveGame.setToolTipText("Solve current game");
		toolbar.add(solveGame);
		
		JButton solveable = new JButton(createImageIcon("checkmark.png"));
		solveable.setToolTipText("Checks if current board is solveable");
		toolbar.add(solveable);
		
		JButton hint = new JButton(createImageIcon("Glossy_3d_blue_questionmark.png"));
		hint.setToolTipText("Click back on board for hint");
		toolbar.add(hint);
		
		for (JButton button: new JButton[] { new9Game, solveGame, solveable, hint }) {
        	button.setFocusPainted(false);
            button.addActionListener(e -> {
            	if(e.getSource() == new9Game)
            		newClicked(board.size);
            	if(e.getSource() == solveGame)
            		solve();
            	if(e.getSource() == solveable)
            		isSolvable();
            	if(e.getSource() == hint)
            		hintOn();
//                newClicked(e.getSource() == new9Game ? 9 : 0);
            });
            //newButtons.add(button);
		}
        
        return toolbar;
        
	}
	/**
	 * used to toggle hints and off
	 */
	private void hintOn() {
		hintTog = true;
		boardPanel.hintTog();
		repaint();
	}
	/** 
	 * uses solvable class to check if the board is solvable
	 */
	private void isSolvable() {
		// TODO Auto-generated method stub
		if(solver.isSolvable()){
			//System.out.println("boo");
			showMessage("The board is still solvable!");
			board.isSolved();
		}
		else{
			errSound();
			showMessage("The board is NOT solvable!");
		}
	}
	/**
	 * solves the board
	 * plays error sound if not solvable to begin with
	 */
	protected void solve() {
		if(solver.solve()){
			showMessage("Solved");
			repaint();
			congratulate();
		}
		else {showMessage("Not Solvable");
			errSound();
		}
		repaint();
		
	}

	/** Create a control panel consisting of new and number buttons. */
    private JPanel makeControlPanel() {

    	JPanel newButtons = new JPanel(new FlowLayout());
    	JButton undo = new JButton("<==");
        JButton new4Button = new JButton("New (4x4)");
        JButton new9Button = new JButton("New (9x9)");
        JButton redo = new JButton("==>");
        for (JButton button: new JButton[] { undo, new4Button, new9Button , redo  }) {
        	button.setFocusPainted(false);
            button.addActionListener(e -> {
                if(e.getSource() == undo){
                	undo();
                }
                if(e.getSource() == new4Button){
                	newClicked(4);
                }	
                if(e.getSource() == new9Button){
                	newClicked(9);
                }
                if(e.getSource() == redo){
                	redo();
                	
                }
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
    		
    		if(values[0]!=-1 && values[1]!=-1 && !board.validCoordinates(new int[]{values[0], values[1], number}))
            	button.setEnabled(false);
            else button.setEnabled(true);
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
    protected ImageIcon createImageIcon(String filename) {
        URL imageUrl = getClass().getResource(IMAGE_DIR + filename);
        if (imageUrl != null) {
            return new ImageIcon(imageUrl);
        }
        return null;
    }


    /** Play error sound*/
    protected static void errSound() {        
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
