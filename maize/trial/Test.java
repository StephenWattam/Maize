package maize.trial;

import maize.*;
import maize.log.*;


import java.util.concurrent.*;

public class Test{


    // Thanks to http://blog.smartkey.co.uk/2011/09/adding-a-thread-timeout-to-methods-in-java/
    // Keep a timeout
    /* private ExecutorService executor = Executors.newFixedThreadPool(1); */

    // State
	public BotTest[] agents;

    // How long the bots have to run before being killed
    public int botStartTimeout;
    public int botWorkTimeout;

	// flipped when the bot hits the end
	public boolean isDone = false;

    // Listener to notify of things
    private TestListener listener;

    private int seqTimeoutLimit;

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

    
    public void cancel(){
		for(int i=0;i<agents.length;i++)
			listener.remAgent(agents[i]);
    }


    // Call bot.start() asynchronously, with a given timeout (in milliseconds).
    // This is used to limit the power bots have to starve each other.
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
        }catch(Exception e){
            Log.log("Bot " + bot.getName() + " threw an exception: ");
            Log.logException(e);
        }finally{
            future.cancel(true);
            executor.shutdownNow();
        }
    }


    // Call agent.agent.move() asynchronously, with a given timeout (in milliseconds).
    // This is used to limit the power bots have to starve each other.
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
            Log.log("Bot " + agent.bot.getName() + " timed out (took more than " + timeout + "ms to respond)");
            agent.seqTimeouts ++;

            // Set stuck if over the limit
            if(agent.seqTimeouts > seqTimeoutLimit)
                agent.isStuck = true;

        }catch(InterruptedException Ie){
            Log.log("Bot " + agent.bot.getName() + " was interrupted during execution");
        }catch(Exception e){
            Log.log("Bot " + agent.bot.getName() + " threw an exception: ");
            Log.logException(e);
        }finally{
            future.cancel(true);
            executor.shutdownNow();
        }
    }


    // Perform one move for all bots.
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
