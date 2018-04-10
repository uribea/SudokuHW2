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
	private int[][] lsValue;
	
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
	//public Board(int size){
		//this.size = size;
		//subsize = (int)Math.sqrt((double)size);
		//emptySquares = size * size;
		//board = new int[size][size];
	//}
	/**
	 * Constructor for board.
	 * @param size the size of the board desired. 
	 * @param info a string given on the format provided through the class website, sets locked starting values
	 */
	public Board(int size){
		this.size = size;
		subsize = (int)Math.sqrt((double)size);
		emptySquares = size * size;
		board = new int[size][size];
		int lsValuesNum = numlsValues();
		lsValue = new int[3][lsValuesNum];//0=x, 1=y, 2=v
		solve();
		removeValues(lsValuesNum);
		
		setLockedCoordinates(lsValuesNum);
		
	}
	
	/** gives the amount of preset coordinates*/
	private int numlsValues(){
		return 12 +(int)(Math.random() *8);
	}
	
	/**converts the string preset values into an array */
	private void removeValues(int num){
		int i = size*size - num;
		int xT, yT; //temp for x and y coordinates
		while(i < 0){
			xT = (int)Math.random()*size;
			yT = (int)Math.random()*size;
			if(board[xT][yT] == 0){
				
			}
			else{
				board[xT][yT] = 0;
				i--;
			}
		}

		
	}
	
	/** it sets the preset values*/
	private void setLockedCoordinates(int lsValuesNum){
		int x = 0;
		for(int i = 0; i < size; ++i){
			for(int j = 0; j < size; ++j){
				if(board[i][j] != 0){
					lsValue[0][x] = i;
					lsValue[1][x] = j;
					lsValue[2][x] = board[i][j];
					
				}
			}
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
	//MISSING JAVADOC
	public boolean solve(){
		return solve(0, 0);
	}
	//MISSING JAVADOC
	public boolean solve(int x, int y){
		int xN, yN; //new x and y
		if(x == size-1){
			xN = 0; 
			yN = y + 1;
		}
		else{
			xN = x + 1;
			yN = y;
		}

		if(isSolved()) 	return true;
		else{
			for (int i = 1; i <= size; ++i){
				int[] ca = {x,y,i};
				if (validCoordinates(ca)){
					setCoordinates(ca);
					if(solve(xN, yN)) return true;
					else{
						ca[0] = x;
						ca[1] = y;
						ca[2] = 0;
						setCoordinates(ca);
					}
				}
				
			}
		}
		return false;
		
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
