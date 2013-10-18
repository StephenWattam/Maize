package maize;

import java.io.*;
import java.awt.Point;

/** The Maze model. */
public class Maze implements Serializable {

    /** UID for serializing */
    private static final long serialVersionUID = 6425553015771621669L;

    /** Maze data array */
    private boolean[][] data = null;

    /** Maze width */
    private int width = 0;

    /** Maze height */
    private int height = 0;

    /** Entrance X coord */
    private int entX = 0;

    /** Entrance Y coord */
    private int entY = 0;

    /** Exit X coord */
    private int exiX = 0;

    /** Exit Y coord */
    private int exiY = 0;

    /** Map instance name */
    private String name = "";

    /** Remove default constructor from default scope. */
    private Maze(){}

    /** Constructor to be used by MazeFactory only.
     * Encapsulates the maze data into an easy to use class.
     *
     * @param  data      Maze data
     * @param  width     Maze width
     * @param  height    Maze height
     * @param  entX      Entrance X coord
     * @param  entY      Entrance Y coord
     * @param  exiX      Exit X coord
     * @param  exiY      Exit Y coord
     */
    public Maze(boolean[][] data, int entX, int entY, int exiX, int exiY){
        this.data   = data;
        this.width  = data.length;
        this.height = data[0].length;

        // TODO: check start/finish

        this.entX   = entX;
        this.entY   = entY;
        this.exiX   = exiX;
        this.exiY   = exiY;
        this.name   = this.toString();
    }

    /** Gets the maze data.
     *
     * @return           Maze data in a 2D boolean array
     */
    //public boolean[][] getData(){
    //  return this.data;
    //}

    public boolean getPoint(int x, int y){
        // Ensure walls cover the plane
        if(x <= 0 || y <= 0 || x >= width-1 || y >= height-1 )
            return true;

        return data[x][y];  // mazes stored in columns.
    }

    public boolean getPoint(Point p){
        return data[p.x][p.y];  // mazes stored in columns
    }

    /** Gets the maze width.
     *
     * @return           Maze width
     */
    public int getWidth(){

        return this.width;
    }

    /** Gets the maze height.
     *
     * @return           Maze height
     */
    public int getHeight(){

        return this.height;
    }

    /** Gets the maze's entrance X coord.
     *
     * @return           Maze's entrance X coord
     */
    public int getEntX(){

        return this.entX;
    }

    /** Gets the maze's entrance Y coord.
     *
     * @return           Maze's entrance Y coord
     */
    public int getEntY(){

        return this.entY;
    }

    /** Gets the maze's exit X coord.
     *
     * @return           Maze's exit X coord
     */
    public int getExiX(){

        return this.exiX;
    }

    /** Gets the maze's exit Y coord.
     *
     * @return           Maze's exit Y coord
     */
    public int getExiY(){

        return this.exiY;
    }

    /** Gets the maze's instance name.
     *
     * @return           Maze's instance name
     */
    public String getName(){
        return this.name;
    }

    /** Sets the maze's instance name.
     *
     * @param    name    Maze's instance name
     */
    public void setName(String name){
        this.name = name;
    }

}
