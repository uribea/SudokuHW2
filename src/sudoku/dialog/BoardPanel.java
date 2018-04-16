package sudoku.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import javax.swing.JPanel;

import sudoku.model.Board;

/**
 * A special panel class to display a Sudoku board modeled by the
 * {@link sudoku.model.Board} class. You need to write code for
 * the paint() method.
 *
 * @see sudoku.model.Board
 * @author Yoonsik Cheon
 * edited by: Daniel Almeraz and Alan Uribe
 */
@SuppressWarnings("serial")
public class BoardPanel extends JPanel {
    
	public interface ClickListener {
		
		/** Callback to notify clicking of a square. 
		 * 
		 * @param x 0-based column index of the clicked square
		 * @param y 0-based row index of the clicked square
		 */
		void clicked(int x, int y);
	}
	
	
	/** Which block index to be highlighted, {-1, -1} if non */
	private int[] hb = {-1, -1};
	
    /** Background color of the board. */
	private static final Color boardColor = new Color(255, 235, 180);
	
	/** Color of the lines in the board. */
	private static final Color lineColor = new Color(0, 0, 0);
	
	/** Color of highlighted selected block in the board. */
	private static final Color highlightColor = new Color(204, 204, 250);
	
	/** Color of locked block in the board. */
	private static final Color lockedColor = new Color(250, 210, 160);
	
	/** Font of original values in the board. */
	private static final Font normalFont = new Font("TimesRoman", Font.BOLD, 16) ;
	
	/** Font of small hint values in the board. */
	private static final Font smallFont = new Font("TimesRoman", Font.PLAIN, 10);
	

    /** Board to be displayed. */
    private Board board;
    
    /** boolean to tell if hints are on or not*/
    boolean hint = false;

    /** Width and height of a square in pixels. */
    private int squareSize;

    /** Create a new board panel to display the given board.
     * @param board the object of board
     * @param listener listens for a click */
    public BoardPanel(Board board, ClickListener listener) {
        this.board = board;
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
            	int xy = locateSquaree(e.getX(), e.getY());
            	if (xy >= 0) {
            		repaint();
            		listener.clicked(xy / 100, xy % 100);
            		repaint();
            	}
            }
        });
    }

    /** Set the board to be displayed.
     * @param board object of the sudoku board */
    public void setBoard(Board board) {
    	this.board = board;
    }
    
    /** 
     * Getter for which blocks to highlight
     * @param x 0-based column index of the highlighted square
     * @param y 0-based column index of the highlighted square
     */
    public void highlightBlock(int x, int y){
    	hb[0] = x;
    	hb[1] = y;
    }
    
    /**
     * Turns off any block that is being highlighted.
     */
    public void highlightBlockOff(){
    	hb[0] = hb [1] = -1;
	}
	 
    
    /**
     * Given a screen coordinate, return the indexes of the corresponding square
     * or -1 if there is no square.
     * The indexes are encoded and returned as x*100 + y, 
     * where x and y are 0-based column/row indexes.
     */
    private int locateSquaree(int x, int y) {
    	if (x < 0 || x > board.size * squareSize
    			|| y < 0 || y > board.size * squareSize) {
    		return -1;
    	}
    	int xx = x / squareSize;
    	int yy = y / squareSize;
    	return xx * 100 + yy;
    }
    /**
     * 
     */
    public void hintTog(){
    	hint = !hint;
    }
    /** Draw the associated board. */
    @Override
    public void paint(Graphics g) {
        super.paint(g); 
        
        g.setFont(normalFont);
        // determine the square size
        Dimension dim = getSize();
        squareSize = Math.min(dim.width, dim.height) / board.size;

        //sets the background of the board
        g.setColor(boardColor);
        g.fillRect(0, 0, squareSize * board.size, squareSize * board.size);


		int size = board.getSize();
		int subsize = board.getSubsize();
		int[] distances = new int[4];
		if(size == 9){
			distances[0] = 9; //how far apart x
			distances[1] = 6; //starting x
			distances[2] = 12; //starting y
			distances[3] = 9; // how far apart y
		}
		else if(size == 4){
			distances[0] = 34; //how far apart x
			distances[1] = 20; //starting x
			distances[2] = 26; //starting y
			distances[3] = 34; // how far apart y
		}

	
       
        //highlights a block if selected
		Graphics g4 = (Graphics2D) g;
        if (hb[0] != -1 && hb[1] != -1){
	        
	        g4.setColor(highlightColor);
	        g4.fillRect(hb[0]*squareSize, hb[1]*squareSize,squareSize, squareSize);
        }
        boolean[][] l = board.getLocked();
        g4.setColor(lockedColor);
        for(int i = 0; i < size; ++i){
        	for(int j = 0; j< size; ++j){
        		if(l[i][j])
        			g4.fillRect(j*squareSize, i*squareSize,squareSize, squareSize)	;
        	}
        }
   		//Puts values in their corresponding values on board
		Graphics2D g3 =  (Graphics2D) g;
		g3.setColor(lineColor);
		int[][] b = board.getBoard();
		
		Graphics2D gSmall =  (Graphics2D) g;
		//gSmall.setFont(new Font("TimesRoman", Font.PLAIN, 4));
		
		for(int i = 0; i < size; ++i){
			for (int j = 0; j < size; ++j){
				if (b[i][j] != 0)
					g.drawString(Integer.toString(b[i][j]),(squareSize)*(j)-3 + (squareSize/2),(squareSize)*(i)+3 + (squareSize/2));
				else{
					if(hint){
						int[] c = {j, i, 0};
						gSmall.setFont(smallFont);
						for(int k = 0; k < subsize; ++k){
							for(int m = 0; m < subsize; ++m){
								c[2]= m*subsize + (k+1);
								if(board.validCoordinates(c))
									gSmall.drawString(Integer.toString(c[2]),(squareSize)*(j) + (k * distances[0]) + distances[1] ,(squareSize)*(i) + distances[2] +  (m * distances[3])  );
								
							}
							
						}
						gSmall.setFont(normalFont);
								
					}
				}
			}
		}
		
		//Draws the lines on the board
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(lineColor);
		Line2D lin = new Line2D.Float();
		for(int i = 0; i <= size * squareSize; i += squareSize){
			lin.setLine(0, i, squareSize * board.size, i);
			g2.draw(lin);
			lin.setLine(i, 0, i, squareSize * board.size);
			g2.draw(lin);
			if((i/squareSize)%subsize == 0){
				lin.setLine(0, i+1, squareSize * board.size, i+1);
				g2.draw(lin);
				lin.setLine(i+1, 0, i+1, squareSize * board.size);
				g2.draw(lin);
				lin.setLine(0, i-1, squareSize * board.size, i-1);
				g2.draw(lin);
				lin.setLine(i-1, 0, i-1, squareSize * board.size);
				g2.draw(lin);
				
				
		
			}
		}
    }

}
