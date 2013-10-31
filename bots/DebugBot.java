package bots;
import maize.*;
import java.io.Serializable;

/** Interface for logic and stuffs */
public class DebugBot implements Bot, Serializable {

    private Bot bot         = new DaveBot();

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

        debug("nextMove([[" + view[0][0] + 
                ", " + view[0][1] + 
                ", " + view[0][2] + 
                "][" + view[1][0] + 
                ", " + view[1][1] + 
                ", " + view[1][2] + 
                "][" + view[2][0] + 
                ", " + view[2][1] + 
                ", " + view[2][2] + 
                "]], " + x + ", " + y + ", " + o + ", " + fx + ", " + fy + ")");
        debug("View at (" + x + "," + y + ") [" + Orientation.getName(o) + "]");
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                debug("(" + (i) + "," + (j) + ") = " + view[i][j]);
            }
            debug("");
        }

        // Get the next move
        int nextMove = bot.nextMove(view, x, y, o, fx, fy);
        debug("Bot instructs us to move " + nextMove); // TODO: look up direction from enum
        return nextMove;
    }

    
    /** Implementation of the Bot interface.
     *
     * @return           Bot name.
     */
    @Override
    public String getName(){
        debug("getName()");
        return "DebugBot (" + bot.getName() + ")";
    }

    /** Implementation of the Bot interface.
     *
     * @return           Bot Description.
     */
    @Override
    public String getDescription(){
        debug("getDescription()");
        return "Wraps another bot, printing info on data in/out.  Currently wrapping " + bot.getName() + ".";
    }


    @Override
    public void start(){
        debug("start()");
        bot.start();
    }

    /** Debug output with prefix. */
    private void debug(String msg){
        System.out.println("DBGBOT: " + msg);
    }
}


