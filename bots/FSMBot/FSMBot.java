package bots.FSMBot;

import maize.Bot;

public class FSMBot implements Bot {
    /** Put your control logic here
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
    public int nextMove(boolean[][] view, int x, int y, int o, int fx, int fy)
    {
        State testState = new State();
        return -1;
    }

    /** A nice fluffy name for the bot, like 'Bob' or 'John'.
     *
     * @return           Bot name.
     */
    public String getName() { return "FSMBot"; }

    /** A description of how it works for telling folks.
     *
     * @return           Bot Description.
     */
    public String getDescription() { return "A finite state machine bot, reads JFlap files"; }

    /** Called when a new maze is started. */
    public void start() { /* stub */ }

    /** Called when a bot is about to be destroyed */
    public void destroy() { /* stub */ }
}