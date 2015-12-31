package bots;
import maize.*;
import java.io.Serializable;

/** Interface for logic and stuffs */
public class RightBot implements Bot, Serializable {

    // True if we have hit a crossroads
    private boolean cross   = false;

    // True if we have hit a t-junction
    private boolean tee     = false;

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
    public int nextMove(boolean[][] view, int x, int y, int o, int fx, int fy){

        int move = 0;

        /* For managing crossroads */
        if(!view[1][0] && !view[2][1] && !view[1][2] && !view[0][1]){

            // If crossroad marker is not set
            if (!cross){
                // set it, and move right.
                cross = true;
                move = Direction.RIGHT;
            } else {
                // else unset it, and move forwards.
                cross = false;
                move = Direction.FORWARD;
            }
        }/* For managing blind corners */
        else if(!view[2][1] && !view[1][0] && view[1][2])	{	
            move = Direction.FORWARD;
            
        }    /* For managing T junction */
        else if(!view[2][1] && !view[0][1] && !view[1][2])	{	
            move = Direction.RIGHT;
            tee = true;
        } else if(!view[2][1]) {
            if(!tee){
                move = Direction.RIGHT;
            } else {
                move = Direction.FORWARD;
                tee = false;
            }
        } else if(view[1][0]){
            move = Direction.LEFT;
        } else {
            move = Direction.FORWARD;
        } 

        return move;
    }

    /** Implementation of the Bot interface.
     *
     * @return           Bot name.
     */
    @Override
    public String getName(){
        return "RightBot";
    }

    /** Implementation of the Bot interface.
     *
     * @return           Bot Description.
     */
    @Override
    public String getDescription(){
        return "Follows right walls.";
    }


    @Override
    public void start(){}

    @Override
    public void destroy() {
        /* stub */
    }
}


