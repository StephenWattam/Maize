package maize;

import java.util.Random;

/** Creates an empty maze for the purposes of testing */
public class LineMazeFactory implements MazeFactory {

    /** Maze width */
    private int width = 0;

    /** Maze height */
    private int height = 0;

    /** Default constructor. */
    public LineMazeFactory() { }

    /** Create the line maze.
     * Wall down the centre with a hole in it.
     *
     * @param    width       Width of the maze.
     * @param    height      Width of the maze.
     *
     * @return               Maze instance
     */
    public Maze getMaze(int width, int height){

        if(width < 10 || height < 10){
            this.width = 9;
            this.height = 9;
        }

        this.width = width;
        this.height = height;

        boolean[][] data = new boolean[this.width][this.height];

        for(int x =0; x < this.width; x++){
            for(int y = 0; y < this.height; y++){
                data[x][y] = (x == this.width/2 && y != this.height/2) ? true : false;
                if(x == 0 || y == 0 || x == this.width-1 || y == this.height-1){
                    data[x][y] = true;
                }

            }
        }

        int startx = 2 + (int)(Math.random() * ((this.width/2)-2)) ;
        int starty = 2 + (int)(Math.random() * ((this.height/2)-2)) ;
        int finishx = this.width/2 + 1 + ((int)(Math.random() * ((this.width/2)-3)));
        int finishy = this.height/2 + 2 + ((int)(Math.random() * ((this.height/2)-3)));

        // Randomly swap start/finish
        if(new Random().nextBoolean())
            return new Maze(data, finishx, finishy, startx, starty, this.getName());

        return new Maze(data, startx, starty, finishx, finishy, this.getName());
    }

    public String getName(){
        return "Line";
    }
}

