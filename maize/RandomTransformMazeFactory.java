

package maize;

import java.awt.Point;

import java.util.Random;

/** Creates an empty maze for the purposes of testing */
public class RandomTransformMazeFactory implements MazeFactory {

    // The child maze factory, from which to rotate mazes
    private MazeFactory factory;

    private static Random random = new Random();

    /** Accept a maze factory to rotate mazes from. */
    public RandomTransformMazeFactory(MazeFactory factory) { 
        this.factory = factory;
    }
    
    public String getName(){
        return factory.getName() + " (rand rot/flip)";
    }

    /** Create the empty maze.
     *
     * @param    width       Width of the maze.
     * @param    height      Width of the maze.
     *
     * @return               Maze instance
     */
    public Maze getMaze(int width, int height){

        // Get a maze...
        Maze m = factory.getMaze(width, height);

        // Randomly rotate up to 3 times, possibly flipping each time
        boolean alreadyFlipped = false;
        for(int i=1; i<random.nextInt(4); i++){
            if(!alreadyFlipped && random.nextBoolean()){
                alreadyFlipped = true;
                flipMazeV(m);
            }
            m = rotateMazeCCW( m );
        }

        return m;
    }

    /** Rotate a maze 90 degrees counter clockwise.
     */
    private Maze rotateMazeCCW( Maze m ){
        // Load width/height 'backwards'
        int newWidth = m.getHeight();
        int newHeight = m.getWidth();

        // Read start/end
        Point newStart = new Point(m.getEntY(), m.getEntX());
        Point newEnd   = new Point(m.getExiY(), m.getExiX());

        // Randomly swap start/end
        if(random.nextBoolean()){
            Point temp = newStart;
            newStart = newEnd;
            newEnd = temp;
        }

        // Rotate the data              // [height][width] because newWidth is old height
        boolean[][] newData = new boolean[newWidth][newHeight];
        for(int i=0; i<newHeight; i++)
            for(int j=0; j<newWidth; j++)
                newData[j][i] = m.getPoint(i, j);

        /* Return a new maze object, rotated. */
        return new Maze(newData, newStart.y, newStart.x, newEnd.y, newEnd.x, this.getName());
    }


    /** Flip a maze vertically. 
     */
    private Maze flipMazeV(Maze m){
        // Load width/height 'backwards'
        int width = m.getWidth();
        int height = m.getHeight();

        // Read start/end
        Point newStart = new Point(m.getEntX(), height - m.getEntY());
        Point newEnd   = new Point(m.getExiX(), height - m.getExiY());

        // Randomly swap start/end
        if(random.nextBoolean()){
            Point temp = newStart;
            newStart = newEnd;
            newEnd = temp;
        }

        // Rotate the data 
        boolean[][] newData = new boolean[width][height];
        for(int i=0; i<width; i++)
            for(int j=0; j<height; j++)
                newData[i][j] = m.getPoint(i, height - j);

        /* Return a new maze object, flipped. */
        return new Maze(newData, newStart.x, height - newStart.y, newEnd.x, height - newEnd.y, this.getName());
    }

}
