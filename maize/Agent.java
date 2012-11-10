package maize;

/** Agent class which defines the API which bots access. */
public final class Agent {

    /** Maze */
    private Maze maze = null;

    /** Bot driving the agent */
    private Bot bot = null;

    /** Agent X coord */
    private int x = 0;

    /** Agent Y coord */
    private int y = 0;

    /** Agent Orientation */
    private int o = 0;

    /** Agent Constructor closed off tot he public */
    private Agent() {}

    /** Agent Constructor to only be called by the AgentFactory.
     * Starts with agent facing NORTH.
     *
     * @param  maze      Maze instance
     * @param  bot       Bot instance
     * @param  x         Starting X coord
     * @param  y         Starting Y coord
     */
    public Agent(Maze maze, Bot bot, int x, int y){

        this.maze = maze;
        this.bot = bot;
        this.x = x;
        this.y = y;
        this.o = Orientation.NORTH;
    }

    /** Shows a rotated view of your surroundings. 
     * Orientation matters.
     *
     * @return           View of surroundings
     */
    public boolean[][] peek(){

        /* get map meta data */
        int mw = maze.getWidth();
        int mh = maze.getHeight();

        boolean[][] mazedata = maze.getData();
        boolean[][] view = new boolean[3][3];

        /* Fill view array */
        view[0][0] = checkMazePoint(mazedata, x-1,  y-1);
        view[0][1] = checkMazePoint(mazedata, x-1,  y);
        view[0][2] = checkMazePoint(mazedata, x-1,  y+1);

        view[1][0] = checkMazePoint(mazedata, x,  y-1);
        view[1][1] = checkMazePoint(mazedata, x,    y);
        view[1][2] = checkMazePoint(mazedata, x,  y+1);

        view[2][0] = checkMazePoint(mazedata, x+1,  y-1);
        view[2][1] = checkMazePoint(mazedata, x+1,    y);
        view[2][2] = checkMazePoint(mazedata, x+1,  y+1);



        /* Rotate into bot view */
        for(int i = this.o; i > 0; i--) { 
            view = rotateArray(view); 
        }

        return view;
    }

    /** Gets maze value at a certain point.
     * Performs bound checking.
     *
     * @param  maze      boolean array
     * @param  x         x coord
     * @param  y         y coord
     *
     * @return           true for hedge, false for path
     */
    private boolean checkMazePoint(boolean[][] maze, int x, int y){
        /* Bound checking */
        if( x < 0 || y < 0 || y >= maze.length || x >= maze[0].length)
            return true;
        
        /* Set finish to blank */
        if(x == this.maze.getExiX() && y == this.maze.getExiY())
            return false;
        
        return maze[x][y];
    }

    // Thank to http://stackoverflow.com/questions/2799755/rotate-array-clockwise
    private static boolean[][] rotateArray(boolean[][] mat) {
        final int M = mat.length;
        final int N = mat[0].length;
        boolean[][] ret = new boolean[N][M];
        for (int r = 0; r < M; r++) {
            for (int c = 0; c < N; c++) {
                ret[c][M-1-r] = mat[r][c];
            }
        }
        return ret;
    }

/*
    /** Rotate array 90 degrees clockwise.
     * To all: Apologises.
     *
     * @param  array     Array to rotate.
     *
     * @return           Rotated array.
     * /
    private boolean[][] rotateArray(boolean[][] array){

        boolean[][] narray = new boolean[3][3];

        narray[0][2] = array[0][0];
        narray[0][1] = array[1][0];
        narray[0][0] = array[2][0];

        narray[1][2] = array[0][1];
        narray[1][1] = array[1][1];
        narray[1][0] = array[2][1];

        narray[2][2] = array[0][2];
        narray[2][1] = array[1][2];
        narray[2][0] = array[2][2];

        return narray;
    }
    */

    /** Rotate agent right.
     * Changes orientation.
     */
    private void rotateRight(){

        if(this.o == Orientation.WEST){
            this.o = Orientation.NORTH;
        } else {
            this.o++;
        }
    }

    /** Rotate agent left.
     * Changes orientation.
     */
    private void rotateLeft(){

        if(this.o == Orientation.NORTH){
            this.o = Orientation.WEST;
        } else {
            this.o--;
        }
    }

    /** Move agent forward.
     * Orientation specific
     */
    private void moveForward(){

        boolean[][] maze = this.maze.getData();


        switch(this.o){

            case Orientation.NORTH:
                this.y = (!checkMazePoint(maze, this.x, this.y-1)) ? this.y-1 : this.y;
                break;

            case Orientation.SOUTH:
                this.y = (!checkMazePoint(maze, this.x, this.y+1)) ? this.y+1 : this.y;
                break;

            case Orientation.EAST:
                this.x = (!checkMazePoint(maze, this.x+1, this.y)) ? this.x+1 : this.x;
                break;

            case Orientation.WEST:
                this.x = (!checkMazePoint(maze, this.x-1, this.y)) ? this.x-1 : this.x;
                break;
        }
    }

    /** Move agent backwards.
     * Orientation specific
     */
    private void moveBackward(){

        boolean[][] maze = this.maze.getData();

        switch(this.o){

            case Orientation.SOUTH:
                this.y = (!checkMazePoint(maze, this.x, this.y-1)) ? this.y-1 : this.y;
                break;

            case Orientation.NORTH:
                this.y = (!checkMazePoint(maze, this.x, this.y+1)) ? this.y+1 : this.y;
                break;

            case Orientation.WEST:
                this.x = (!checkMazePoint(maze, this.x+1, this.y)) ? this.x+1 : this.x;
                break;

            case Orientation.EAST:
                this.x = (!checkMazePoint(maze, this.x-1, this.y)) ? this.x-1 : this.x;
                break;
        }
    }

    /** Move method which calls the agent's bot for guidance in life.
    */
    public void move(){

        /* Oh Prophet, what shall I do? */
        int move = this.bot.nextMove(this.peek(), this.x, this.y, this.o, this.maze.getExiX(), this.maze.getExiY());

        /* Check if move is valid */
        if(move >= 0 || move < 4){

            switch(move){

                case Direction.FORWARD: 
                    this.moveForward();
                    break;

                case Direction.BACK:
                    this.moveBackward();
                    break;

                case Direction.LEFT:
                    this.rotateLeft();
                    break;

                case Direction.RIGHT:
                    this.rotateRight();
                    break;
            }
        }
    }

    /** Get the X coordinate of the agent.
     *
     * @return         X Coord.
     */
    public int getX(){

        return this.x;
    }

    /** Get the Y coordinate of the agent.
     *
     * @return         Y Coord.
     */
    public int getY(){

        return this.y;
    }

    /** Get the Orientation of the agent.
     * @see Direction
     *
     * @return         Orientation.
     */
    public int getOrientation(){

        return this.o;
    }

    /** Returns if agent has finished.
     *
     * @return         Finished?
     */
    public boolean isFinished(){

        return (this.x == this.maze.getExiX() && this.y == this.maze.getExiY());
    }
}
