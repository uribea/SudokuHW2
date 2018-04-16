package sudoku.model;
/**
 * Doubly linked list to undo and redo coordinates in the board
 * @author Daniel Almeraz and Alan Uribe
 *
 */
public class CoordinatesDLL {
	/**  x coordinate*/
	int x;
	
	/** y coordinate*/
	int y;
	
	/** original value that is going to be changed*/
	int before;
	
	/** the value that the original is going to change to*/
	int after;
	
	/**previous in the linked list*/
	CoordinatesDLL prev;
	
	/** next in the linked list*/
	CoordinatesDLL next;
	
	/**
	 * 
	 * @param xIN x coordinate changed
	 * @param yIN y coordinate changed
	 * @param b values that was originally in the board
	 * @param a values that the original value is going to be changed to
	 * @param p previous
	 * @param n next
	 */
	public CoordinatesDLL(int xIN, int yIN, int b, int a, CoordinatesDLL p, CoordinatesDLL n) {
		x = xIN;
		y = yIN;
		before = b;
		after = a;
		prev = p;
		next = n;
		
	}
	/**
	 * prints out the contents of a node
	 * used for debugging
	 */
	public void print(){
		System.out.println("x = "+x);
		System.out.println(("y = "+y));
		System.out.println("before = " + before);
		System.out.println("after = " + after);
	}

}
