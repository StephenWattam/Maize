package maize.trial;

import maize.*;
import maize.log.*;


import java.util.concurrent.*;

/** Runs Bot objects over a Maze using Agents to control movement and using BotTests to maintain score/state for each.
 *
 * Enforces timeouts and consistency of BotTest object scores.
 */
public class Test{


    /** State of all agents as they run. 
     *
     * This variable is confusingly named. */
	public BotTest[] agents;

    /** Time in ms the bot may run start() before being killed. */
    public int botStartTimeout;
    /** Time in ms the bot may run nextMove() before being killed. */
    public int botWorkTimeout;

	/** Set to true when all bots complete the maze.
     * May be used to check if the test is done. */
	public boolean isDone = false;

    /** Listener to notify of BotTests being added or removed
     * from the test.
     */
    private TestListener listener;

    /** The number of times a bot may fail to successfully move before it is counted as
     * stuck and removed from the test. 
     */
    private int seqTimeoutLimit;

    /** Create a new Test for a given maze and bot combination.
     *
     * @param m The maze to run bots on.
     * @param bs An array of bots to run on this maze.
     * @param listener A callback object to be notified of all bot progress during the test.
     * @param botStartTimeout the number of milliseconds each bot may run start() before being killed.
     * @param botWorkTimeout the number of milliseconds each bot may run nextMove() before being killed.
     * @param seqTimeoutLimit The number of sequential failures to move each bot may incur before being counted as stuck and
     * removed from the test.
     */
	public Test(
            Maze m, 
            Bot[] bs, 
            TestListener listener, 
            int botStartTimeout, 
            int botWorkTimeout,
            int seqTimeoutLimit
            ){

		if(m==null) return;
		AgentFactory af     = new AgentFactory();

		this.agents         = new BotTest[bs.length];
		this.listener          = listener;
        this.botStartTimeout  = botStartTimeout;
        this.botWorkTimeout   = botWorkTimeout;
        this.seqTimeoutLimit  = seqTimeoutLimit;


		// Add each bot
		for(int i=0; i<bs.length; i++){
            Log.log("Building runtime environment for bot " + (i+1) + "/" + bs.length);

            // Create a bot test
			agents[i]       = new BotTest();
			agents[i].bot   = bs[i];
			agents[i].agent = af.getAgent(m, bs[i]);
			agents[i].moves = 0;

            // Add to the maze panel 
			listener.addAgent(agents[i]);
		}


        // Call start on all bots
        Log.log("Starting bots... (timeout: " + botStartTimeout + ")");
        for(int i=0; i<this.agents.length; i++){
            Log.log("Calling bot.start for " + this.agents[i].bot.getName());
            /* startAgentWithTimeout( MazeUISettingsManager.botStartTimeout, this.agents[i].bot ); */
            startAgentWithTimeout( botStartTimeout, this.agents[i].bot );
        }
        Log.log("Bots started.");

	}	

   
    /** Cancel the test, and unload all bots.
     */
    public void cancel(){
		for(int i=0;i<agents.length;i++)
			listener.remAgent(agents[i]);
    }


    /** Call bot.start() asynchronously, with a given timeout (in milliseconds).
    * This is used to limit the power bots have to starve each other.
    *
    * @param timeout The timeout in ms
    * @param bot The bot itself
    */
    private void startAgentWithTimeout(int timeout, final Bot bot){
        final ExecutorService executor = Executors.newFixedThreadPool(1);

        final Future future = executor.submit(new Runnable(){
            public void run(){
                bot.start();
            }
        });

        try{
            future.get(timeout, TimeUnit.MILLISECONDS);
            //ExecutionException: deliverer threw exception
            //TimeoutException: didn't complete within downloadTimeoutSecs
            //InterruptedException: the executor thread was interrupted
        }catch(TimeoutException Te){
            Log.log("Bot " + bot.getName() + " timed out (took more than " + timeout + "ms to respond)");
        }catch(InterruptedException Ie){
            Log.log("Bot " + bot.getName() + " was interrupted during execution");
        }catch(ExecutionException Ee){
            Log.log("Bot " + bot.getName() + " threw an exception (nested): ");
            Log.logException(Ee.getCause());
        }catch(Exception e){
            Log.log("Bot " + bot.getName() + " threw an exception: ");
            Log.logException(e);
        }finally{
            future.cancel(true);
            executor.shutdownNow();
        }
    }


    /** Call agent.agent.move() asynchronously, with a given timeout (in milliseconds).
    * This is used to limit the power bots have to starve each other.
    *
    * When bots fail, their seqTimeout properties are incremented.
    * 
    * @param timeout The timeout in ms
    * @param agent The BotTest to maintain.
    *
    */
    private void moveAgentWithTimeout(int timeout, final BotTest agent){
        final ExecutorService executor = Executors.newFixedThreadPool(1);

        final Future future = executor.submit(new Runnable(){
            public void run(){
                try{
                    agent.agent.move();
                    agent.seqTimeouts = 0;
                } catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        });

        try{
            future.get(timeout, TimeUnit.MILLISECONDS);
            //ExecutionException: deliverer threw exception
            //TimeoutException: didn't complete within downloadTimeoutSecs
            //InterruptedException: the executor thread was interrupted
        }catch(TimeoutException Te){
            increaseFailureCount(agent);
        }catch(InterruptedException Ie){
            Log.log("Bot " + agent.bot.getName() + " was interrupted during execution");
        }catch(ExecutionException Ee){
            Log.log("Bot " + agent.bot.getName() + " threw an exception (nested): ");
           
            if(Ee.getCause().getClass().getName() == "java.lang.RuntimeException"){
                Log.log("This exception was thrown by the bot itself:");
                Log.logException(Ee.getCause().getCause());
                increaseFailureCount(agent);
            }else{
                Log.logException(Ee.getCause());
            }
        }catch(Exception e){
            Log.log("Bot " + agent.bot.getName() + " threw an exception: ");
            Log.logException(e);
        }finally{
            future.cancel(true);
            executor.shutdownNow();
        }
    }

    /** Increase seqTimeouts and count sequential timeout count. 
    * Will cause the bot to register as stuck if it hits the limit.
    *
    * @param agent The BotTest object that has failed to execute.
    */
    private void increaseFailureCount(BotTest agent){
        agent.seqTimeouts ++;

        Log.log("Bot " + agent.bot.getName() + " Failed (" + agent.seqTimeouts + "/" + seqTimeoutLimit + ", " + botWorkTimeout + "ms)");

        // Set stuck if over the limit
        if(agent.seqTimeouts > seqTimeoutLimit)
            agent.isStuck = true;

    }


    /** Perform one move for all bots.
     * 
     * @return false if the simulation has ended, or true if there is still work to be done.
     */
    public boolean iterate(){
        BotTest agent;
        boolean keepRunning = false;

        for(int i=0;i<agents.length;i++){
            agent = agents[i];

            // check against maze
            agent.isFinished = agent.agent.isFinished();

            // Are all of our agents finished?
            if(agent.isFinished == false && agent.isStuck == false){
                keepRunning = true;

                /* System.out.println("Sequential timeouts: " + agent.seqTimeouts); */


                // move the agent with a timeout
                /* moveAgentWithTimeout(MazeUISettingsManager.botWorkTimeout , agent); */
                moveAgentWithTimeout(botWorkTimeout , agent);
                /* agent.agent.move(); */


                // update ui
                agent.moves++;
            }
        }

        // Remove any finished agents
        // note that remAgent checks if it's there,
        // so we don't have to.
        for(int i=0;i<agents.length;i++)
            if(agents[i].isFinished || agents[i].isStuck)
                this.listener.remAgent(agents[i]);


        this.listener.updateAgents();

        // We finish when I say we finish
        if(!keepRunning){
            isDone = true;
        }

        // Say if we're finishing or not
        return keepRunning;
    }

    
}
