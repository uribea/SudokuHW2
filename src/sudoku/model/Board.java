package sudoku.model;

//Code By: Daniel Almeraz
public class Board {
	public int size;
	private int emptySquares;
	private int[][] board;
	private int subsize;  //length of a subregion
	private int[][] lockedStartingValues;
	
	public Board(){
		this(4);
		subsize = 2;
		emptySquares = 16;
		board = new int[4][4];
	}

	public Board(int size){
		this.size = size;
		subsize = (int)Math.sqrt((double)size);
		emptySquares = size * size;
		board = new int[size][size];
	}
	
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
	public int infoValues(String info){
		int counter = 0;
		for(int i = 0; i < info.length(); ++i)
			if(info.charAt(i) == 'x')
				counter++;
		return counter;
	}
	public void storeInfo(String info){
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
	public void setFirstCoordinates(){
		for(int i = 0; i < lockedStartingValues[0].length; ++i){
			board[lockedStartingValues[1][i]][lockedStartingValues[0][i]] = lockedStartingValues[2][i];
			emptySquares--;
		}
			
	}

	//GETTERS
	public int getSize(){
		return size;
	}
	public int getSubsize(){
		return subsize;
	}
	public int[][] getBoard(){
		for(int i = 0; i <board.length; i++){
						for(int j = 0; j <board.length; j++)
							System.out.print(board[i][j] + "-");
					System.out.println();
					}
			System.out.println("reeeeeeeeeeeeeeee");
		return board;
	}
	
	
	public boolean validCoordinates(int[] c){ //CHECKS COORDINATES
		if (c[0] > size || c[1] > size ||c[2] > size){
			System.out.println();
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
	
		for (int i = c[1]/(subsize) * subsize, x = 0; x < subsize; ++x, ++i){ //checks subregion
			for (int j = c[0]/subsize * subsize, y = 0; y < subsize; ++y, ++j ){
				if(board[i][j] == c[2]) return false;
			}
		}
		
		return true;
	}
	
	public void setCoordinates(int[] c){
		if (c[2] == 0) emptySquares++;
		if (board[c[1]][c[0]] == 0) emptySquares--;
		board[c[1]][c[0]] = c[2];
	}
	
	public boolean isSolved() {
		return emptySquares < 1;
	}
}
