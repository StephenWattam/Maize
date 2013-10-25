package maize.trial;

import maize.*;


/** Represent's a single bot's trial. */
public class BotTest{
	public Bot bot;
	public Agent agent;
	public int moves;
	public int index;
	public boolean isFinished = false;

    // Sequential timeouts since last success
    public int seqTimeouts = 0; 
    public boolean isStuck = false;
}
