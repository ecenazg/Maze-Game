import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

//-----------------------------------------------------
// Title: MazeSolver Class
// Author: Ecenaz Güngör
// Description: This class contains reading input and solving maze functions.
// I used breadth first search(BFS) approach to traversing over maze. After 
// successfully find a treasure that I backtrack to points until start point
// using stack.
//-----------------------------------------------------
public class MazeSolver {

	// Four direction (right, down, left, up)
	// If the problem is changed from 4 to 8 direction, I just add new directions
	// and approach still works. For example to go right down add (1,1) or left up
	// add (-1,-1) etc.
	private static final int[][] Directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

	// Number of rows and columns info
	int rowNum, colNum;

	// The labyrinth. Walls are +, Treasuries are E and remainings are path
	// I assume inputs are error-free. I mean inputs do not contain other
	// characters
	static char[][] maze;

	// Since initially we don't have row and column size info, I store maze to
	// this temporary arraylist (not array). I can also traverse over this list. But
	// each
	// time I need to write mazeArrayList.get(i).charAt(j). It reduces the
	// readability of the code.
	ArrayList<String> mazeArrayList;

	// Start point and multiple end points.
	Coordinate startPoint;
	List<Coordinate> endPoints;

	// Holds all successful paths
	ArrayList<List<Coordinate>> resultList;

	// Main function. Initially create an instance. Then read inputs from file.
	// Then do some minor adjustments. Finally find the paths.
	public static void main(String[] args) throws FileNotFoundException {
		MazeSolver mazeSolver = new MazeSolver(); // create an object
		mazeSolver.readInput();
		mazeSolver.init();
		mazeSolver.findPaths();
		mazeSolver.printPaths();

	}

	// Ask user to a file name.
	// Then open a file reader and read line by line.
	// Set row and column size.
	private void readInput() throws FileNotFoundException {
		Scanner console = new Scanner(System.in);
		System.out.println("Input file name:");
		String fileName = console.nextLine();
		console.close();
		// The close() method of closes the scanner which has been opened.

		Scanner sc = new Scanner(new BufferedReader(new FileReader(fileName)));
		mazeArrayList = new ArrayList<String>();

		while (sc.hasNextLine()) {
			rowNum++;
			String line = sc.nextLine().trim();
			// The trim() method removes whitespace from both ends of a string.
			mazeArrayList.add(line);
		}
		sc.close();
		colNum = mazeArrayList.get(0).length();
	}

	// Create 2d maze by using arraylist.
	// Start point is (1,0). If we change it to other point, just alter 1,0 value.
	// Since multiple treasuries can exist on the maze, I need to hold each ones
	// coordinate values.
	private void init() {
		maze = new char[rowNum][colNum];
		startPoint = new Coordinate(1, 0, null);
		endPoints = new ArrayList<Coordinate>();
		resultList = new ArrayList<List<Coordinate>>(); // result list (1,2,3)

		int row = 0;
		for (String str : mazeArrayList) {
			for (int i = 0; i < str.length(); i++) {
				if (str.charAt(i) == 'E')
					endPoints.add(new Coordinate(row, i, null)); // Treasure is found
				if (str.charAt(i) == '+' || str.charAt(i) == '-' || str.charAt(i) == '|')
					maze[row][i] = '+'; // I changed all walls to + due to code beauty
				else
					maze[row][i] = str.charAt(i); // Everything else
			}
			row++;
		}
	}

	// This function call findPaths function to find path from start to treasure.
	// Then add paths to resultList
	// I need to clear visit information to other treasuries.
	// Null path means, no valid path can be available.
	private void findPaths() {
		MazeHelper mazeHelper = new MazeHelper();
		mazeHelper.visited = new boolean[rowNum][colNum];
		while (true) {
			List<Coordinate> path = solveBFS(mazeHelper);
			if (path != null) {
				resultList.add(path);
				mazeHelper.resetVisited();
			} else
				break;
		}
	}

	// The core function of solving the maze. I implemented Breadth first search
	// (BFS) approach to travel. I added possible points to the list and call them
	// each time to reach treasure.
	// If the point is out of bound or already visited then I neglect it.
	private List<Coordinate> solveBFS(MazeHelper mazeHelper) {

		// The List store possible points.
		// I choose LinkedList class, since I remove first element each time
		// Removing first element in linkedlist is more efficient than Arraylist
		LinkedList<Coordinate> nextToVisit = new LinkedList<>();

		// Add start point to list
		nextToVisit.add(startPoint);

		// If no element in list, then we don't have any valid path
		while (!nextToVisit.isEmpty()) {
			// Take Current point. Head of the linkedlist
			Coordinate cur = nextToVisit.remove();

			// If the point is out of box or already visited then continue
			// I can handle circularity problem by using visited boolean array
			if (!mazeHelper.isValidLocation(maze, cur.getRow(), cur.getCol())
					|| mazeHelper.isVisited(cur.getRow(), cur.getCol())) {
				continue;
			}

			// If the point is a wall then continue
			// I mark the wall as visited. Actually the line is ridiculous.
			// But I want to the walls are eliminated by first if clause
			// after first visit.
			if (mazeHelper.isWall(maze, cur.getRow(), cur.getCol())) {
				mazeHelper.setVisited(cur.getRow(), cur.getCol(), true);
				continue;
			}

			// If the point is a treasure, then call backtrack functions to find
			// paths. I changed the treasure to wall to prevent adding the point
			// to the path of other treasuries.
			if (mazeHelper.isTreasure(cur.getRow(), cur.getCol(), endPoints)) {
				List<Coordinate> retList = backtrackPath(cur);
				mazeHelper.setWall(maze, cur.getRow(), cur.getCol());
				endPoints.remove(cur);
				return retList;
			}

			// The point is not wall, not treasure and in the maze area. This
			// means it is a path. So add 4 directions to the linkedlist tail.
			// I may add a control here to add only valid points (non visited,
			// non wall). I prefer add all possible points and control them in
			// another place.
			for (int[] dir : Directions) {
				Coordinate next = new Coordinate(cur.getRow() + dir[0], cur.getCol() + dir[1], cur);
				nextToVisit.add(next);
			}
			mazeHelper.setVisited(cur.getRow(), cur.getCol(), true);
		}
		return null;
	}

	// This function is called when i find a treasure.
	// Since points are stored reverse order (Treasure to start), I used stack
	// to reverse them.
	// Stacked elements are popped in a list and returned to caller function.
	private List<Coordinate> backtrackPath(Coordinate cur) {
		List<Coordinate> path = new ArrayList<>();
		Coordinate iter = cur;
		Stack<Coordinate> st = new Stack<>();
		while (iter != null) {
			st.add(iter);
			iter = iter.previous;
		}
		while (!st.isEmpty()) {
			path.add(st.pop());
		}
		return path;
	}

	// Initially print number of paths
	// Then print all paths froms start to end
	// Since my approach changes treasure point to wall, I write path until
	// treasure and put E to end.
	private void printPaths() {
		int index = 1;
		System.out.println(resultList.size() + " treasures are found.");
		if (resultList.size() > 0)
			System.out.println("Paths are:");
		for (List<Coordinate> path : resultList) {
			System.out.print(index++ + ") ");
			for (int i = 0; i < path.size() - 1; i++)
				System.out.print(maze[path.get(i).getRow()][path.get(i).getCol()]);
			System.out.println('E');
		}
	}

}
