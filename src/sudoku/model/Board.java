package sudoku.model;

/**
 * Model class for the Sudoku program 
 * 
 * @author Daniel Almeraz
 * edited by: Daniel Almeraz and Alan Uribe
 */

public class Board {
	
	/** length( or width) of board */
	public int size;
	
	/** number of empty squares in the board*/
	private int emptySquares;
	
	/** the board itself */
	private int[][] board;
	
	/** length (or width) of subregion*/
	private int subsize;
	
	/** stores initial values.
	 * Used to make sure values inside can't be changed*/
	private int[][] lockedStartingValues;
	
	/**
	 * Constructor for board.
	 * Default size of four.
	 */
	public Board(){
		this(4);
		subsize = 2;
		emptySquares = 16;
		board = new int[4][4];
	}
	
	/**
	 * Constructor for board.
	 * @param size the size of the board desired. 
	 */
	public Board(int size){
		this.size = size;
		subsize = (int)Math.sqrt((double)size);
		emptySquares = size * size;
		board = new int[size][size];
	}
	/**
	 * Constructor for board.
	 * @param size the size of the board desired. 
	 * @param info a string given on the format provided through the class website, sets locked starting values
	 */
	public Board(int size, String info){
		this.size = size;
		subsize = (int)Math.sqrt((double)size);
		emptySquares = size * size;
		board = new int[size][size];
		int infoValues = infoValues(info);
		lockedStartingValues = new int[3][infoValues];
		
		storeInfo(info);
		
		setFirstCoordinates();
		
	}
	
	/** gives the amount of preset coordinates*/
	private int infoValues(String info){
		int counter = 0;
		for(int i = 0; i < info.length(); ++i)
			if(info.charAt(i) == 'x')
				counter++;
		return counter;
	}
	
	/**converts the string preset values into an array */
	private void storeInfo(String info){
		int arrayIndex = 0;
		for (int i = 0; i < info.length(); ++i){
			if (info.charAt(i) == 'x')
				lockedStartingValues[0][arrayIndex] = ((int) info.charAt(i+3) - 48);
			else if (info.charAt(i) == 'y')
				lockedStartingValues[1][arrayIndex] = ((int) info.charAt(i+3) - 48);
			else if (info.charAt(i) == 'v'){
				lockedStartingValues[2][arrayIndex] = ((int) info.charAt(i+7) - 48);
				arrayIndex++;
			}
		}
	}
	
	/** it sets the preset values*/
	private void setFirstCoordinates(){
		for(int i = 0; i < lockedStartingValues[0].length; ++i){
			board[lockedStartingValues[1][i]][lockedStartingValues[0][i]] = lockedStartingValues[2][i];
			emptySquares--;
		}
	}

	/** getter for size
	 * @return the size in 1d of the board*/
	public int getSize(){
		return size;
	}
	
	/**getter for subsize
	 * @return the subsize in 1d of the board*/
	public int getSubsize(){
		return subsize;
	}
	
	/** getter for board
	 * @return the boards 2d array*/
	public int[][] getBoard(){

		return board;
	}
	
	/**Checks if coordinates attempting to be entered in the board are valid
	 * @param c the coordinates being attempted to be added
	 * @return whether valid or not
	 * */
	public boolean validCoordinates(int[] c){
		if (c[0] > size || c[1] > size ||c[2] > size){
			return false;
		}
		if(c[2] == 0) return true;
		/*for(int i = 0; i < lockedStartingValues.length; ++i){
			if (c[0] == lockedStartingValues[0][i] && c[1] == lockedStartingValues[1][i])
				return false;
		}*/
		for(int i = 0; i < size; ++i){ //checks vertical and horizontal
			if (board[c[1]][i] == c[2]) return false;
			if (board[i][c[0]] == c[2]) return false;
		}
		
		 //checks subregion
		for (int i = c[1]/(subsize) * subsize, x = 0; x < subsize; ++x, ++i){
			for (int j = c[0]/subsize * subsize, y = 0; y < subsize; ++y, ++j ){
				if(board[i][j] == c[2]) return false;
			}
		}
		return true;
	}
	
	/** sets the coordinates on the board after the validation is passed
	 *  @param c the coordinates being added
	 */
	public void setCoordinates(int[] c){
		if (c[2] == 0) emptySquares++;
		if (board[c[1]][c[0]] == 0) emptySquares--;
		board[c[1]][c[0]] = c[2];
	}
	
	/** Checks if board is solve.
	 * 
	 * @return whether the board is complete or not
	 */
	public boolean isSolved() {
		return emptySquares < 1;
	}
}
