
package maize;

import java.awt.Point;

public class Q1aMazeFactory implements MazeFactory {


    /** Default constructor. */
    public Q1aMazeFactory() { }

    /** Create the empty maze.
     *
     * @param    width       Width of the maze.
     * @param    height      Width of the maze.
     *
     * @return               Maze instance
     */
    public Maze getMaze(int width, int height){
        // Min width and height
        width = Math.max(width, 10);
        height = Math.max(height, 10);

        boolean[][] data = new boolean[width][height];

/*        // Write borders
        for(int i =0; i < width; i++)
            for(int j = 0; j < height; j++)
                data[i][j] = (i == 0 || j == 0 || i == width-1 || j == height-1)? true : false;
  */          

        Point end = new Point(5, 1);
        Point start = new Point(5, 9);
       
        // Create maze
        return new Maze(data, start.x, start.y, end.x, end.y, this.getName());
    } 

    public String getName(){
        return "Q1a";
    }
}
