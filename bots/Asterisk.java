package bots;
import maize.*;
import java.io.Serializable;


import java.util.Vector;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.awt.Point;


/** Bot using A* to route.
 *
 * @author Stephen Wattam
 */
public class Asterisk extends AbsoluteBot implements Serializable {

    /** Should the shadow maze cache be cleared on each run? */
    private static final boolean CLEAR_MEMORY_ON_START = false;

    private Vector<Point> route = null;
    private Shadow shadow = new Shadow();

    @Override
    public int calculateMove(boolean[][] view, int x, int y, int o, int fx, int fy){


        // Add our view to the internal model.
        addToShadow(view, new Point(x - 1, y - 1));
        
        // Show the shadow
        /* shadow.render(); */

        // Compute a new route if we've run out of points
        if(route == null || route.size() == 0)
            route = calculateRoute(new Point(x, y), new Point(fx, fy));

        // Get a move from the route
        if(route == null){
            // No valid route
            
            // Clear the shadow cache
            // in case we've been run on two mazes
            clearShadow();

            return getRandomMove();
        }

        // Get from route, or handle any edge cases by computing a new route.
        return getMoveFromRoute(new Point(x, y), new Point(fx, fy));
    }

    /** Implementation of the Bot interface.
     *
     * @return           Bot name.
     */
    @Override
    public String getName(){
        return "*";
    }

    /** Implementation of the Bot interface.
     *
     * @return           Bot Description.
     */
    @Override
    public String getDescription(){
        return "Map builder, using A* to route.  Mapped " + shadow.getWalls().size() + " wall[s].";
    }

    @Override
    public void start(){
        if(CLEAR_MEMORY_ON_START)
            clearShadow();
    }

    /** Clear the memory of the surroundings.
     *
     * Used when the maze is thought to have changed.
     */
    private void clearShadow(){
        this.shadow = new Shadow();
    }

    /** Returns a random direction.
     */
    private int getRandomMove(){
        return (int)(Math.random() * 4);
    }

    /** Returns a direction chosen to match the route.
     */
    private int getMoveFromRoute(Point current, Point finish){

        // If we are next to the finish, go there!
        if(isAdjacent(current, finish))
            return computeDirectionFromPoints(current, finish);
            
        // Read next item from route
        Point next = route.remove(0);

        // If next is a wall, recalculate the route
        if(shadow.getPoint(next)){
            this.route = calculateRoute(current, finish);
            return getMoveFromRoute(current, finish);
        }

        // Move towards point from route
        return computeDirectionFromPoints(current, next);
    }

    /** Returns a direction (north-normalised) to move in order to hit next from current.
     *
     *  Returns a random value if the points are equal.
     *
     * @param current The current location
     * @param next The target location.  Presumed adjacent.
     * @return A value from maize.Orientation
     */
    private int computeDirectionFromPoints(Point current, Point next){

        // Move 'towards' next item
        if(next.x < current.x)
            return Orientation.WEST;
        if(next.y < current.y)
            return Orientation.NORTH;
        if(next.x > current.x)
            return Orientation.EAST;
        if(next.y > current.y)
            return Orientation.SOUTH;

        return getRandomMove();
    }

    /** Returns true if two points are adjacent.
     *
     * Uses manhattan distance to take into account bot motion.
     */
    private boolean isAdjacent(Point a, Point b){
        return ((Math.abs(a.x - b.x) + Math.abs(a.y - b.y)) == 1);
    }


    /** Update the shadow with the current context.
     *
     * Assumes context is 3x3.
     *
     * @param context As seen facing north (rotate prior to use if not using AbsoluteBot)
     * @param topLeft The point of the top-left of the context grid.
     */
    private void addToShadow(boolean[][] context, Point topLeft){
        for(int j=0; j<3; j++){
            for(int i=0; i<3; i++){

                Point p = new Point(topLeft.x + i, topLeft.y + j);

                if(context[i][j]){
                    shadow.addPoint(p);
                }else if(shadow.getPoint(p)){
                    // Here we have seen a space where previously there was none.
                    // This indicates we've been run on two mazes at once, so clear the
                    // shadow
                    clearShadow();
                }

            }
        }
    }

    /** Use a customised MazeSolver to compute a route to the finish using A*.
     *
     * @param from The point to route from. i.e. the bot location
     * @param to The target, i.e. the finish.
     * @return An in-order list of points representing the route, or null if no route was found
     */
    private Vector<Point> calculateRoute(Point from, Point to){

        MazeSolver solver = new MazeSolver( shadow.getWalls(), from, to );
        Vector<Point> newRoute = solver.solve();

        /* if(newRoute != null) */
        /*     System.out.println("New route length: " + newRoute.size()); */

        return newRoute;
    }


    /** Represents the bot's view of the maze.
     *
     * A more rudimentary (and efficient) form of MazeSolver$ShadowMaze.
     */
    class Shadow{
        
        private Vector<Point> points = new Vector<Point>();

        private int width = 0;
        private int height = 0;

        /** Set a given point to true.
         */
        public void addPoint(Point p){
            if(p.x >= width)
                width = p.x + 2;
            if(p.y >= height)
                height = p.y + 2;

            points.add(p);
        }

        /** Return true if a point is set, or false otherwise.
         */
        public boolean getPoint(Point p){
            for(Point p2: points)
                if(p.equals(p2))
                    return true;

            return false;
        }

        /** Returns the list of known walls.
         */
        public Vector<Point> getWalls(){
            return points;
        }

        /** Render the Shadow maze to the terminal.
         */
        public void render(){
            for(int i=0; i<width; i++)
                System.out.print("-");
            System.out.print("\n");

            for(int j=0; j<height; j++){
                for(int i=0; i<width; i++){
                    char c = ' ';

                    if(getPoint(new Point(i, j)))
                        c = '#';

                    System.out.print("" + c);
                }
                System.out.print("\n");
            }
            
            for(int i=0; i<width; i++)
                System.out.print("-");
            System.out.print("\n");
        }

    }



    /** Solves a given Maze using the A* routing algorithm.
     *
     * Note that, though it behaves similarly to a bot (no diagonal motion), it does not 
     * take into account the time needed to rotate a bot and returns a list of points
     * for its route.
     *
     * Due to the properties of A*, this class will produce the shortest path
     * (if one exists).
     *
     * @author Stephen Wattam
     */
    public class MazeSolver{

        private Point start;
        private Point end;

        /** The maze routing model.
         * Contains the maze data, but is mutable and can be annotated
         * with route data.
         */
        private ShadowMaze shadow;

        /** Holds the route after calculation. */
        private Vector<Point> route;

        /** Create a new MazeSolver for a given maze. */
        public MazeSolver(Vector<Point> walls, Point start, Point end){

            // Save the start, end points
            this.start = start;
            this.end = end;

            // Compute the width, height of the shadow plane
            int maxWidth = Math.max(start.x, end.x);
            int maxHeight = Math.max(start.y, end.y);
            for(Point p: walls){
                if(p.x > maxWidth)
                    maxWidth = p.x;
                if(p.y > maxHeight)
                    maxHeight = p.y;
            }

            // Construct new shadow
            // Add one onto the size so that any points in the max size are shown
            // Don't forget arrays start from 0 but Point values don't (well, maths doesn't...)
            //
            // Then add a second one to create an "unknown" gutter to route around
            this.shadow = new ShadowMaze(walls, maxWidth + 1 + 1, maxHeight + 1 + 1);

            // Ensure the routing algorithm can actually touch start/end
            shadow.setPoint(start, false);
            shadow.setPoint(end, false);
        }

       
        /** Solve the route and return it.
         *
         * @return The route from start to finish, or null if no route could be found.
         */
        public Vector<Point> solve(){

            // DEBUG
            /* shadow.renderToTerminal( start, end ); */

            route = aStar( start, end );

            // DEBUG 
            /* shadow.renderToTerminal( start, end ); */

            return route;
        }


        /** Returns a computed route, or null if not yet calculated.
         *
         * @return The object's currently computed route, if one exists, or null otherwise.
         */
        public Vector<Point> getRoute(){
            return route;
        }



        /** Route using A* algorithm from start to goal.  Uses 
         * the shadow model for all state management.
         *
         * @param start The point where routing should start, i.e. route from
         * @param goal The point where routing will end successfully
         * @return An in-order Vector of Points, or null if no route was found.
         */
        private Vector<Point> aStar(Point start, Point goal){
            /* System.out.println("Start: " + start.x + "," + start.y + "  Finish: " + goal.x + "," + goal.y); */

            HashSet<Point> fringe = new HashSet<Point>();
            fringe.add(start);

            while( fringe.size() > 0 ){
                
                Point current = findLowestHeuristic(fringe, start, goal);
                fringe.remove(current);            // Accept it 
                shadow.setPoint(current, true);   // Keep track of no-go areas with the shadow model
               
                /* System.out.println("Current: " + current.x + "," + current.y); */


                // TODO: Check if current == the end and return the route if so
                if(current.equals(goal)){
                    /* System.out.println("Found route!"); */
                    return backtraceRoute(current, start);
                }

                
                Set<Point> nextMoves = shadow.getValidMoves(current);  // Get a list of valid moves
                for(Point p: nextMoves){     // Set routing info for backtrackingi
                    if(fringe.contains(p)){

                        // If it's in the fringe, check to see if our route is better (from start to parent)
                        // than the current route.  If so, set ourselves as the back route instead.
                        if( manhattanDistance(start, current) < 
                            manhattanDistance(start, shadow.getRoutePointer(p)) ){
                            shadow.setRoutePointer(p, current);
                        }
                    }else{
                        // Not in fringe.  Set a backroute and add to fringe
                        shadow.setRoutePointer(p, current);
                        /* System.out.println("Considering: " + p.x + "," + p.y); */
                        fringe.add(p);
                    }
                }


            }

            /* System.out.println("Not found route."); */
            return null;
        }


        /** Trace back from the goal (end) to the start (start).
         *
         * @param end The final point in the route, i.e. the goal
         * @param start The starting point in the route
         * @return A list of points representing the route, in forwards order (from start to end)
         */
        private Vector<Point> backtraceRoute(Point end, Point start){
            Vector<Point> reverseRoute = new Vector<Point>();

            Point next = shadow.getRoutePointer(end);
            while(!(next.equals(start))){

                // add to the listing
                reverseRoute.add(next);

                // Move to next parent pointer
                next = shadow.getRoutePointer(next);
            }

            // Reverse the list of points, then send back
            Collections.reverse(reverseRoute);

            // Return the route
            return reverseRoute;    // Now not actually in reverse, but hey ho...
        }


        /** Return the point from a set with lowest distance heuristic between from and to.
         *
         * @param set A set of points to compare against the 'to' point.
         * @param from A point to route from
         * @param to A single point to route to
         * @return The point from the set with the lowest Manhattan distance
         */
        private Point findLowestHeuristic(Collection<Point> set, Point from, Point to){
            if(set.size() == 0)
                return null;

            // Start off with the first point
            Point bestPoint = null;
            double bestDistance = 0;

            // Iterate over them
            for(Point p: set){
                double distance = computeHeuristic(from, to, p);
                if(bestPoint == null || distance < bestDistance){
                    bestPoint = p;
                    bestDistance = distance;
                }
            }

            return bestPoint;
        }

       
        /** Compute the A* distance heuristic.
         *
         * @param from The start point.
         * @param to The end point
         * @param p The point to score.
         */
        private double computeHeuristic(Point from, Point to, Point p){
            double distanceTo = manhattanDistance(p, to);
            double distanceFrom = shadow.getRouteLength(p);

            return distanceFrom + distanceTo;
        }


        /** Compute manhattan distance between two points.  Commutative.
         *
         * @param a One of the points
         * @param b The other one of the points.
         * @return The Manhattan distance between each point.
         */
        private double manhattanDistance(Point a, Point b){
            return Math.abs(b.x - a.x) + Math.abs(b.y - a.y);
        }

        /* --------------------------------------------------------------------- */

        /** Represents the state of the maze whilst being routed.  Extra walls
         * are produced to simulate the "closed" set, and routing data is added
         * in the form of a series of backwards pointers.
         */
        private class ShadowMaze{

            /** Width of maze.  Immutable. */
            private int width;
            /** Height of maze.  Immutable. */
            private int height;


            /** Store a grid of walls/movable places */
            private boolean[] grid; // True for a wall (i.e. unreachable square), False for a space

            /** Store a list of backreferences to determine a route later. */
            private Point[] route;   // Backreferences for tracing route


            public ShadowMaze(Vector<Point> walls, int width, int height){

                // Width and height
                this.width = width;
                this.height = height;

                // Store state
                grid            = new boolean[ width * height ];
                route           = new Point[ width * height ];

                // Construct new boolean array and set to false
                for(int i=0; i<width; i++){
                    for(int j=0; j<height; j++){
                        setPoint(i, j, false);
                   }
                }

                // Set the walls to true
                for(Point p: walls)
                    setPoint(p, true);
                
            }


            /** Render debug to terminal, including the route.
             *
             * @param start The start point of the maze.  Shows up as 'S'
             * @param end The finish point of the maze.  Shows up as 'F'
             */
            public void renderToTerminal(Point start, Point end){

                for(int i=0; i<width+2; i++)
                    System.out.print("-");
                System.out.print("\n");

                for(int j=0; j<height; j++){
                    System.out.print("|");
                    for(int i=0; i<width; i++){
                        char c = ' ';


                        if(getPoint(i, j))
                            c = '#';
     
                        // Plot route
                        Point route = getRoutePointer(i, j);
                        if(route != null){
                            if(route.equals(new Point(i - 1, j)))
                                c = '>';
                            if(route.equals(new Point(i + 1, j)))
                                c = '<';
                            if(route.equals(new Point(i, j - 1)))
                                c = 'v';
                            if(route.equals(new Point(i, j + 1)))
                                c = '^';
                        }

                        if(new Point(i, j).equals(start))
                            c = 'S';
                        if(new Point(i, j).equals(end))
                            c = 'F';
                       
                        System.out.print("" + c);
                    }
                    System.out.print("|\n");
                }


                for(int i=0; i<width+2; i++)
                    System.out.print("-");
                System.out.print("\n");
            }


            /* -- */


            /** Returns a set of points that are movable to by a bot,
             * but are not set to true.
             *
             * @param x The x co-ordinate
             * @param y The y co-ordinate
             * @return A set containing points that may be moved to (any that are
             *         non-diagonally adjacent and also set to false).
             */
            public Set<Point> getValidMoves(int x, int y){
                HashSet<Point> set = new HashSet<Point>();

                // Bots cannot move diagonally.
                if(!getPoint(x - 1, y))
                    set.add(new Point( x - 1, y ));
                if(!getPoint(x + 1, y))
                    set.add(new Point( x + 1, y ));
                if(!getPoint(x, y - 1))
                    set.add(new Point( x, y - 1 ));
                if(!getPoint(x, y + 1))
                    set.add(new Point( x, y + 1 ));

                return set;
            }

            
            /** Return a list of valid bot moves from a given point.
             * Excludes all walls and previously set points.
             *
             * @param p The point to query.
             * @return A set containing points that may be moved to (any that are
             *         non-diagonally adjacent and also set to false).
             */
            public Set<Point> getValidMoves(Point p){
                return getValidMoves(p.x, p.y);
            }


            /* -- */


            /** Set a point true or false. 
             *
             * @param x The x co-ordinate
             * @param y The y co-ordinate
             * @param value The value to set.  true is a wall, false is a space.
             */
            public void setPoint(int x, int y, boolean value){
                 /* System.out.println(x + "," + y + " = " + (y * this.width + x)); */
                if(x >= this.width || y >= this.height || x < 0 || y < 0)
                    return;

                grid[ y * this.width + x ] = value;
            }
            
            /** Set a point on/off 
             *
             * @param p The location to set
             * @param value The value to set.  true is a wall, false is a space.
             */
            public void setPoint(Point p, boolean value){
                setPoint(p.x, p.y, value);
            }

            /** Get the on/off status of a point 
             *
             * @param x The x co-ordinate
             * @param y The y co-ordinate
             * @return The status of the point at p.  true is a wall or "forbidden" square, false is a space.
             */
            public boolean getPoint(int x, int y){
                if(x >= this.width || y >= this.height || x < 0 || y < 0)
                    return true;

                return grid[ y * this.width + x ];
            }

            /** Get the on/off status of a point.
             *
             * @param p The point to return the current status of.
             * @return The status of the point at p.  true is a wall or "forbidden" square, false is a space.
             */
            public boolean getPoint(Point p){
                return getPoint(p.x, p.y);
            }


            /* -- */


            /** Set a point at x, y to point at r.
             *
             * @param x The x co-ordinate
             * @param y The y co-ordinate
             * @param r A point representing a route---literally where this square's next
             *          step is (like FAT tables).
             */
            public void setRoutePointer(int x, int y, Point r){
                route[ y * this.width + x ] = r;
            }

            /** Set a route at point p to r.
             *
             * @param p The point to set
             * @param r A point representing a route---literally where this square's next
             *          step is (like FAT tables).
             */
            public void setRoutePointer(Point p, Point r){
                setRoutePointer(p.x, p.y, r);
            }

            /** Get the route pointer from x, y.
             *
             * @param x The x co-ordinate
             * @param y The y co-ordinate
             * @return The next point in the route, as indicated by the location given
             */
            public Point getRoutePointer(int x, int y){
                return route[ y * this.width + x ];
            }

            /** Get the route pointer from point p.
             *
             * @param p The point to get the route for.
             * @return The next point in the route, as indicated by the location given
             */
            public Point getRoutePointer(Point p){
                return getRoutePointer(p.x, p.y);
            }


            /* -- */

            /** Return the route length from this point until the next null one.
             *
             * Warning: does not fix loops, and thus can run infinitely if you
             * have set route pointers foolishly.
             *
             * @param x The x co-ordinate.
             * @param y The y co-ordinate.
             * @return The length of the path from x, y to the next null item
             */
            public int getRouteLength(int x, int y){
                // FIXME The complexity of this class is higher than the rest of the system,
                // as it does not use space/time tradeoffs.  Improve it to work with larger mazes
                // by caching.

                int length = 0;
                Point next = getRoutePointer(x, y);

                if(next == null)
                    return 0;

                // Follow route back
                while((next = getRoutePointer(next)) != null)
                    length ++;
                

                return length;
            }

            /** Return the route length from this point until the next null one.
             *
             * Warning: does not fix loops, and thus can run infinitely if you
             * have set route pointers foolishly.
             *
             * @param p The point to start tracing from.
             * @return The length of the path from x, y to the next null item
             */
            public int getRouteLength(Point p){
                return getRouteLength(p.x, p.y);
            }

        }

    }

    public void destroy() {
        /* stub */
    }

}


