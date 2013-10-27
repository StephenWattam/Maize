package maize.trial;

import maize.*;


/** Represent's a single bot's trial. 
 *
 * Distinct from an Agent in that this is a single trial delimited by time, whereas an Agent can be run many times or even
 * moved between mazes.
 *
 * */
public class BotTest{

    /** The Bot instance running in the trial. */
	public Bot bot;

    /** The Agent responsible for interpreting the bot's calls. */
	public Agent agent;

    /** The number of moves registered by the simulation since the BotTest was created. */
	public int moves;

    /** An index number used by the simulation to identify the BotTest */
	public int index;

    /** Has the agent reached the finish square? */
	public boolean isFinished = false;

    // Sequential timeouts since last success
    /** How many times the bot has failed to move (through timeout or exception) in a row. */
    public int seqTimeouts = 0; 

    /** Is the bot stuck?  Set when the simulation realises that it has failed a given number of times to move. */
    public boolean isStuck = false;
}
