package maize;

import java.io.*;
import java.awt.Point;
import java.util.*;

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

    /** Route information. */
    private Vector<Point> route = null;

    /** Total path length cache */
    private int pathLength = 0;

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

        // Count path length, i.e. all of the un-wall squares
        this.pathLength = 0;
        for(int i=0; i<this.width; i++){
            for(int j=0; j<this.height; j++){
                this.pathLength += this.data[i][j] ? 0 : 1;
            }
        }

    } 

    /** Solve the maze using MazeSolver.  The route is accessible using getRoute().
     */
    public void solve(){
       MazeSolver solver = new MazeSolver(this);
       this.route = solver.solve();
    }

    /** If a route is set, returns true if the Point given is on the route.
     *
     * @param p The point to query
     * @return True if the co-ordinates given are on the route.
     */
    public boolean getIsOnRoute(Point p){
        if(this.route == null || this.route.size() == 0)
            return false;

        // Match any in the route list
        for(Point r: route)
            if(r.equals(p))
                return true;

        return false;
    }

    /** If a route is set, returns true if the Point given is on the route.
     *
     * @param x The x co-ordinate
     * @param y The y co-ordinate
     * @return True if the co-ordinates given are on the route.
     */
    public boolean getIsOnRoute(int x, int y){
        return getIsOnRoute(new Point(x, y));
    }

    /** Return the total path length of the maze.
     */
    public int getTotalPathLength(){
        return pathLength;
    }
        

    /** Returns the current route, or null if one is not set.
     */
    public Vector<Point> getRoute(){
        return this.route;
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
