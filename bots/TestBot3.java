package bots;
import maize.*;
import java.io.Serializable;
import java.lang.Math;
import java.lang.Object;
import java.util.Arrays;


/** Interface for logic and stuffs */	
public class TestBot3 implements Bot, Serializable {

	private boolean[][][] actionMap; // initialized in start()
	private int nextMove; // stores next move. initialized in start()
	
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
    public void start()
	{
		debugLn("========START start()=======================");
		debugLn("Initializing...");
		actionMap = new boolean[1][1][4];
		nextMove = -1;
		debugLn("========END  start()========================");
	}
	
    @Override
    public int nextMove(boolean[][] view, int x, int y, int o, int fx, int fy){
	
		debugLn("========START nextMove()====================");
		view = Orientation.rotateToNorth(view, o); // replace view so that it is always facing north
		
		int nextOp;
		if ((nextOp = returnNextOperation(o)) != -1) //if operation in que
			return nextOp;
		
		int[] optimalDirs = getOptimalDirs(x, y, fx, fy); // get array of directions in order of importance
		Outer:
		while (true)
		{
			for(int i=0; i < optimalDirs.length; i++) // it is possible for this to be required to run twice
			{
				if (!isPreviousAction(x, y, optimalDirs[i])) // if not been this direction before from this point on the maze
				{
					logAction(x, y, optimalDirs[i]); // log this action as being attempted
					if (!isWall(view, optimalDirs[i])) //if no wall in the way
					{
						nextMove = optimalDirs[i]; // use this direction
						break Outer;
					}
				}
			}
		}
		debugLn("========END   nextMove()====================");
		return returnNextOperation(o);
    }

	private boolean isWall(boolean[][] view, int dir){
		
		debugLn("========START isWall()======================");
		boolean returnVal;
		if (dir == Orientation.NORTH && view[1][0])
			returnVal = true;
		else if (dir == Orientation.EAST && view[2][1])
			returnVal = true;		
		else if (dir == Orientation.SOUTH && view[1][2])
			returnVal = true;		
		else if (dir == Orientation.WEST && view[0][1])
			returnVal = true;
		else
			returnVal = false;
		
		debugLn("========END   isWall()======================");
		return returnVal;
	}
	
	private boolean atFinish(int x, int y, int fx, int fy){
		debugLn("========START atFinish()====================");
		debugLn("========END   atFinish()====================");
		return (x==fx && y==fy);
	}
	
	private boolean approachingFinish(int x, int y, int fx, int fy, int action)
	{
		debugLn("========START approachingFinish()===========");
		boolean returnVal;
		switch (action)
		{
			case Orientation.NORTH:	returnVal = atFinish(x  , y-1, fx, fy);break;			
			case Orientation.EAST:	returnVal = atFinish(x+1, y  , fx, fy);break;
			case Orientation.SOUTH:	returnVal = atFinish(x  , y+1, fx, fy);break;
			case Orientation.WEST:	returnVal = atFinish(x-1, y  , fx, fy);break;
			default: returnVal = false;break;
		}
		debugLn("========END   approachingFinish()===========");
		return returnVal;
	}
	
	private int[] getOptimalDirs(int x, int y, int fx, int fy){
		
		debugLn("========START getOptimalDirs()==============");
		int[] directions = new int[4];
		int xDist = fx - x;
		int yDist = fy - y;
		if (xDist == 0)
		{
			if (yDist < 0)
			{
				directions[0] = Orientation.NORTH;
				directions[3] = Orientation.SOUTH;		
			}
			else
			{
				directions[0] = Orientation.SOUTH;
				directions[3] = Orientation.NORTH;
			}
			directions[1] = Orientation.EAST;
			directions[2] = Orientation.WEST;	
		}
		else if (yDist == 0)
		{
			if (xDist < 0)
			{
				directions[0] = Orientation.WEST;
				directions[3] = Orientation.EAST;		
			}
			else
			{
				directions[0] = Orientation.EAST;
				directions[3] = Orientation.WEST;
			}
			directions[1] = Orientation.NORTH;
			directions[2] = Orientation.SOUTH;
		}
		else if(Math.abs(xDist) < Math.abs(yDist))
		{
			if (xDist < 0)
			{
				directions[0] = Orientation.WEST;
				if (yDist < 0)
				{
					directions[1] = Orientation.NORTH;
					directions[3] = Orientation.SOUTH;
				}
				else
				{
					directions[1] = Orientation.SOUTH;
					directions[3] = Orientation.NORTH;
				}
				directions[2] = Orientation.EAST;
			}
			else
			{
				directions[0] = Orientation.EAST;
				if (yDist < 0)
				{
					directions[1] = Orientation.NORTH;
					directions[3] = Orientation.SOUTH;
				}
				else
				{
					directions[1] = Orientation.SOUTH;
					directions[3] = Orientation.NORTH;
				}
				directions[2] = Orientation.WEST;
			}
		}
		else
		{
			if (yDist < 0)
			{
				directions[0] = Orientation.NORTH;
				if (xDist < 0)
				{
					directions[1] = Orientation.WEST;
					directions[3] = Orientation.EAST;
				}
				else
				{
					directions[1] = Orientation.EAST;
					directions[3] = Orientation.WEST;
				}
				directions[2] = Orientation.SOUTH;
			}
			else
			{
				directions[0] = Orientation.SOUTH;
				if (xDist < 0)
				{
					directions[1] = Orientation.WEST;
					directions[3] = Orientation.EAST;
				}
				else
				{
					directions[1] = Orientation.EAST;
					directions[3] = Orientation.WEST;
				}
				directions[2] = Orientation.NORTH;
			}
		}
		
		// now check to see if any of the directions would take the bot somewhere it's already been. If so move that direction to the bottom of the array
		int[] optimisedDirections = directions.clone();
		
		int j=0; //contains actual index given that elements can be shifted
		for(int i=0; i < directions.length; i++)
		{
			if(isPreviousLocation(x, y, directions[i]))
				optimisedDirections = shiftArray(optimisedDirections, j); // shift to end of array
			else
				j++; //only increase if element not shifted
		}
		directions = optimisedDirections;
		
		debugLn("========END   getOptimalDirs()==============");
		return directions;
	}
	
	private void logAction(int x, int y, int action){
	
		debugLn("========START logAction()===================");
		boolean[] newAction = actionMap[x][y]; // set as previous action
		
		if (newAction[0] && newAction[1] && newAction[2] && newAction[3]) // all actions previousley taken
		{
			newAction[0] = false;
			newAction[1] = false;
			newAction[2] = false;
			newAction[3] = false;
		}
		
		switch (action)
		{
			case Orientation.NORTH:	newAction[0] = true; break;
			case Orientation.EAST:	newAction[1] = true; break;
			case Orientation.SOUTH:	newAction[2] = true; break;
			case Orientation.WEST:	newAction[3] = true; break;
			default: break;
		}
		writeToMap(x, y, newAction);
		debugLn("========END   logAction()===================");
	}
	
	private int returnNextOperation(int currentDirection)
	{
		debugLn("========START returnNextOperation()=========");
	
		int command;

		if (nextMove == -1)
		{
			command = -1;
		}
		else
		{
			//pointing in correct direction
			if (nextMove == currentDirection) 
			{
				command = Direction.FORWARD;
				nextMove = -1; // no more moves needed
			}
			
			else if (nextMove == getOppositeDir(currentDirection)) // pointing in opposite direction
			{
				command = Direction.BACK;
				nextMove = -1; // no more moves needed
			}
			else
			{
				command = Direction.RIGHT; // will only have to turn once to get to correct position
			}
		}
		if (command != -1)
		{
			debugLn("Going " + Direction.getName(command) + ".");
		}
		
		debugLn("========END   returnNextOperation()=========");
		return command;
	}
	
	private int getOppositeDir(int orientation){
	
		debugLn("========START getOppositeDir()==============");
		int returnVal = orientation + 2;
		if (returnVal > 3)returnVal -= 4;
		debugLn("========END   getOppositeDir()==============");
		return returnVal;
	}
	
	private boolean isPreviousLocation(int x, int y, int action)
	{
		debugLn("========START isPreviousLocation()==========");
		boolean returnVal;
		switch (action)
		{
			case Orientation.NORTH:	returnVal = beenAtLocation(x  , y-1);break;
			case Orientation.EAST:	returnVal = beenAtLocation(x+1, y  );break;
			case Orientation.SOUTH:	returnVal = beenAtLocation(x  , y+1);break;
			case Orientation.WEST:	returnVal = beenAtLocation(x-1, y  );break;
			default: returnVal=false;break;
		}
		debugLn("========END   isPreviousLocation()==========");
		return returnVal;
	}
	
	private boolean beenAtLocation(int x, int y)
	{
		debugLn("========START beenAtLocation()==============");
		resizeMap(x, y); //resize map if necessary. presuming there is always a wall around edge.
		boolean[] previousAction = actionMap[x][y];
		debugLn("========END   beenAtLocation()==============");
		return (previousAction[0] || previousAction[1] || previousAction[2] || previousAction[3]);
	}
	
	private boolean isPreviousAction(int x, int y, int action){
	
		debugLn("========START isPreviousAction()============");
		resizeMap(x, y); //resize map if necessary
		boolean[] previousAction = actionMap[x][y];
		
		boolean returnVal;
		
		if (previousAction[0] && previousAction[1] && previousAction[2] && previousAction[3])returnVal = false;	// all directions have been taken		
		else if (previousAction[0] && action == Orientation.NORTH)returnVal = true;
		else if (previousAction[1] && action == Orientation.EAST)returnVal = true;
		else if (previousAction[2] && action == Orientation.SOUTH)returnVal = true;
		else if (previousAction[3] && action == Orientation.WEST)returnVal = true;
		else returnVal = false;
		debugLn("========END   isPreviousAction()============");
		return returnVal;
	}
	
	private void writeToMap(int x, int y, boolean[] val){
		
		debugLn("========START writeToMap()==================");
		resizeMap(x, y); //resize map if necessary
		actionMap[x][y] = val;
		debugLn("========END   writeToMap()==================");
	}
	
	private void resizeMap(int x, int y){
		debugLn("========START resizeMap()===================");
		int width = actionMap.length;
		int height = actionMap[0].length;
		boolean needsResize = false;
		
		if (y+1 > height)
		{
			height = y+1;
			needsResize = true;
		}
		if (x+1 > width)
		{
			width = x+1;
			needsResize = true;
		}
		if (needsResize) // only continue if array needs resizing
		{
			
			
			boolean[][][] mapCopy = actionMap.clone(); // check this if it doesn't work.
			
			
			actionMap = new boolean[width][height][4];

			for(int i=0; i < mapCopy.length; i++)
			{
				for (int j=0; j< mapCopy[i].length; j++)
				{
					for (int k=0; k< mapCopy[i][j].length; k++)
					{
						actionMap[i][j][k] = mapCopy[i][j][k];
					}
				}
			}
		}
		debugLn("========END   resizeMap()===================");	
	}
	
	private int[] shiftArray(int[] array, int index) //move value at index to bottom of array. http://stackoverflow.com/a/7970968/1048589
	{
		debugLn("========START shiftArray()==================");
		int temp = array[index];

		for (int i = index+1; i <= (array.length - 1); i++) {                
			array[i-1] = array[i];
		}
		array[array.length-1] = temp;
		debugLn("========END   shiftArray()==================");
		return array;
	}
	
	private void debugLn(String str)
	{
        System.out.println(str + "\n");
    }
	
    /** Implementation of the Bot interface.
     *
     * @return           Bot name.
     */
    @Override
    public String getName(){
        return "24 Hour Bot";
    }

    /** Implementation of the Bot interface.
     *
     * @return           Bot Description.
     */
    @Override
    public String getDescription(){
        return "It tries its best, and that's all you can really ask for.";
    }
}


