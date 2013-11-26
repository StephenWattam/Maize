package maize;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Generates a map using a Randomized Prim's Algorithm.
 *
 * See http://en.wikipedia.org/wiki/Maze_generation_algorithm#Randomized_Kruskal.27s_algorithm for details
 *
 * @author John Vidler
 */
public class PrimMazeFactory implements MazeFactory
{
    /** Maze width */
    private int width = 0;

    /** Maze height */
    private int height = 0;

    /** Maze data */
    private boolean[][] data = null;
    private ArrayList<Wall> wallList = new ArrayList<Wall>();

    /** Default Constructor */
    public PrimMazeFactory() { }

    /** Public method for getting the maze.
    *
    * @param    width       Width of the maze.
    * @param    height      Width of the maze.
    *
    * @return         Maze object.
    */
    public Maze getMaze(int width, int height){
        /* Check for sane values, otherwise use defaults. */
        if(width < 7 || height < 7){
            this.width = 7;
            this.height = 7;
        }

        this.width = (this.width == 0) ? width : this.width;
        this.height = (this.height == 0) ? height : this.height;

        this.data = new boolean[height][width];

        return generatePrim( width, height );
    }

    protected class Wall
    {
        Point position;
        Point opposite;

        public Wall( Point pos, Point opp )
        {
            position = pos;
            opposite = opp;
        }
    }

    protected Maze generatePrim( int width, int height )
    {
        Random random = new Random();

        // Fill the maze entirely.
        for( int y=0; y<this.height-1; y++ )
            for( int x=0; x<this.width-1; x++ )
                this.data[y][x] = true;

        // Pick a random start position
        Point start = new Point( random.nextInt( this.width-2 ) + 1, random.nextInt( this.height-2 ) + 1 );
        this.data[start.y][start.x] = false;

        // Add the walls around the start to the wall list.
        wallList.add( new Wall(new Point(start.x - 1, start.y), new Point(start.x - 2, start.y)) );
        wallList.add( new Wall(new Point(start.x + 1, start.y), new Point(start.x + 2, start.y)) );
        wallList.add( new Wall(new Point(start.x, start.y - 1), new Point(start.x, start.y - 2)) );
        wallList.add( new Wall(new Point(start.x, start.y + 1), new Point(start.x, start.y + 2)) );

        Point endPoint = null;

        // While we have walls...
        while( wallList.size() > 1 )
        {
            Wall randomWall = wallList.get( random.nextInt( wallList.size() ) );

            if( !isValidPosition( randomWall.position ) )
            {
                wallList.remove( randomWall );
                continue;
            }

            // Is the other side of this wall already in the maze?
            if( !isWall( randomWall.opposite ) )  // Yes!
                wallList.remove( randomWall );
            else  // No!
            {
                // Mark this wall as a passage
                Point passage = randomWall.position;
                Point opposite = randomWall.opposite;

                // Clamp the end point to within the bounds of the maze
                endPoint = new Point( Math.min(Math.max(opposite.x, 1), width), Math.min(Math.max(opposite.y, 1), height) );

                this.data[passage.y][passage.x] = false;
                this.data[opposite.y][opposite.x] = false;

                // Add the surrounding non-passage cells to the wall list
                if( isWall( new Point(opposite.x - 1, opposite.y) ))
                    wallList.add( new Wall(new Point(opposite.x - 1, opposite.y), new Point(opposite.x - 2, opposite.y)) );
                if( isWall( new Point(opposite.x + 1, opposite.y) ))
                    wallList.add( new Wall(new Point(opposite.x + 1, opposite.y), new Point(opposite.x + 2, opposite.y)) );
                if( isWall( new Point(opposite.x, opposite.y - 1) ))
                    wallList.add( new Wall(new Point(opposite.x, opposite.y - 1), new Point(opposite.x, opposite.y - 2)) );
                if( isWall( new Point(opposite.x, opposite.y + 1) ))
                    wallList.add( new Wall(new Point(opposite.x, opposite.y + 1), new Point(opposite.x, opposite.y + 2)) );
            }
        }

        Wall end = wallList.get( 0 );
        wallList.clear();
        this.data[end.position.y][end.position.x] = true; // Mark this as a wall

        endPoint = findNearbySpace( endPoint );
        if( endPoint == null )
            endPoint = end.opposite;

        return new Maze( this.data, start.x, start.y, endPoint.x, endPoint.y, this.getName() );
    }

    protected Point findNearbySpace( Point start )
    {
        // Clamp the endpoint to 1 wall inside the field.
        start = new Point( Math.min(Math.max(start.x, 1), width), Math.min(Math.max(start.y, 1), height) );

        if( this.data[start.y][start.x] )
            return start;

        for( int y=0; y<2; y+=2)
            for( int x=0; x<2; x+=2)
                if( this.data[Math.min(Math.max(y, 1), width)][Math.min(Math.max(x, 1), height)] )
                    return new Point( start.x+x, start.y+y );

        return null;
    }

    protected boolean isValidPosition( Point point )
    {
        if( point.x > 0 && point.x < this.width-1 )
            if( point.y > 0 && point.y < this.height-1 )
                return true;
        return false;
    }

    protected boolean isWall( Point point )
    {
        if( isValidPosition( point ) )
            return this.data[point.y][point.x];
        return false;  // Return true if we're outside the maze, all blocks there are walls!
    }

    public String getName(){
        return "Randomized Prim's Algorithm";
    }

}


