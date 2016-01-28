
package maize;

import java.awt.Point;

public class Q3iiMazeFactory implements MazeFactory {


    /** Default constructor. */
    public Q3iiMazeFactory() { }

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

        // Fill
        for(int i =0; i < width; i++)
            for(int j = 0; j < height; j++)
                data[i][j] = true;
            

        Point end = new Point(1, 1);
        Point start = new Point((int)(Math.random() * (width - 4)) + 3, (int)(Math.random() * (height - 4)) + 2);

        int mid = (start.y - end.y) / 2;
        for(int i=start.y; i >= mid; i--)
            data[start.x][i] = false;

        for(int i=start.x; i>=end.x; i--)
            data[i][mid] = false;

        for(int i=mid; i >= end.y; i--)
            data[end.x][i] = false;
       
        // Create maze
        return new Maze(data, start.x, start.y, end.x, end.y, this.getName());
    } 

    public String getName(){
        return "Q3ii";
    }
}
