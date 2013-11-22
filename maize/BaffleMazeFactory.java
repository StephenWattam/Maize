
package maize;

import java.awt.Point;

import java.util.Random;

/** Creates an empty maze for the purposes of testing */
public class BaffleMazeFactory implements MazeFactory {

    /** Default constructor. */
    public BaffleMazeFactory() { }

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

        // Ensure width is odd
        width = width + ((width + 1) % 2);
      
        // Choose a sort-of-random baffle width
        int baffle_width = 2 + new Random().nextInt((int)(width / 4));

        // Gen the grid.
        boolean[][] data = generate(width, height, baffle_width);


        // Place the end based on the number of baffles
        Point end = null;
        if((width - 1) % (baffle_width * 2) == 0)
            end = new Point(width - 2, 1);
        else
            end = new Point(width - 2, height - 2);
        Point start = new Point(1, 1);

       
        // Create maze
        return new Maze(data, start.x, start.y, end.x, end.y, this.getName());
    }

    public String getName(){
        return "Baffle";
    }
  
    // Generate a baffle structure with the given spacing
    private boolean[][] generate(int width, int height, int baffle_width){
        // Init the data
        boolean[][] data = new boolean[width][height];

        // Construct the baffles
        int cutPos = 0;
        for(int i=0; i<width; i += baffle_width){
            cutPos = ((i % (baffle_width * 2) == 0) ? 1 : height - 2);
            data = addBaffle(data, i, height, cutPos);
        }

        return data;
    }

    // Add a baffle to the grid, with a cut at the given height
    private boolean[][] addBaffle(boolean[][] data, int xpos, int height, int cutPos){
        //Draw a line and undraw a cutpoint
        for(int i=0; i<height; i++){
            data[xpos][i] = true;
        }
        data[xpos][cutPos] = false;

        return data;
    }
}
