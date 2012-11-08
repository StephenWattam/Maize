package maize;

import maize.*;

/* Provides a bot-like interface, but corrects for the orientation of the bot. 
 *
 * @author Ben Sharatt
 * */
public class AbsoluteBot implements Bot
{
	private int nextMove = -1;
	private int nextNextMove = -1;

	public String getName()
	{
		return "AbsoluteBot";
	}

	public String getDescription()
	{
		return "Shell.  Provides a normalised form of the context matrix and bot motion (in maze space).";
	}

	public int nextMove(boolean[][] view, int x, int y, int o, int fx, int fy)
	{
		if(nextMove == -1)
		{
			boolean[][] correctedView = Orientation.rotateToNorth(view, o);
			int nextMoveVal = calculateMove(correctedView, x, y, 0, fx, fy);
			int r = -1;

			switch(o)
			{
				case Orientation.NORTH:
					switch(nextMoveVal)
					{
						case Orientation.NORTH:
							r = Direction.FORWARD;

							break;

						case Orientation.SOUTH:
							r = Direction.LEFT;
							nextMove = Direction.LEFT;
							nextNextMove = Direction.FORWARD;

							break;

						case Orientation.WEST:
							r = Direction.LEFT;
							nextMove = Direction.FORWARD;

							break;

						case Orientation.EAST:
							r = Direction.RIGHT;
							nextMove = Direction.FORWARD;

							break;
					}

					break;

				case Orientation.SOUTH:
					switch(nextMoveVal)
					{
						case Orientation.NORTH:
							r = Direction.LEFT;
							nextMove = Direction.LEFT;
							nextNextMove = Direction.FORWARD;

							break;

						case Orientation.SOUTH:
							r = Direction.FORWARD;

							break;

						case Orientation.WEST:
							r = Direction.RIGHT;
							nextMove = Direction.FORWARD;

							break;

						case Orientation.EAST:
							r = Direction.LEFT;
							nextMove = Direction.FORWARD;

							break;
					}

					break;

				case Orientation.WEST:
					switch(nextMoveVal)
					{
						case Orientation.NORTH:
							r = Direction.RIGHT;
							nextMove = Direction.FORWARD;

							break;

						case Orientation.SOUTH:
							r = Direction.LEFT;
							nextMove = Direction.FORWARD;

							break;

						case Orientation.WEST:
							r = Direction.FORWARD;

							break;

						case Orientation.EAST:
							r = Direction.LEFT;
							nextMove = Direction.LEFT;
							nextNextMove = Direction.FORWARD;

							break;
					}

					break;

				case Orientation.EAST:
					switch(nextMoveVal)
					{
						case Orientation.NORTH:
							r = Direction.LEFT;
							nextMove = Direction.FORWARD;

							break;

						case Orientation.SOUTH:
							r = Direction.RIGHT;
							nextMove = Direction.FORWARD;

							break;

						case Orientation.WEST:
							r = Direction.LEFT;
							nextMove = Direction.LEFT;
							nextNextMove = Direction.FORWARD;

							break;

						case Orientation.EAST:
							r = Direction.FORWARD;

							break;
					}

					break;
			}

			return r;
		}
		else
		{
			int r = nextMove;
			nextMove = nextNextMove;
			nextNextMove = -1;
			return r;
		}
	}

    /* Override me instead of nextMove */
	public int calculateMove(boolean[][] view, int x, int y, int o, int fx, int fy)
	{
		boolean[][] correctedView = Orientation.rotateToNorth(view, o);

		return Orientation.SOUTH;
	}
}
