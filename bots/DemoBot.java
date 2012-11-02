package bots;
import maize.*;
import java.io.Serializable;

// For text status output 
import java.text.DecimalFormat;

// for full modelling
import java.awt.Point;
import java.util.HashSet;
import java.util.Vector;

public class DemoBot implements Bot, Serializable {

    // Move 150 times, then switch to the next bot type
    private static final int MOVES_PER_BOT = 200;
    // Remember a list of bots and loop over them
    private Vector<Bot> bots    = new Vector<Bot>();
    // Keeps track of which bot we are currently using
    private int botCounter      = 0;
    // When this reaches 0, switch bot.
    private int moveCounter     = MOVES_PER_BOT;


    /** Sets up default bots */
    public DemoBot(){
        // Add some bots
        this.bots.add(new LeftBot());
        this.bots.add(new Gorad());
        this.bots.add(new RightBot());
        this.bots.add(new DaveBot());
    }

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
      
        // Display information about the current view
        displayView(view, x, y, o, fx, fy);

        // Memorise points and plot experience
        rememberPoints(Orientation.rotateToNorth(view, o), x, y);
        renderPoints(x, y);

        // Then defer to inner bot.
        moveCounter --;
        if(moveCounter == 0){
            moveCounter = MOVES_PER_BOT;
            botCounter = (botCounter + 1) % bots.size();

            System.out.println("*** Switching to bot: " + bots.get(botCounter).getName());
        }
        return bots.get(botCounter).nextMove(view, x, y, o, fx, fy);
                /* (int)(Math.random() * 4); */
    }

    /** Implementation of the Bot interface.
     *
     * @return           Bot name.
     */
    @Override
    public String getName(){
        return "DemoBot";
    }

    /** Implementation of the Bot interface.
     *
     * @return           Bot Description.
     */
    @Override
    public String getDescription(){
        return "Current wrapping " + bots.size() + " bot[s].  Remembered " + points.size() + " points.";
    }

    /* ================================================================================== */
    /*  Private methods                                                                   */
    /* ================================================================================== */

    /* ------------------------------------------------------ */
    //  Text output
    /* ------------------------------------------------------ */
    /** Outputs status on Bots */
    private void displayView(boolean[][]view, int x, int y, int o, int fx, int fy){
        System.out.println("==================================================");
        System.out.println(" Status");
        System.out.println("==================================================");
        System.out.println("  Position: " + x + ", " + y);
        System.out.println("  Finish: " + fx+ ", " + fy );
        System.out.println("  Distance: " + (Math.abs(fx-x) + Math.abs(fy-y)) + " manhattan, " + new DecimalFormat("#.00").format(Math.sqrt(Math.pow(fx-x, 2) + Math.pow(fy-y,2))) +" euclidean");
        System.out.println("  Orientation: " + Orientation.getName(o));
        System.out.println("  Current bot: " + bots.get(botCounter).getName());
        System.out.println("  Context (default: Forward is up):");
        printContext(view);
        System.out.println("  Context (adjusted: North is up):");
        printContext(Orientation.rotateToNorth(view, o));

        System.out.println("==================================================");
    }

    /** Prints a context matrix. */
    private static void printContext(boolean[][] view){
        System.out.println("     +---+");
        for(int i=0;i<3;i++){
            System.out.print("     |");
            for(int j=0;j<3;j++){
                System.out.print( "" + viewChar(view, i, j) );
            }
            System.out.print( "|\n" );
        }
        System.out.println("     +---+");
    }

    /** Returns the symbols to use when printing context */
    private static char viewChar(boolean[][] view, int i, int j){
        char c = '.';
        if(view[i][j])
            c = '#';
        return c;
    }

    /** Manage points for a general maze overview render */
    private int[] maxXY = {0,0};
    private HashSet<Point> points = new HashSet<Point>();

    private void rememberPoints(boolean[][] view, int xBot, int yBot){
        int x = 0;
        int y = 0;

        // Loop through, adding absolute points
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                if(view[i][j]){
                    x = xBot + (i-1);
                    y = yBot + (j-1);

                    // This may be abusive, but it uses a hash and prevents
                    // me from having to do duplicate detection!
                    points.add(new Point(x, y));

                    // count max
                    if(x > maxXY[0])
                        maxXY[0] = x;
                    if(y > maxXY[1])
                        maxXY[1] = y;

                    // handy debug output
                    /* System.out.println(i + "," + j +" => " + x + "," + y +" => "+ view[i][j]); */
                }

    }

    private void renderPoints(int botX, int botY){
        // Construct an array of booleans.
        // This method should be o(n), rather than o(n^2) of the naive way
        boolean[][] map = new boolean[maxXY[0]+1][maxXY[1]+1];
        for(int i=0;i<map.length;i++)
            for(int j=0;j<map[i].length;j++)
                map[i][j] = false;

        // Now add all the points to the map
        for( Point p : points)
            map[p.x][p.y] = true;

        // ------------------------------------------
        // Now render map

        // Header
        System.out.print("+");
        for(int i=0;i<map.length;i++){ System.out.print("-"); }
        System.out.print("+\n");

        // loop over map
        for(int i=(map[0].length-1);i>=0;i--){
            System.out.print("|");
            for(int j=0;j<map.length;j++){

                // output wall, bot, space
                if(map[j][i])
                    System.out.print("#");
                else if(i == botY && j == botX)
                    System.out.print("*");
                else
                    System.out.print(".");
                
            }
            System.out.print("|\n");
        }

        // Footer
        System.out.print("+");
        for(int i=0;i<map.length;i++){ System.out.print("-"); }
        System.out.print("+\n");
    }
    


}
