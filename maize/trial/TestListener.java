
package maize.trial;

import maize.*;

/** Handles updates from a TestThread about bots completing and such. */
public interface TestListener{

    /** Called when an agent is added to the maze test.
     */
    public void addAgent(BotTest a);
    

    /** Called when an agent is removed from the maze.
     *
     * This could be due to completion, for example.
     */
    public void remAgent(BotTest a);


    /** Called to signify that the values in the added agents have changed.
     *
     * If a listener polls, there's no need to watch this.
     */
    public void updateAgents();
}
