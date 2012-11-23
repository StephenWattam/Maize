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

        width = Math.max(width, 7);
        height = Math.max(height, 7);

        // Ensure the dimensions are odd
        width = ((int)(width/2)) * 2 + 1;
        height = ((int)(height/2)) * 2 + 1;

        this.width = width;
        this.height = height;

        this.buildBlankMaze();

        return new Maze(this.data, start.x, start.y, finish.x, finish.y);
    } 

    private double distance(Point a, Point b)
    {
        int dx = a.x - b.x;
        int dy = a.y - b.y;
        double prod = (double)((dx * dx) + (dy * dy));
        return Math.sqrt(prod);
    }

    /** Creates a maze with scattered walls.
    */
    protected void buildBlankMaze(){

        this.data = new boolean[this.width][this.height];
        boolean flip = false;

        this.start = new Point();
        this.finish = new Point();

        do{
            start.x = 1 + (int)(Math.random() * (this.width - 2)) ;
            start.y = 1 + (int)(Math.random() * (this.width - 2)) ;

            finish.x = 1 + (int)(Math.random() * (this.height - 2)) ;
            finish.y = 1 + (int)(Math.random() * (this.height - 2)) ;
        }//make sure start is >1/2 the diagonal length of the maze away from the finish
        while(distance(start, finish) < (0.5 * Math.sqrt((this.width * this.width) + (this.height * this.height)) ));


        /**Walls go on in 25% of the squares.*/
        for(int i = 0; i < (this.width * this.height)*0.25; i++){

            this.data[1 + (int)(Math.random() * (this.width-3))][1 + (int)(Math.random() * (this.height-3))] = true;
        }

        for(int x =0;x < this.width; x++){
            for(int y =0; y < this.height; y++){
                if(x ==0 || y ==0 || x == this.width-1 || y ==this.height-1){
                    this.data[x][y] = true;
                }
            }
        }

        this.data[start.x][start.y] = false;
        this.data[finish.x][finish.y] = false;

    }

}


