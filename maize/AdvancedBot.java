package maize;

/** This bot retains some state and performs more complex actions easily.  
 *It has been designed as a simpler API for
 * some functions that are prohibitively complex using a stateless model, thus it infers a limited model from its
 * surroundings.  
 */
public class AdvancedBot implements Bot{

	private static final int RETARD_THRESHOLD = 1000000;
	private static final int CIRC_BUFFER_LIMIT = 500;

	/** Holds all operations. */
	private int[] operation_queue = new int[CIRC_BUFFER_LIMIT];

	/** Next free index for inserting a move. */
	private int nextop = 0; 

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
	 * @return     Next move in form of Direction.
	 */
	public int nextMove(boolean[][] view, int x, int y, int o, int fx, int fy){

		/* keep track of idiots' nondeterminism */
		int retard_counter = 0;

		while(nextop <= 0){
			this.makeMove(view, x, y, o, fx, fy);
			if(retard_counter >  RETARD_THRESHOLD)
				System.err.println("You are looping without issuing instructions, it's happened " + retard_counter + " times already -- fix your code to prevent this.");

			retard_counter ++;
		}

		return shuffle();
	}

	/** Gets the next move from the buffer.
	 *
	 * @return           Next move.
	 */
	private int shuffle(){

		/* Circular queue operation */
		operation_queue[operation_queue.length-1] = operation_queue[0];

		for(int i=1; i<CIRC_BUFFER_LIMIT; i++)
			operation_queue[i-1] = operation_queue[i];

		nextop --;

		return operation_queue[operation_queue.length-1];
	}


	/** Place your bot logic here.
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
	 */
	public void makeMove(boolean[][] view, int x, int y, int o, int fx, int fy){

		turnLeft();
		forward();

	}

	/** Queue up an arbitrary command .
	 *
	 * @param		cmd		Bot Command
	 *
	 * @return         false if there is no room in the circular buffer
	 */
	private boolean qcmd(int cmd){

		if(nextop >= operation_queue.length)
			return false;	    

		/* Assign the command in the next free space. */
		operation_queue[nextop] = cmd;

		/* Point at the free space beyond. 
		 * Note this will point at length( operation_queue), then fail next time with a false call.
		 */
		nextop ++;

		return true;
	}

	/** Queues a move forward.
	 */
	protected void forward(){	qcmd( Direction.FORWARD ); }

	/** Queues a move backward.
	 */
	protected void backward(){	qcmd( Direction.BACK);  }

	/** Queues a left rotation .
	 */
	protected void rotateLeft(){	qcmd( Direction.LEFT);  }

	/** Queues a right rotation.
	 */
	protected void rotateRight(){	qcmd( Direction.RIGHT); }

	/** Queues a left strafe.
	 */
	protected void strafeLeft(){
		qcmd( Direction.LEFT);
		qcmd( Direction.FORWARD);
		qcmd( Direction.RIGHT);
	}

	/** Queues a right strafe.
	 */
	protected void strafeRight(){
		qcmd( Direction.RIGHT);
		qcmd( Direction.FORWARD);
		qcmd( Direction.LEFT);
	}

	/** Queues a 180 rotation.
	 */
	protected void rotate180(){
		qcmd(Direction.RIGHT);
		qcmd(Direction.RIGHT);
	}

	/** Turns left around an obsticle.
	 * If gap is in NW corner of view, will end up on the square facing in.
	 */
	protected void turnLeft(){
		qcmd( Direction.FORWARD);
		qcmd( Direction.LEFT);
		qcmd( Direction.FORWARD);
	}	

	/** Turns right around an obsticle.
	 * If gap is in NE corner of view, will end up on the square facing in.
	 */
	protected void turnRight(){
		qcmd( Direction.FORWARD);
		qcmd( Direction.RIGHT);
		qcmd( Direction.FORWARD);
	}	

	/** Reverses left around an obsticle.
	 * If gap is in SW corner of view, will end up on the square facing back.
	 */
	protected void reverseLeft(){
		qcmd( Direction.BACK);
		qcmd( Direction.RIGHT);
		qcmd( Direction.BACK);
	}

	/** Reverses right around an obsticle.
	 * If gap is in SE corner of view, will end up on the square facing back.
	 */
	protected void reverseRight(){
		qcmd( Direction.BACK);
		qcmd( Direction.RIGHT);
		qcmd( Direction.BACK);
	}

	/** A nice fluffy name for the bot, like 'Bob' or 'John'. 
	 *
	 * @return           Bot name.
	 */
	public String getName(){
		return "AdvancedBot";
	}

	/** Implementation of the Bot interface.
	 *
	 * @return           Bot Description.
	 */
	public String getDescription(){
		return "Is capable of complex sequences of actions, but by default does little.";
	}

    /** Implementation of bot#start */
    public void start(){}

    @Override
    public void destroy() {
    	/* stub */
    }
}
