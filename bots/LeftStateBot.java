package bots;
import maize.*;
import maize.ui.*;
import java.io.Serializable;

/** Left bot using the state system. */
public class LeftStateBot extends StateBot implements Serializable{

    /** Default constructor.
    */
    public LeftStateBot(){
        super();
    }

    /** This function should contain control logic for the bot.
     * Follows left walls.
     *
     * @param    view    View matric from the perspective of the bot, orientated so
     *                   the top of the matrix is facing the same direction as the 
     *                   bot.
     * @param    x       X coord of the bot.
     * @param    y       Y coord of the bot.
     * @param    o       Orientation of the bot @see Orientation
     * @param    fx      X coord of the finish.
     * @param    fy      Y coord of the finish.
     */
    @Override
    public void makeMove(boolean[][] view, int x, int y, int o, int fx, int fy){

        if(isLeftTurn(view)){
            turnLeft();
        } else if(!isForward(view)){
            rotateRight();
        } else {
            forward();
        }
    }

    /** Left turn ahead?
     * Checks the view matrix.
     *
     * @param		view		View Matrix
     *
     * @return						If a left turn is ahead.
     */
    public boolean isLeftTurn(boolean[][] view){
        return !view[0][0]; 
    }

    /** Forward ahead?
     * Checks the view matrix.
     *
     * @param		view		View Matrix
     *
     * @return						.
     */
    public boolean isForward(boolean[][] view){
        return !view[1][0]; 
    }

    /** A nice fluffy name for the bot, like 'Bob' or 'John'. 
     *
     * @return           Bot name.
     */
    @Override
    public String getName(){
        return "LeftStateBot";
    }

    /** Implementation of the Bot interface.
     *
     * @return           Bot Description.
     */
    @Override
    public String getDescription(){
        return "Left wall follower using StateBot.";
    }
}
