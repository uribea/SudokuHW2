package sudoku.model;



public class Solver {
	Board board;
	int size;
	
	public Solver(Board b, int s){
		board = b;
		size = s;
		
	}
	//MISSING JAVADOC
	public boolean solve(){
		return solve(0, 0);
	}
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
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
				System.out.println(y+ " " +x + " "+i);
				int[] ca = {x,y,i};
				if (board.validCoordinates(ca)){
					board.setCoordinates(ca);
					if(solve(xN, yN)) return true;
					else{
						ca[0] = x;
						ca[1] = y;
						ca[2] = 0;
						board.setCoordinates(ca);
					}
				}
				
			}
		}
		return false;
		
	}
	public boolean isSolvable(){
		Board tempBoard = new Board(size);
		tempBoard.setBoardArray(board.getBoard());
		Solver tempSolver = new Solver(tempBoard, tempBoard.getSize());
		return tempSolver.solve();
	}
}
