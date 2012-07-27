package bots;
import maize.*;
import java.io.Serializable;

/** Interface for logic and stuffs */
public class LeftBot implements Bot, Serializable {

    private boolean cross = false;
    private boolean tee = false;

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
	    if (!cross){
		cross = true;
		move = Direction.LEFT;
	    } else {
		cross = false;
		move = Direction.FORWARD;
	    }
	}
	/* For managing blind corners */
	else if(!view[0][1] && !view[1][0] && view[1][2])	{	
	    move = Direction.FORWARD;
	    /* For managing T junction */
	} else if(!view[2][1] && !view[0][1] && !view[1][2])	{	
	    move = Direction.LEFT;
	    tee = true;
	} else if(!view[0][1]) {
	    if(!tee){
		move = Direction.LEFT;
	    } else {
		move = Direction.FORWARD;
		tee = false;
	    }
	} else if(view[1][0]){
	    move = Direction.RIGHT;
	} else {
	    move = Direction.FORWARD;
	} 

	return move;
    }

    /** Implementation of the Bot interface.
     *
     * @return           Bot name.
     */
    public String getName(){
	return "LeftBot";
    }

    /** Implementation of the Bot interface.
     *
     * @return           Bot Description.
     */
    public String getDescription(){
	return "Follows left walls.";
    }
}


