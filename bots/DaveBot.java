package bots;
import maize.*;
import java.io.Serializable;

/** Interface for logic and stuffs */
public class DaveBot implements Bot, Serializable {

    /** Implementation of the Bot interface.
     * @see Bot
     * 
     * @param    view    View matric from the perspective of the bot, orientated so
     *                   the top of the matrix is facing the same direction as the 
     *                   bot.
     * @param    x       X coord of the bot.
     * @param    y       Y coord of the bot.
     * @param    o       Orientation of the bot @see Orientation
     * @param    fx      X coord of the finish.
     * @param    fy      Y coord of the finish.
     *
     * @return     Next move in form of Direction.####
     */
    @Override
    public int nextMove(boolean[][] view, int x, int y, int o, int fx, int fy){
        // For those of you not familiar with constants:
        // Orientation.NORTH, EAST, SOUTH, WEST are all integers
        // from 0 to 3.  The code below returns one of these at random.
        return (int)(Math.random() * 4);
    }

    /** Implementation of the Bot interface.
     *
     * @return           Bot name.
     */
    @Override
    public String getName(){
        return "DaveBot";
    }

    /** Implementation of the Bot interface.
     *
     * @return           Bot Description.
     */
    @Override
    public String getDescription(){
        return "Randomly dithers around with no real judgement, like the real dave.";
    }
}

