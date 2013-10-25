package maize.trial;

import maize.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.swing.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.io.*;
import javax.imageio.*;

import java.util.concurrent.*;




public class TestThread extends Thread{

    // Thanks to http://blog.smartkey.co.uk/2011/09/adding-a-thread-timeout-to-methods-in-java/
    // Keep a timeout
    private ExecutorService executor = Executors.newFixedThreadPool(1);

	// UI to update via callback
	public TestListener panel;

    // State
	public BotTest[] agents;

    // How long the bots have to run before being killed
    public int botStartTimeout;
    public int botWorkTimeout;

	// flipped when the bot hits the end
	public boolean isDone = false;

	// delay, can be changed on the fly
	private int delayms;

	// quit on next run
	private boolean quit = false;
	// pause indefinitely
	private boolean pause = false;

	public TestThread(Maze m, Bot[] bs, 
            TestListener client, 
            int delayms, int botStartTimeout, 
            int botWorkTimeout 
            ){   // FIXME: remove requirement for agentlist!

		if(m==null) return;
		AgentFactory af     = new AgentFactory();

		this.agents         = new BotTest[bs.length];
		this.delayms        = delayms;
		this.panel          = client;
        this.botStartTimeout  = botStartTimeout;
        this.botWorkTimeout   = botWorkTimeout;


		// Add each bot
		for(int i=0; i<bs.length; i++){
            /* Log.log("Building runtime environment for bot " + (i+1) + "/" + bs.length); */

            // Create a bot test
			agents[i]       = new BotTest();
			agents[i].bot   = bs[i];
			agents[i].agent = af.getAgent(m, bs[i]);
			agents[i].moves = 0;

            // Add to the maze panel
			panel.addAgent(agents[i]);
		}


        // Call start on all bots
        /* Log.log("Starting bots..."); */
        for(int i=0; i<this.agents.length; i++){
            /* Log.log("Calling bot.start for " + this.agents[i].bot.getName() ); */
            /* startAgentWithTimeout( MazeUISettingsManager.botStartTimeout, this.agents[i].bot ); */
            startAgentWithTimeout( botStartTimeout, this.agents[i].bot );
        }

	}	

    // Set the delay used when repeating
    // moves using run()
	public void setDelay(int delayms){
		if(delayms >= 0)
			this.delayms = delayms;
	}

    // Quit, stopping the test and removing
    // the agents from the panel
	public void quit(){
		for(int i=0;i<agents.length;i++)
			panel.remAgent(agents[i]);
		quit = true;
	}

    // Returns the current state of pause
	public boolean isPaused(){
		return pause;
	}

    // Switches pause on/off
	public void toggle_pause(){
		pause = !pause;
	}



    // Call bot.start() asynchronously, with a given timeout (in milliseconds).
    // This is used to limit the power bots have to starve each other.
    private void startAgentWithTimeout(int timeout, final Bot bot){
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
            /* Log.log("Bot " + bot.getName() + " timed out (took more than " + timeout + "ms to respond)"); */
        }catch(InterruptedException Ie){
            /* Log.log("Bot " + bot.getName() + " was interrupted during execution"); */
        }catch(Exception e){
            /* Log.log("Bot " + bot.getName() + " threw an exception: "); */
            /* Log.logException(e); */
        }finally{
            future.cancel(true);
        }
    }


    // Call agent.agent.move() asynchronously, with a given timeout (in milliseconds).
    // This is used to limit the power bots have to starve each other.
    private void moveAgentWithTimeout(int timeout, final BotTest agent){
        final Future future = executor.submit(new Runnable(){
            public void run(){
                try{
                    agent.agent.move();
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
            /* Log.log("Bot " + agent.bot.getName() + " timed out (took more than " + timeout + "ms to respond)"); */
        }catch(InterruptedException Ie){
            /* Log.log("Bot " + agent.bot.getName() + " was interrupted during execution"); */
        }catch(Exception e){
            /* Log.log("Bot " + agent.bot.getName() + " threw an exception: "); */
            /* Log.logException(e); */
        }finally{
            future.cancel(true);
        }
    }


    // Perform one move for all bots.
	private boolean iterate(){
		BotTest agent;
		boolean keepRunning = false;

		for(int i=0;i<agents.length;i++){
			agent = agents[i];

			// check against maze
			agent.isFinished = agent.agent.isFinished();

			// Are all of our agents finished?
			if(agent.isFinished == false){
				keepRunning = true;


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
            if(agents[i].isFinished)
                this.panel.remAgent(agents[i]);
            
        

		this.panel.updateAgents();
		return keepRunning;
	}

    // Repeatedly iterate until all bots are complete
	public void run(){
		while(iterate() == true){

			//update ui
			this.panel.updateAgents();

			try{ Thread.currentThread().sleep(delayms); }catch(java.lang.InterruptedException ie){ }

			// exit without completion
			if( quit ){
				for(int i=0;i<agents.length;i++)
					panel.remAgent(agents[i]);
				panel.updateAgents();
				return;
			} 

			// hang but allow for response from other calls
			while(pause)
				try{ Thread.currentThread().sleep(delayms); }catch(java.lang.InterruptedException ie){}
		}
		isDone = true;
		//update ui
		this.panel.updateAgents();
	}
}

