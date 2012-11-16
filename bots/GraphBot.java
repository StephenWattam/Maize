package bots;
import maize.*;
import java.io.Serializable;

// For text status output 
import java.text.DecimalFormat;

// for full modelling
import java.awt.Point;
import java.util.*;
import java.util.HashSet;
import java.util.Vector;
import java.util.HashMap;
import java.awt.Dimension;

/** Semi-advanced bot that learns of its surroundings, and uses a guided depth-first search algorithm to find a fairly efficient route to the finish.  It does this by constructing a graph from the tile system the maze uses, and then spidering that, sticking as close to the finish as it can.
 *
 * It's also cable of very verbose debug output
 *
 * @author Stephen Wattam <stephenwattam@gmail.com>
 */
public class GraphBot implements Bot, Serializable {


    // ------------------------------------------------------------
    //  Configuration
    //
    // Turn debug on/off
    private static final boolean DEBUG = true;

    // ------------------------------------------------------------
    //  Don't edit below this line
    //  (unless you know what you're doing)


    // Constants to define map values
    private static final char UNKNOWN    = '·';
    private static final char SPACE      = ' ';
    private static final char WALL       = '#';
    private static final char FINISH     = 'F';



    // Contains instructions
    private Vector<Integer> buffer      = new Vector<Integer>();
    // Holds the best route.
    private Vector<Point> bestRoute     = null;

    // Make a random move.
    private int daveMode(){
        return 0;// (int)(Math.random() * 4);
    }

    /** Implementation of the Bot interface. */
    @Override
    public String getName(){
        return "Graph Bot";
    }

    /** Implementation of the Bot interface. */
    @Override
    public String getDescription(){
        return "Builds a map of its surroundings, and uses a targeted depth-first graph traversal algorithm to find a route.  Remembered " + points.size() + " walls.";
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
        debugln("==================================================");

        // Memorise points and plot experience
        debugln("Building world model...");
        int newPoints   = rememberPoints(Orientation.rotateToNorth(view, o), x, y, fx, fy);
        char[][] map = constructMap();
 
        // If we have new data, force the bot to compute a new route
        if(newPoints > 0 && !validateRoute(map, bestRoute))
            bestRoute = null;

        // Find the best route only when we run out of instructions from the previous one
        // For this to work, queueRoute() should queue up ONE UNIT'S MOTION ONLY (not including turning)!
        if(this.buffer.size() == 0){

            // If the route is null, or we have run out of data
            // then force a recompute
            if(bestRoute == null ||  bestRoute.size() < 2){// || !bestRoute.get(0).equals(new Point(x, y))){
                debugln("Routing...");
                bestRoute = findRoute(map, x, y, fx, fy);
            }

            // Take some data off the route, and add it to the buffer
            queueRoute(bestRoute, o);
            //buffer.add( Direction.FORWARD );
        }

        // Display information about the current view,
        // render full map
        debugln("Rendering...");
        displayView(view, bestRoute, x, y, o, fx, fy);
        renderMap(map, bestRoute, x, y, fx, fy, o);
        debugln("==================================================");

        // Then follow anything in the buffer
        return (int)this.buffer.remove(0);
    }

    @Override
    public void start(){
    }

    /* ================================================================================== */
    //  Subsystems                                                                   
    /* ================================================================================== */


    /* ------------------------------------------------------ */
    //  Route following
    /* ------------------------------------------------------ */
    // These methods presume:
    //  a) There is a valid route in this.bestRoute (or passed to the method)
    //  b) A vector with movement instructions exists in this.buffer,
    //     and it is valid to add to it.
    //

    // Queues instructions up by reading the route and current orientation,
    // then pushing things onto this.buffer
    public void queueRoute(Vector<Point> route, int o){
        if(route == null){
            System.err.println("Warning, null route!");
            System.err.println("Entering Dave mode...");
            buffer.add( daveMode() );
            return;
        }

        // Compute the orientation we WANT, from our position and that of
        // the first route item.
        Point current   = route.get(0);
        Point next      = route.get(1);

        // Check we have points
        if(current == null || next == null){
            System.err.println("Route doesn't have more than one point.");
            System.err.println("This presumably means I'm already at the finish");
            System.err.println("Entering Dave mode...");
            buffer.add( daveMode() );
            return;
        }
       
        // Compare the two coordinates
        int dx = next.x - current.x;
        int dy = next.y - current.y;

        // Check we're not being told to do the impossible
        if(dx != 0 && dy != 0){
            System.err.println("The route is telling me to go diagonally.");
            System.err.println("This is probably a massive ugly bug.");
            System.err.println("Entering Dave mode...");
            buffer.add( daveMode() );
            return;
        }


        // Decide which way to move, and move.
        if(dx == 0 && dy > 0){
            /* debugln("BACK"); */
            rotateToMatch(o, Orientation.SOUTH);
        }else if(dx == 0 && dy < 0){
            /* debugln("FORTH"); */
            rotateToMatch(o, Orientation.NORTH);
        }else if(dy == 0 && dx < 0){
            /* debugln("LEFT"); */
            rotateToMatch(o, Orientation.WEST);
        }else{ //if(dy == 0 && dx < 1)
            /* debugln("RIGHT"); */
            rotateToMatch(o, Orientation.EAST);
        }


        // Then remove the first item in the route
        route.remove(0);

    }

    // Rotates the bot to the desired orientation by adding to the instruction buffer
    //
    // This is the version from AbsoluteBot, by Ben.  My version is below.
    private void rotateToMatch(int current, int desired){
        switch(current)
        {
            case Orientation.NORTH:
                switch(desired)
                {
                    case Orientation.NORTH:
                        this.buffer.add(Direction.FORWARD);

                        break;

                    case Orientation.SOUTH:
                        this.buffer.add( Direction.BACK );

                        break;

                    case Orientation.WEST:
                        this.buffer.add( Direction.LEFT );
                        this.buffer.add( Direction.FORWARD );

                        break;

                    case Orientation.EAST:
                        this.buffer.add( Direction.RIGHT );
                        this.buffer.add( Direction.FORWARD );

                        break;
                }

                break;

            case Orientation.SOUTH:
                switch(desired)
                {
                    case Orientation.NORTH:
                        this.buffer.add( Direction.BACK );

                        break;

                    case Orientation.SOUTH:
                        this.buffer.add( Direction.FORWARD );

                        break;

                    case Orientation.WEST:
                        this.buffer.add( Direction.RIGHT );
                        this.buffer.add( Direction.FORWARD );

                        break;

                    case Orientation.EAST:
                        this.buffer.add( Direction.LEFT );
                        this.buffer.add( Direction.FORWARD );

                        break;
                }

                break;

            case Orientation.WEST:
                switch(desired)
                {
                    case Orientation.NORTH:
                        this.buffer.add( Direction.RIGHT );
                        this.buffer.add( Direction.FORWARD );

                        break;

                    case Orientation.SOUTH:
                        this.buffer.add( Direction.LEFT );
                        this.buffer.add( Direction.FORWARD );

                        break;

                    case Orientation.WEST:
                        this.buffer.add( Direction.FORWARD );

                        break;

                    case Orientation.EAST:
                        this.buffer.add( Direction.BACK );

                        break;
                }

                break;

            case Orientation.EAST:
                switch(desired)
                {
                    case Orientation.NORTH:
                        this.buffer.add( Direction.LEFT );
                        this.buffer.add( Direction.FORWARD );

                        break;

                    case Orientation.SOUTH:
                        this.buffer.add( Direction.RIGHT );
                        this.buffer.add( Direction.FORWARD );

                        break;

                    case Orientation.WEST:
                        this.buffer.add( Direction.BACK );

                        break;

                    case Orientation.EAST:
                        this.buffer.add( Direction.FORWARD );

                        break;
                }
                break;
        }

    }


    /* ------------------------------------------------------ */
    //  Text output
    /* ------------------------------------------------------ */
    /** Outputs status on Bots */
    private void displayView(boolean[][]view, Vector<Point> route, int x, int y, int o, int fx, int fy){
        debugln(" Status");
        debugln("  Position: " + x + ", " + y);
        debugln("  Finish: " + fx+ ", " + fy );
        debugln("  Distance: " + (Math.abs(fx-x) + Math.abs(fy-y)) + " manhattan, " + new DecimalFormat("#.00").format(Math.sqrt(Math.pow(fx-x, 2) + Math.pow(fy-y,2))) +" euclidean");
        if(route != null)
            debugln("  Route Length: " + route.size());
        debugln("  Orientation: " + Orientation.getName(o));
        debugln("  Context (default: Forward is up):");
        printContext(view);
        debugln("  Context (adjusted: North is up):");
        printContext(Orientation.rotateToNorth(view, o));

    }

    /** Prints a 3x3 context matrix. */
    private static void printContext(boolean[][] view){
        debugln("     +---+");
        for(int i=0;i<3;i++){
            debug("     |");
            for(int j=0;j<3;j++){
                debug( "" + viewChar(view, j, i) );
            }
            debug( "|\n" );
        }
        debugln("     +---+");
    }

    /** Returns the symbols to use when printing context */
    private static char viewChar(boolean[][] view, int i, int j){
        char c = ' ';
        if(view[i][j])
            c = '#';
        return c;
    }

    /* ------------------------------------------------------ */
    //  Memorising system
    /* ------------------------------------------------------ */

    // Keep track of max in X, Y so we don't have to loop over
    // the points.
    // NB: these values are always one too large, to allow a gutter where
    //     the route can go around the outside of a maze which has
    //     only been partially learned.
    private int[] maxXY = {0,0};
    // Keep a list of points we have seen.
    private HashMap<Point, Character> points = new HashMap<Point, Character>();

    // Record the points in the view at a given X, Y.
    // Presumes the view is already the correct way up
    private int rememberPoints(boolean[][] view, int xBot, int yBot, int fx, int fy){
        // keep track of 'real' x-y, not relative to bot
        int x = 0;
        int y = 0;

        // count new points
        int newPoints = 0;

        // Loop through, adding absolute points
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++){
                x = xBot + (i-1);
                y = yBot + (j-1);

                // see if we already have recorded this
                if(!points.containsKey(new Point(x, y)))
                    newPoints ++;

                // Record the adjusted (real) x-y coordinates.
                if(view[i][j]){
                    points.put(new Point(x, y), WALL);
                }else{
                    points.put(new Point(x, y), SPACE);
                }

                // count max
                if(x+1 > maxXY[0])
                    maxXY[0] = x+1;
                if(y+1 > maxXY[1])
                    maxXY[1] = y+1;

                // handy debug output
                /* debugln(i + "," + j +" => " + x + "," + y +" => "+ view[i][j]); */
            }
               

        // Add the finish
        if(fx+1 > maxXY[0])
            maxXY[0] = fx+1;
        if(fy+1 > maxXY[1])
            maxXY[1] = fy+1;


        return newPoints;
    }

    // Construct a char[][] map out of the set of points we have memorised.
    //
    // This basically translates the points into a more usable format, 
    // but is mainly for ease of processing later
    private char[][] constructMap(){
        // This method should be o(n), rather than o(n^2) of the naive way
        char[][] map = new char[maxXY[0]+1][maxXY[1]+1];
        for(int i=0;i<map.length;i++)
            for(int j=0;j<map[i].length;j++)
                map[i][j] = UNKNOWN;

        // Now add all the points to the map
        Point p = null;
        for( Map.Entry<Point, Character> e : points.entrySet()){
            p = e.getKey();
            map[p.x][p.y] = e.getValue();
        }

        return map;
    }

    // Render the memory to screen.
    //
    // This renders somewhat upside-down, so that 0,0 is in the bottom left, just like the
    // UI display in Maize
    private void renderMap(char[][] map, Vector<Point> route, int botX, int botY, int fx, int fy, int o){
        // Header
        debug("+");
        for(int i=0;i<map.length;i++){ debug("-"); }
        debug("+\n");

        // loop over map 
        // (note order is inverted to display map in |Y|-y layout)
        for(int i=0;i<map[0].length;i++){
            debug("|");
            for(int j=0;j<map.length;j++){

                // Fix terminal rendering order
                int x = j;
                int y = i;

                // output wall, bot, space
                if(y == botY && x == botX){
                    switch(o){
                        case Orientation.NORTH:
                            debug("^");
                            break;
                        case Orientation.EAST:
                            debug(">");
                            break;
                        case Orientation.SOUTH:
                            debug("v");
                            break;
                        case Orientation.WEST:
                            debug("<");
                            break;
                    }
                }
                else if(route != null && route.contains(new Point(x, y)))
                    debug("+");
                else
                    debug("" + map[x][y]);
                
            }
            debug("|\n");
        }

        // Footer
        debug("+");
        for(int i=0;i<map.length;i++){ debug("-"); }
        debug("+\n");
    }
    


    /* ------------------------------------------------------ */
    //  Routing system
    /* ------------------------------------------------------ */

    // Find a route from the x, y point to the fx, fy point, using
    // the map provided.
    private Vector<Point> findRoute(char[][] map, int x, int y, int fx, int fy){

        // Then create a vector for the routing graph.
        HashMap<Point, Vector<Point>> nodes = new HashMap<Point, Vector<Point>>();


        // Construct a tree by listing the adjacency for each square
        /* debugln("Constructing tree..."); */
        /* for(int i=0;i<map.length;i++) */
        /*     for(int j=0;j<map[i].length;j++){ */
        /*         Vector<Point> adjacent = createLocalRoutes(map, i, j); */
        /*         if(adjacent.size() > 0) */
        /*             nodes.put( new Point(i, j), adjacent ); */
        /*     } */
    

        // Search!
        Vector<Point> route = breadthFirstSearch( map, new Point(x, y), new Point(fx, fy), new Vector<Point>() );
     
        if(route == null){
            debugln("**** NO ROUTE TO FINISH!");
            debugln("     Perhaps I'm being run on two mazes!");
        }else{
            for(Point p: route)
                debug("(" + p.x + "," + p.y + ")");
            debugln("");
        }

        return route;
    }

   
    // Recursively seek the finish.
    //
    // Note: this does not actually perform a breadth first search,
    //       because it would be too computationall expensive.
    //       Instead it performs a directed depth-first search for the finish.
    private Vector<Point> breadthFirstSearch( 
                    char[][] map,
                    Point position,
                    Point finish,
                    Vector<Point> route){
        /* debug("->(Pos " + position.x + "," + position.y + ")"); */
        /* debug("(hist " + route.size() + ")\n"); */

        // then load a point
        Vector<Point> next = createLocalRoutes(map, position.x, position.y);
            /* tree.get(position); */

        // Return null if nowhere to go!
        if(next == null){
            /* debugln("<-[0]"); */
            return null;
        }

        /* debug("  (next " + next.size() + ")\n"); */
        // Copy the route reference to avoid messing up others' contexts
        // and then add our position and keep searching.
        route.add(position);


        // loop through the next ones
        for( Point p : next ){
            // Don't loop
            if(!route.contains(p)){
                // Check if we have completed the route
                if(p.equals(finish) || map[p.x][p.y] == UNKNOWN){
                    route.add(p);
                    /* debugln("<=[1]"); */
                    return route;
                }
            }
        }
      

        // Keep track of minimum
        Point minimum = null;
        Vector<Point> remaining = (Vector<Point>)next.clone();

        

        // Pick off the minimum each time
        // this is a poor man's sorting algorithm
        // XXX: This is O(n^2) worst case, and is only used because
        //      I cannot use an inline class to use Comparable.sort();
        //      Since the maximum number of items in next can be 4, this
        //      isn't a particular issue, but it'd be nice to fix it...
        while( remaining.size() > 0 ){
            // establish minimum
            for(Point p: remaining){
                /* For simple distance seeking:
                if(minimum == null || finish.distance(minimum) > finish.distance(p))
                    minimum = p;
                */
                /* And for more complex finish seeking/backtrack prevention:*/
                if(minimum == null || estimateRoutePenalty(minimum, position, finish) > estimateRoutePenalty(p, position, finish))
                    minimum = p;
            }
            
            // And then remove it from the list
            remaining.remove(minimum);

            // and then see if it's good.
            // Don't loop
            if(!route.contains(minimum)){
                // Check to see if the finish is downstream
                Vector<Point> result = breadthFirstSearch(map, minimum, finish, (Vector<Point>)route.clone());
                // If it is, return, else don't.
                if(result != null){
                    /* debugln("<=[2]"); */
                    return result;
                }
            }

            // Search from zero base again
            minimum = null;
        }

        /* debugln("<-[3]"); */
        return null;
    }


    // Compute a routing penalty based on:
    //  current position (current)
    //  position of next provisional point (p)
    //  finish position
    private double estimateRoutePenalty(Point p, Point current, Point finish){

        // Definitely go to the finish!
        if(p == finish)
            return Double.MIN_VALUE;

        // See if the point takes us further from, or closer to, the finish
        //
        // Varies between -1 and 1
        double deltaDistance  = p.distance(finish) - current.distance(finish);

        return deltaDistance;
    }   


    // Construct a list of points that are directly adjacent to x, y, and are not walls.
    // Since this is designed to map where the bot may move, it does not include diagonals.
    private Vector<Point> createLocalRoutes(char[][] map, int x, int y){
        Vector<Point> routes = new Vector<Point>();

        // Don't count walls, to keep the volume of data down!
        if(map[x][y] == WALL)
            return routes;

        addPointIfSpace(routes, map, x-1, y);
        addPointIfSpace(routes, map, x+1, y);
        addPointIfSpace(routes, map, x, y-1);
        addPointIfSpace(routes, map, x, y+1);

        // DEBUG output to list points.
        /* debug("" + x +"," + y + " : "); */
        /* for(Point p: routes){ */
        /*     debug("(" + p.x + "," + p.y + ") -> "); */
        /* } */
        /* debugln("["  + routes.size() + "]"); */

        return routes;
    }

    // Add a point to a route IF the point is a space, and IF it falls within bounds
    private void addPointIfSpace(Vector<Point> routes, char[][] map, int x, int y){
        // Check bounds
        if(x >= map.length || x < 0 || map.length == 0)
            return;
        if(y >= map[x].length || y < 0 || map[x].length == 0)
            return;

        // Check walls
        if(map[x][y] != WALL)
            routes.add( new Point(x, y) );
    }

    // Returns false if the route tries to go over walls
    // is used to check updates to the route
    private static boolean validateRoute( char[][] map, Vector<Point> route ){
        // Return false if no route
        if(route == null)
            return false;

        // Return false if the route hits a wall
        for(Point p: route)
            if(map[p.x][p.y] == WALL)
                return false;

        // Else it must be valid
        return true;
    }

    /* ------------------------------------------------------ */
    //  Debug/output system
    /* ------------------------------------------------------ */

    // Debug message (with newline) only if DEBUG set
    private static void debugln(final String str){
        debug(str + "\n");
    }

    // Debug message only if DEBUG set
    private static void debug(final String str){
        if(DEBUG)
            System.out.print(str);
    }
    
}
