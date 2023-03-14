import java.util.Arrays;
import java.util.List;

//-----------------------------------------------------
//Title: MazeHelper Class
// Description: This class contains helper functions to solve maze. Function names 
// describes itself clearly.
//-----------------------------------------------------

public class MazeHelper {

	// I need to store that a point is whether visited or not. (boolean)
	// It should be the same size as the maze.
	boolean[][] visited;
	
	// I must not go beyond the labyrinth. Negative row-col values and values are
	// greater than maze frame are not valid locations.
	public boolean isValidLocation(char[][] maze, int row, int col) {
        if (row < 0 || row >= maze.length || col < 0 || col >= maze[0].length) {
            return false;
        }
        return true;
    }
	
	// Helper function returns whether point has been visited or not
	public boolean isVisited(int row, int col) {
        return visited[row][col];
    }
	
	// If solver find a treasury then I need to clear all visited information
	// to find next treasury.
	public void resetVisited() {
		for (int i=0;i<visited.length;i++)
			Arrays.fill(visited[i], false);
	}

	// HW said wall can be +, - or | . But these variants have no effect on the 
	// solution. So I can change it all to + sign. So I need to checked only + 
	// sign. It could be something like that if I do not change other signs.
	// if(maze[row][col] == '+' || maze[row][col] == '-' || maze[row][col] == '|')
	public boolean isWall(char[][] maze, int row, int col) {
		if(maze[row][col] == '+')
    		return true;
        return false;
	}

	// Simply set visited info value to given coordinate
	public void setVisited(int row, int col, boolean value) {
		visited[row][col] = value;
	}

	// Control the given coordinate is a treasury or not.
	// I store all treasuries to endPoints list. So simply check the point is 
	// whether in the list or not
	public boolean isTreasure(int row, int col, List<Coordinate> endPoints) {
		for(Coordinate c:endPoints)
    		if(row==c.getRow() && col==c.getCol())
    			return true;
        
    	return false;
	}

	// In my approach, After print the path, I change treasure point to the wall. 
	// So other treasuries' paths do not contains any treasure. 
	public void setWall(char[][] maze, int row, int col) {
		maze[row][col] = '+';
	}
	
}