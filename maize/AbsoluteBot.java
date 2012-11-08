package maize;

import maize.*;

/** Provides a bot-like interface, but corrects for the orientation of the bot. 
 *
 * @author Ben Sherratt
 */
public class AbsoluteBot implements Bot
{
	private int nextMove = -1;

    /** Implementation of the Bot interface.
     *
     * @return           Bot name.
     */
    @Override
	public String getName()
	{
		return "AbsoluteBot";
	}

    /** Implementation of the Bot interface.
     *
     * @return           Bot name.
     */
    @Override
	public String getDescription()
	{
		return "Shell.  Provides a normalised form of the context matrix and bot motion (in maze space).";
	}

    /** Implementation of Bot#nextMove.  Should not be overridden when subclassing.  Use calculateMove() instead.
     *
     * @see Bot
     */
    @Override
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
							r = Direction.BACK;

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
							r = Direction.BACK;

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
							r = Direction.BACK;

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
							r = Direction.BACK;

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
			nextMove = -1;
			return r;
		}
	}

    /** Override me instead of nextMove.  Returns an Orientation in which to move, rather than a Direction.
     *
     * @see Orientation
     * @see Bot
     *
     * @param    view    View matrix from the perspective of the maze, orientated so
     *                   the top of the matrix is facing the same direction as the 
     *                   maze (NOT the bot!).
     * @param    x       X coord of the bot.
     * @param    y       Y coord of the bot.
     * @param    o       Orientation of the bot @see Orientation
     * @param    fx      X coord of the finish.
     * @param    fy      Y coord of the finish.
     *
     * @return A value from Orientation
     */
	public int calculateMove(boolean[][] view, int x, int y, int o, int fx, int fy)
	{
		boolean[][] correctedView = Orientation.rotateToNorth(view, o);

		return Orientation.SOUTH;
	}
}
