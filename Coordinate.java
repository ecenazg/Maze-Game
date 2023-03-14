//-----------------------------------------------------
// Title: Coordinate class
// Description: This class is used for storing coordinate attributes.
//-----------------------------------------------------

public class Coordinate {
	
	
	int x, y;
	double distanceFromCenter;

	Coordinate(int a, int b)
    {
        x = a;
        y = b;
    }

	@Override
	public String toString() {
		return "[" + x + "," + y + "]";
	}
    
	
}

