package maize;

import java.awt.Point;

/** Creates an empty maze for the purposes of testing */
public class EmptyMazeFactory implements MazeFactory {


    /** Default constructor. */
    public EmptyMazeFactory() { }

    /** Create the empty maze.
     *
     * @param    width       Width of the maze.
     * @param    height      Width of the maze.
     *
     * @return               Maze instance
     */
    public Maze getMaze(int width, int height){
        // Min width and height
        width = Math.max(width, 4);
        height = Math.max(height, 4);

        boolean[][] data = new boolean[width][height];

/*        // Write borders
        for(int i =0; i < width; i++)
            for(int j = 0; j < height; j++)
                data[i][j] = (i == 0 || j == 0 || i == width-1 || j == height-1)? true : false;
  */          

        Point end = new Point(width - 2, height - 2);
        Point start = null;
        while(start == null || start.equals(end)){
            start = new Point(1 + (int)(Math.random() * width-3), 1 + (int)(Math.random() * height-3));
        }
       
        // Create maze
        return new Maze(data, start.x, start.y, end.x, end.y, this.getName());
    } 

    public String getName(){
        return "Empty";
    }
}
