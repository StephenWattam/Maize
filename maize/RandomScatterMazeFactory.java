package maize;

import java.awt.*;

/** Creates an maze made of random scattered bricks */
public class RandomScatterMazeFactory implements MazeFactory {

    /** Maze width */
    private int width = 0;

    /** Maze height */
    private int height = 0;

    /** Maze data */
    private boolean[][] data = null;

    private Point start = null;
    private Point finish = null;

    /** Default Constructor */
    public RandomScatterMazeFactory() { }

    /** Public method for getting the maze.
     *
     * @param    width       Width of the maze.
     * @param    height      Width of the maze.
     *
     * @return         Maze object.
     */
    public Maze getMaze(int width, int height){

        // min
        width = Math.max(width, 7);
        height = Math.max(height, 7);

        // oddness
        // Ensure the dimensions are odd
        width = ((int)(width/2)) * 2 + 1;
        height = ((int)(height/2)) * 2 + 1;

        this.width = width;
        this.height = height;

        this.buildBlankMaze();

        return new Maze(this.data, start.x, start.y, finish.x, finish.y);
    } 

    /** Creates a maze with scattered walls.
    */
    protected void buildBlankMaze(){

        this.data = new boolean[this.width][this.height];
        boolean flip = false;

        this.start = new Point();
        this.finish = new Point();

        start.x = 1 + (int)(Math.random() * (this.width - 2)) ;
        start.y = 1 + (int)(Math.random() * (this.height - 2)) ;

        finish.x = 1 + (int)(Math.random() * (this.width - 2)) ;
        finish.y = 1 + (int)(Math.random() * (this.height - 2)) ;

        /**Walls go on in 25% of the squares.*/
        for(int i = 0; i < (this.width * this.height)*0.25; i++){
            int x = 1 + (int)(Math.random() * (this.width-3));
            int y = 1 + (int)(Math.random() * (this.height-3));

            this.data[x][y] = true;
        }


        this.data[start.x][start.y] = false;
        this.data[finish.x][finish.y] = false;

    }

}


