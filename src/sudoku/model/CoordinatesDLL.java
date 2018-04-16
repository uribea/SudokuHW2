package sudoku.model;

public class CoordinatesDLL {
	int x;
	int y;
	int before;
	int after;
	CoordinatesDLL prev;
	CoordinatesDLL next;
	
	public CoordinatesDLL(int xIN, int yIN, int b, int a, CoordinatesDLL p, CoordinatesDLL n) {
		x = xIN;
		y = yIN;
		before = b;
		after = a;
		prev = p;
		next = n;
		
	}
	public void print(){
		System.out.println("x = "+x);
		System.out.println(("y = "+y));
		System.out.println("before = " + before);
		System.out.println("after = " + after);
	}

}
