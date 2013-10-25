package maize.trial;

import maize.*;
import maize.log.*;

import java.util.concurrent.*;




public class TestThread extends Thread{

	// delay, can be changed on the fly
	private int delayms;

	// quit on next run
	private boolean quit = false;
	// pause indefinitely
	private boolean pause = false;

    private Test test;

	public TestThread(
            Maze m, 
            Bot[] bs, 
            TestListener listener, 
            int delayms, 
            int botStartTimeout, 
            int botWorkTimeout,
            int seqTimeoutLimit
            ){

        this.test           = new Test(m, bs, listener, botStartTimeout, botWorkTimeout, seqTimeoutLimit);
        this.delayms        = delayms;
	}	

    // Set the delay used when repeating
    // moves using run()
	public void setDelay(int delayms){
		if(delayms >= 0)
			this.delayms = delayms;
	}

    // Quit, stopping the test and removing
	public void quit(){
        test.cancel();
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

    public boolean isDone(){
        return test.isDone;
    }

    // Repeatedly iterate until all bots are complete
	public void run(){
		while(test.iterate() == true && !quit){

			try{ Thread.currentThread().sleep(delayms); }catch(java.lang.InterruptedException ie){ }

			// hang but allow for response from other calls
			while(pause)
				try{ Thread.currentThread().sleep(delayms); }catch(java.lang.InterruptedException ie){}


		}
		//update ui
		/* this.panel.updateAgents(); */
	}
}

