package sudoku.model;


/**
 * Class to run solving algorithm on the board
 * @author Daniel Almeraz and Alan
 *
 */
public class Solver {
	/** the board the class needs to run its algorithms on*/
	Board board;
	/** size of the board*/
	int size;
	/**
	 * constructor for solver
	 * @param b instance of board
	 * @param s the size of the board
	 */
	public Solver(Board b, int s){
		board = b;
		size = s;
		
	}
	/**
	 * solve the board, used for as a more intuitive way to class solve that calls another solve
	 * with valuables it needs
	 * @return boolean to see if board is solvable
	 */
	public boolean solve(){
		//int[] ca = {0, 0,((int) Math.random()*9) + 1};
		//board.setCoordinates(ca);
		return solve(0, 0);
	}
	/**
	 *  solve the board
	 * @param x place to start in board 
	 * @param y place to start in board
	 * @return boolean to see if board is solvable
	 */
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
		if(board.hasValueIn(x, y)) return (solve(xN, yN));

		if(board.isSolved()) 	return true;
		else{
			for (int i = 1; i <= size; ++i){
				//System.out.println(y+ " " +x + " "+i);
				int[] ca = {x,y,i};
				if (board.validCoordinates(ca)){
					board.setCoordinates(ca, true);
					if(solve(xN, yN)) return true;
					else{
						ca[0] = x;
						ca[1] = y;
						ca[2] = 0;
						board.setCoordinates(ca, true);
					}
				}
				
			}
		}
		return false;
		
	}
	public boolean isSolvable(){
		/*Board tempBoard = board;
		tempBoard.setBoardArray(board.getBoard());
		Solver tempSolver = new Solver(tempBoard, tempBoard.getSize());
		*/
		int[][] temp = new int[board.size][board.size];//board.getBoard();
		int[][] temp2 = board.getBoard();
		for(int i = 0 ; i < temp.length; i++){
			for(int j = 0 ; j < temp.length; j++){
				//System.out.print(temp2[i][j]+" ");
				temp[i][j] = temp2[i][j];
			}
			//System.out.println();
		}
		//System.out.println("-----------------------");
		boolean sol = solve();
		board.setBoardArray(temp);
		board.isSolved();
		return sol;
	}
}
