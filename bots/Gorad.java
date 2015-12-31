package bots;
import java.util.Random;
import maize.*;
import java.util.*;
import java.awt.Point;
/* 
 * THE 2 DEAD END PROBLEM
 * whenever we detect we're in a dead end, set inDeadEnd to true _until_
 * we are on a square with more possible routes than the way we came and and the way we're going.
 * When that happens, add the previous square's coordinates to a list of
 * dead-end openings, which we then test against when ordering possible
 * routes by preference, with a dead end being classed as blocked
 * (which could help us detect that this area is also technically a dead end,
 * but let's not get ahead of ourselves).
 * */
public class Gorad implements Bot
{
	//NESW; always NESW.
	private boolean[] cardBlock;//cardinal directions (north, south) of squares around us
	private boolean frontBlokt;
	private boolean leftBlokt;
	private boolean rightBlokt;
	private boolean backBlokt;
	
	private boolean leftOfGoal;
	private boolean aboveGoal;
	private boolean furtherVertically;
	private boolean inDeadEnd = false;
	
	private Set<Point> deadEndOpenings = new HashSet<Point>(8);
	
	//init the previous move to an invalid one, so when compared in 1st move, 
	//prevSqCard does not equal any of them.
	private int prevSqCard = 5;
	private int logicFails = 0;
	
	public String getName()
	{
		return "Gorad, the Go-Getter";
	}
	
	public String getDescription()
	{
		//return "Moves directly to the goal. If blocked, Gorad will try 1)to go around in the perpendicularly but still towards the goal, 2) the other perpendicular direction , or 3) the opposite direction to the optimum one.";
		return "Now with states! And randomness!";
		/* Tries to take the shortest possible route to the goal, by moving in the direction (x or y) that Gorad is further from the goal in.
		 * If a wall is encountered, Gorad will try in order (eg if north is best but blocked, and goal is north-west)
		 * 1)to go around in the perpendicular direction that it is from the goal, (eg west) 
		 * 2) the other perpendicular direction (eg east), then 
		 * 3) the opposite direction to the optimum one (eg south).
		 * Contains ~20% of your RDA* of nested if statements.
		 * *Based on average consumption rates for undergrads.*/
	}
	
	public int nextMove(boolean[][] view, int x, int y, int o, int fx, int fy)
	{
		int dx = x - fx;
		int dy = y - fy;
		
		//turn an orientation-dependent 2d array of blocks 
		//into a 4-long (north, east, south west) 1d array of blocks
		cardBlock = getCardinalDirectionsFromView(view, o);
				
		leftOfGoal = (dx <= 0);//determine whether we're left or right of goal
	
		aboveGoal = (dy <= 0);//determine whether we're above or below goal
		
		furtherVertically = (Math.abs(dx) < Math.abs(dy) );//are we further away horizontally or vertically
		
		//BEGIN the majority of our semi-boilerplate algorithm
		int[] prefCard = new int[4];//list of preferred directions to go in, in order of preference
		//sort cardinal directions in order of preference, 0 being the best direction to go in.
		if(aboveGoal)
		{
			if(furtherVertically)
			{
				prefCard[0] = Orientation.SOUTH;
				prefCard[3] = Orientation.NORTH;
				if(dx == 0)//if we aren't left or right of the goal, then randomise preference for east & west.
				{
					//flip a coin.
					//(is a random number between 1 and 2 divisible by 2?)
					if(( (int)(Math.floor(Math.random()+1)) ) % 2 == 0)
					{
						prefCard[1] = Orientation.EAST;
						prefCard[2] = Orientation.WEST;
					}
					else
					{
						prefCard[1] = Orientation.WEST;
						prefCard[2] = Orientation.EAST;
					}
				}
			}
			else if(dy != 0)//only run this if we are actually above or below the goal, not adjacent.
			{
				//not further vertically
				//Allow randomess to take effect in other if statements in the surrounding blocks if false
				prefCard[1] = Orientation.SOUTH;
				prefCard[2] = Orientation.NORTH;
			}
		}
		else//below goal
		{
			//if this branch executes, dy shouldn't == 0; aboveGoal is only true if dy <= 0.
			if(furtherVertically)
			{
				prefCard[0] = Orientation.NORTH;
				prefCard[3] = Orientation.SOUTH;
				if(dx == 0)//if we aren't left or right of the goal, then randomise preference for east & west.
				{
					//flip a coin.
					//(is a random number between 1 and 2 divisible by 2?)
					if(( (int)(Math.floor(Math.random()+1)) ) % 2 == 0)
					{
						prefCard[1] = Orientation.EAST;
						prefCard[2] = Orientation.WEST;
					}
					else
					{
						prefCard[2] = Orientation.EAST;
						prefCard[1] = Orientation.WEST;
					}
				}
			}
			else
			{
				prefCard[1] = Orientation.NORTH;
				prefCard[2] = Orientation.SOUTH;
			}
		}
		
		if(leftOfGoal)
		{
			if(!furtherVertically)
			{
				prefCard[0] = Orientation.EAST;
				prefCard[3] = Orientation.WEST;
				if(dy == 0)//if we aren't above or below the goal, then randomise preference for north & south.
				{
					//flip a coin.
					//(is a random number between 1 and 2 divisible by 2?)
					if(( (int)(Math.floor(Math.random()+1)) ) % 2 == 0)
					{
						prefCard[1] = Orientation.NORTH;
						prefCard[2] = Orientation.SOUTH;
					}
					else
					{
						prefCard[2] = Orientation.NORTH;
						prefCard[1] = Orientation.SOUTH;
					}
				}
			}
			else if(dx != 0)//only run this if we are actually left or right of the goal, not above or below it.
			{
				//further vertically
				prefCard[1] = Orientation.EAST;
				prefCard[2] = Orientation.WEST;
			}
		}
		else//right of goal
		{
			 //if this branch executes, dx shouldn't == 0; leftOfGoal is only true if dx <= 0.
			if(!furtherVertically)
			{
				prefCard[0] = Orientation.WEST;
				prefCard[3] = Orientation.EAST;
				if(dy == 0)//if we aren't above or below the goal, then randomise preference for north & south.
				{
					//flip a coin.
					//(is a random number between 1 and 2 divisible by 2?)
					if(( (int)(Math.floor(Math.random()+1)) ) % 2 == 0)
					{
						prefCard[1] = Orientation.NORTH;
						prefCard[2] = Orientation.SOUTH;
					}
					else
					{
						prefCard[2] = Orientation.NORTH;
						prefCard[1] = Orientation.SOUTH;
					}
				}
			}
			else
			{
				prefCard[1] = Orientation.WEST;
				prefCard[2] = Orientation.EAST;
			}
		}
		
		//calculate number of blockedDirs in advance now, in order to
		//work out if we've come out of a dead end yet
		int blockedDirs=0;
		for(int i = 0; i < prefCard.length; i++)
		{
			if(cardBlock[prefCard[i]])//if this dir is blocked, increment blockedDirs
			{
				blockedDirs++;
			}
			else if(isAKnownDeadEnd(getCoordOfAdjacent(new Point(x,y), prefCard[i])))
			{
				//is this way a known dead end opening?
				//treat the mouth of a known dead end as blocked, because 
				//there's no way we'd want to go that way.
				blockedDirs++;
			}
		}
		if(inDeadEnd
		&& blockedDirs <= 1)
		{
			//if this is the first time there's more direction to go in
			//than just back to the dead end and the way out of it,
			//we're no longer in a dead end, but the last square we were on led to it
			inDeadEnd = false;
			deadEndOpenings.add(getCoordOfAdjacent(new Point(x,y), prevSqCard));
		}
		
		//perform a series of eliminating tests to work out
		//which preferred direction we actually CAN go in
		//this implementation is kind of inefficient because
		//we perform the same set of tests twice.
		//refactor it later.
		for(int i = 0; i < prefCard.length; i++)
		{
			if(cardBlock[prefCard[i]])//if this dir is blocked, and move on
			{
				continue;
			}
			else if(isAKnownDeadEnd(getCoordOfAdjacent(new Point(x,y), prefCard[i])))
			{
				//is this way a known dead end opening?
				//treat the mouth of a known dead end as blocked, because 
				//there's no way we'd want to go that way.
				blockedDirs++;
				continue;
			}
			else if(prefCard[i] == prevSqCard)//if we came from this way, move on
			{
				continue;
			}
			else
			{
				//if it's not blocked, isn't the way we came
				//(and is the best direction due to the array testing the most preferential first),
				//go this way!
/*exitpoint*/	return checkMove(prefCard[i], o);
			}
		}
		if(blockedDirs == 3)//are we at the end of a dead end?
		{
			//now check if the 3 directions which arent the one we came from are blocked; 
			//if so, go the we way we came, as we are in a dead end.
			//By logic, the only unblocked path is the way we came. Go back that way.
			inDeadEnd = true;
/*exit*/	return checkMove(prevSqCard, o);
		}
		System.out.print("Gorad error "+(++logicFails)+": in nextMove(). randomising direction. \n");
		return (int)(Math.random() * 4);
	}
	
	/**method to translate an absolute suggested direction (north, west, etc)
	into an action for Gorad to perform (turn right, go forwards etc).
	* Called when we've worked out which actual direction we want to go in, and
	* want Gorad to do whatever it takes to to that
	@param card - cardinal direction we want to travel in
	@param orie - the current orientation of the bot
	@return the Directional movement constant we want to perform*/
	private int checkMove(int card, int orie)
	{
		//are we facing that direction as well?
		if(card == orie)
		{
			//YUSS
			//set the direction back to this square to be 180deg to the way we're going now
			prevSqCard = (card + 2) % 4;
			return Direction.FORWARD;
		}
		else if( ((orie+3) % 4) == card)
		{
			//is the preferred cardinal direction to our left?
			//we didn't move on this go, so only turn left, don't change prefSqCard;
			//the direction back to this square shouldnt change, so we remember which way we went
			//when we did actually last move.
			return Direction.LEFT;//turn left
		}
		else if(((orie+1) % 4) == card)
		{
			//to our right?
			return Direction.RIGHT;//turn right
		}
		else
		{
			//behind us?
			//set the direction back to this square to be 180deg to the way we're going now
			prevSqCard = (card + 2) % 4;
			return Direction.BACK;
		}
		//if we haven't returned by now (and we should have), just do something random
		//System.out.print("Gorad error "+(++logicFails)+": in checkMove(). randomising direction. \n");
		//return (int) (Math.random() * 4);
	}
	
	private boolean isAKnownDeadEnd(Point pCheck)
	{
		for(Point pd : deadEndOpenings)
		{
			if(pCheck.equals(pd))
			{
				return true;
			}
		}
		return false;
	}

	/**helper method to get the coordinates of a square adjacent to the
	 * coordinates of another square.
	 * @param current the reference point
	 * @param orie the direction from the reference point we want to get the coordinates for.
	 * <br /> Valid Values:
	 * <ul>
	 * <li>0: North</li>
	 * <li>0: East </li>
	 * <li>0: South </li>
	 * <li>0: West </li>
	 * </ul>*/
	private Point getCoordOfAdjacent(Point current, int orie)
	{
		int outx;
		int outy;
		switch(orie)
		{
			//the square north of us
			case 0:
			outx = current.x;
			outy = current.y -1;
			break;
			
			//east of us
			case 1:
			outx = current.x + 1;
			outy = current.y;
			break;
			
			//south
			case 2:
			outx = current.x;
			outy = current.y + 1;
			break;
			
			//west
			case 3:
			outx = current.x - 1;
			outy = current.y;
			break;
			
			default:
			outx = current.x;
			outy = current.y;
			System.out.print("Gorad error "+(++logicFails)+": something went wrong in getting an adjacent coordinate.");
		}
		return new Point(outx, outy);
	}
	
	public boolean[] getCardinalDirectionsFromView(boolean[][] view, int orientation)
	{
		frontBlokt = view[1][0];
		leftBlokt = view[0][1];
		rightBlokt = view[2][1];
		backBlokt = view[1][2];
		
		boolean[] properDirections = new boolean[4];
		/*make array of whether north, east, south west (respectively)
		 are blocked, independent of current orientation.
		 * 0=north
		 * 1=east
		 * 2=west
		 * 3=south
		 * */
		switch(orientation)
		{
			//we're facing north
			case Orientation.NORTH:
			properDirections[0] = frontBlokt;//front-facing tile is north
			properDirections[1] = rightBlokt;//
			properDirections[2] = backBlokt;
			properDirections[3] = leftBlokt;
			break;
			
			case Orientation.EAST://we're facing east
			properDirections[1] = frontBlokt;//front-facing tile is east
			properDirections[2] = rightBlokt;
			properDirections[3] = backBlokt;
			properDirections[0] = leftBlokt;
			break;
			
			case Orientation.SOUTH:
			properDirections[2] = frontBlokt;
			properDirections[3] = rightBlokt;
			properDirections[0] = backBlokt;
			properDirections[1] = leftBlokt;
			break;
			
			case Orientation.WEST:
			properDirections[3] = frontBlokt;
			properDirections[0] = rightBlokt;
			properDirections[1] = backBlokt;
			properDirections[2] = leftBlokt;
			break;
		}
		return properDirections;
	}

    @Override
    public void start(){}

    @Override
    public void destroy() {
        /* stub */
    }
}
