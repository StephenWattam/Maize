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


public class DemoBot implements Bot, Serializable {


    // Contains instructions
    private Vector<Integer> buffer = new Vector<Integer>();

    // Holds the best route.
    private Vector<Point> bestRoute = new Vector<Point>();


    /** Sets up default bots */
    public DemoBot(){
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
      

        System.out.println("==================================================");
        System.out.println("==================================================");


        // Memorise points and plot experience
        System.out.println("Building world model...");
        int newPoints = rememberPoints(Orientation.rotateToNorth(view, o), x, y, fx, fy);
        boolean[][] map = constructMap();
 
        // If we have new data, force the bot to compute a new route
        if(newPoints > 0 && !validateRoute(map, bestRoute))
            bestRoute = null;

        // Find the best route only when we run out of instructions from the previous one
        // For this to work, queueRoute() should queue up ONE UNIT'S MOTION ONLY (not including turning)!
        if(this.buffer.size() == 0){

            // If the route is null, or we have run out of data
            // then force a recompute
            if(bestRoute == null ||  bestRoute.size() < 2){// || !bestRoute.get(0).equals(new Point(x, y))){
                System.out.println("Routing...");
                bestRoute = findRoute(map, x, y, fx, fy);
            }

            // Take some data off the route, and add it to the buffer
            queueRoute(bestRoute, o);
        }

        // Display information about the current view,
        // render full map
        System.out.println("Rendering...");
        displayView(view, bestRoute, x, y, o, fx, fy);
        renderMap(map, bestRoute, x, y, fx, fy);
        System.out.println("==================================================");

        // Then follow anything in the buffer
        return (int)this.buffer.remove(0);
    }

    // FIXME!
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
            /* System.out.println("BACK"); */
            rotateToMatch(o, Orientation.SOUTH);
        }else if(dx == 0 && dy < 0){
            /* System.out.println("FORTH"); */
            rotateToMatch(o, Orientation.NORTH);
        }else if(dy == 0 && dx < 0){
            /* System.out.println("LEFT"); */
            rotateToMatch(o, Orientation.WEST);
        }else{ //if(dy == 0 && dx < 1)
            /* System.out.println("RIGHT"); */
            rotateToMatch(o, Orientation.EAST);
        }


        // Then remove the first item in the route
        route.remove(0);

    }

    // Rotates the bot to the desired orientation by adding to the instruction buffer
    //
    // TODO: improve this so it is more efficient
    //       it currently has one case where it turns right three times instead of
    //       turning left.
    private void rotateToMatch(int current, int desired){
        // check for 270 degree left turns
        /* System.out.println(Orientation.getName(current) + "->" + Orientation.getName(desired) + ":" + Math.abs(current - desired)); */

        /* // special case to prevent weird 'turning three times right to go left' */
        if((current - desired) == 1){
        /*     /* System.out.println("\n####### LEFT HAND TURN"); */ 
            buffer.add(Direction.LEFT);
            buffer.add(Direction.FORWARD);
            return;
        }

        /* // 180 degrees, */
        /* // this basically means we can just tell the bot to  */
        /* // go backwards */
        if(Math.abs(current - desired) == 2){
            buffer.add( Direction.BACK );
            return;
        }
        
        // Else go right until we meet the point we want
        // then move forward
        while(current != desired){
            // Go right
            buffer.add(Direction.RIGHT);
            // Keep track
            current = ((current + 1) % 4);
        }
        buffer.add(Direction.FORWARD);


        return;
    }

    // Make a random move.
    private int daveMode(){
        return 0;// (int)(Math.random() * 4);
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
        return "Remembered " + points.size() + " points.";
    }

    /* ================================================================================== */
    /*  Private methods                                                                   */
    /* ================================================================================== */

    /* ------------------------------------------------------ */
    //  Text output
    /* ------------------------------------------------------ */
    /** Outputs status on Bots */
    private void displayView(boolean[][]view, Vector<Point> route, int x, int y, int o, int fx, int fy){
        System.out.println(" Status");
        System.out.println("  Position: " + x + ", " + y);
        System.out.println("  Finish: " + fx+ ", " + fy );
        System.out.println("  Distance: " + (Math.abs(fx-x) + Math.abs(fy-y)) + " manhattan, " + new DecimalFormat("#.00").format(Math.sqrt(Math.pow(fx-x, 2) + Math.pow(fy-y,2))) +" euclidean");
        if(route != null)
            System.out.println("  Route Length: " + route.size());
        System.out.println("  Orientation: " + Orientation.getName(o));
        System.out.println("  Context (default: Forward is up):");
        printContext(view);
        System.out.println("  Context (adjusted: North is up):");
        printContext(Orientation.rotateToNorth(view, o));

    }

    /** Prints a 3x3 context matrix. */
    private static void printContext(boolean[][] view){
        System.out.println("     +---+");
        for(int i=2;i>=0;i--){
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
        char c = ' ';
        if(view[i][j])
            c = '#';
        return c;
    }

    /* ------------------------------------------------------ */
    //  Memorising system
    /* ------------------------------------------------------ */


    // Keep track of max in X, Y
    private int[] maxXY = {0,0};
    // Keep a list of points we have seen.
    private HashSet<Point> points = new HashSet<Point>();


    // Record the points in the view at a given X, Y.
    // Presumes the view is already the correct way up
    private int rememberPoints(boolean[][] view, int xBot, int yBot, int fx, int fy){
        int x = 0;
        int y = 0;

        // count new points
        int newPoints = 0;


        // Loop through, adding absolute points
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                if(view[i][j]){
                    x = xBot + (i-1);
                    y = yBot + (j-1);

                    // see if we already have recorded this
                    if(!points.contains(new Point(x, y)))
                        newPoints ++;

                    // Record the adjusted (real) x-y coordinates.
                    points.add(new Point(x, y));

                    // count max
                    if(x+1 > maxXY[0])
                        maxXY[0] = x+1;
                    if(y+1 > maxXY[1])
                        maxXY[1] = y+1;

                    // handy debug output
                    /* System.out.println(i + "," + j +" => " + x + "," + y +" => "+ view[i][j]); */
                }



        // ------- Add the finish
        // count max
        if(fx+1 > maxXY[0])
            maxXY[0] = fx+1;
        if(fy+1 > maxXY[1])
            maxXY[1] = fy+1;


        return newPoints;

    }

    // Construct a boolean[][] map out of the set of points we have memorised.
    //
    // This basically translates the points into a more usable format, 
    // but is mainly for ease of processing later
    private boolean[][] constructMap(){
        // ------------------------------------------
        // Construct a map of booleans.
        
        // This method should be o(n), rather than o(n^2) of the naive way
        boolean[][] map = new boolean[maxXY[0]+1][maxXY[1]+1];
        for(int i=0;i<map.length;i++)
            for(int j=0;j<map[i].length;j++)
                map[i][j] = false;

        // Now add all the points to the map
        for( Point p : points)
            map[p.x][p.y] = true;

        return map;
    }

    // Render the memory to screen.
    //
    // This renders somewhat upside-down, so that 0,0 is in the bottom left, just like the
    // UI display in Maize
    private void renderMap(boolean[][] map, Vector<Point> route, int botX, int botY, int fx, int fy){

        // ------------------------------------------
        // Now render map

        // Header
        System.out.print("+");
        for(int i=0;i<map.length;i++){ System.out.print("-"); }
        System.out.print("+\n");

        // loop over map 
        // (note order is inverted to display map in |Y|-y layout)
        for(int i=(map[0].length-1);i>=0;i--){
            System.out.print("|");
            for(int j=0;j<map.length;j++){

                // output wall, bot, space
                if(i == botY && j == botX)
                    System.out.print("*");
                else if(i == fx && j == fy)
                    System.out.print("F");
                else if(route != null && route.contains(new Point(j, i)))
                    System.out.print(".");
                else if(map[j][i])
                    System.out.print("#");
                else
                    System.out.print(" ");
                
            }
            System.out.print("|\n");
        }

        // Footer
        System.out.print("+");
        for(int i=0;i<map.length;i++){ System.out.print("-"); }
        System.out.print("+\n");
    }
    


    // 
    private Vector<Point> findRoute(boolean[][] map, int x, int y, int fx, int fy){
        HashMap<Point, Vector<Point>> nodes = new HashMap<Point, Vector<Point>>();


        // Construct a tree by listing the adjacency for each square
        /* System.out.println("Constructing tree..."); */
        /* for(int i=0;i<map.length;i++) */
        /*     for(int j=0;j<map[i].length;j++){ */
        /*         Vector<Point> adjacent = createLocalRoutes(map, i, j); */
        /*         if(adjacent.size() > 0) */
        /*             nodes.put( new Point(i, j), adjacent ); */
        /*     } */
    

        // Search!
        Vector<Point> route = breadthFirstSearch( map, new Point(x, y), new Point(fx, fy), new Vector<Point>() );
     
        if(route == null){
            System.out.println("**** NO ROUTE TO FINISH!");
            System.out.println("     Perhaps I'm being run on two mazes!");
        }else{
            for(Point p: route)
                System.out.print("(" + p.x + "," + p.y + ")");
            System.out.println("");
        }

        return route;
    }

   
    // Recursively seek the finish.
    //
    // Note: this does not actually perform a breadth first search,
    //       because it would be too computationall expensive.
    //       Instead it performs a directed depth-first search for the finish.
    private Vector<Point> breadthFirstSearch( 
                    boolean[][] map,
                    Point position,
                    Point finish,
                    Vector<Point> route){
        /* System.out.print("->(Pos " + position.x + "," + position.y + ")"); */
        /* System.out.print("(hist " + route.size() + ")\n"); */

        // then load a point
        Vector<Point> next = createLocalRoutes(map, position.x, position.y);
            /* tree.get(position); */

        // Return null if nowhere to go!
        if(next == null){
            /* System.out.println("<-[0]"); */
            return null;
        }

        /* System.out.print("  (next " + next.size() + ")\n"); */
        // Copy the route reference to avoid messing up others' contexts
        // and then add our position and keep searching.
        route.add(position);


        // loop through the next ones
        for( Point p : next ){
            // Don't loop
            if(!route.contains(p)){
                // Check if we have completed the route
                if(p.equals(finish)){
                    route.add(p);
                    /* System.out.println("<=[1]"); */
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
            for(Point p: remaining)
                if(minimum == null || finish.distance(minimum) > finish.distance(p))
                    minimum = p;
            
            // And then remove it from the list
            remaining.remove(minimum);

            // and then see if it's good.
            // Don't loop
            if(!route.contains(minimum)){
                // Check to see if the finish is downstream
                Vector<Point> result = breadthFirstSearch(map, minimum, finish, (Vector<Point>)route.clone());
                // If it is, return, else don't.
                if(result != null){
                    /* System.out.println("<=[2]"); */
                    return result;
                }
            }

            // Search from zero base again
            minimum = null;
        }

        /* System.out.println("<-[3]"); */
        return null;
    }


    // Construct a list of points that are directly adjacent to x, y, and are not walls.
    // Since this is designed to map where the bot may move, it does not include diagonals.
    private Vector<Point> createLocalRoutes(boolean[][] map, int x, int y){
        Vector<Point> routes = new Vector<Point>();

        // Don't count walls, to keep the volume of data down!
        if(map[x][y])
            return routes;

        addPointIfSpace(routes, map, x-1, y);
        addPointIfSpace(routes, map, x+1, y);
        addPointIfSpace(routes, map, x, y-1);
        addPointIfSpace(routes, map, x, y+1);

        // DEBUG output to list points.
        /* System.out.print("" + x +"," + y + " : "); */
        /* for(Point p: routes){ */
        /*     System.out.print("(" + p.x + "," + p.y + ") -> "); */
        /* } */
        /* System.out.println("["  + routes.size() + "]"); */

        return routes;
    }

    // Add a point to a route IF the point is a space, and IF it falls within bounds
    private void addPointIfSpace(Vector<Point> routes, boolean[][] map, int x, int y){
        // Check bounds
        if(x >= map.length || x < 0 || map.length == 0)
            return;
        if(y >= map[x].length || y < 0 || map[x].length == 0)
            return;

        // Check walls
        if(!map[x][y])
            routes.add( new Point(x, y) );
    }

    // Returns false if the route tries to go over walls
    // is used to check updates to the route
    private boolean validateRoute( boolean[][] map, Vector<Point> route ){
        // Return false if no route
        if(route == null)
            return false;

        // Return false if the route hits a wall
        for(Point p: route)
            if(map[p.x][p.y])
                return false;

        // Else it must be valid
        return true;
    }

}
