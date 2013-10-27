package maize.trial;

import maize.*;
import maize.log.*;

import java.util.concurrent.*;



/** Wraps a Test and maintains a constantly-running asynchronous test.
 *
 * Useful for swing UIs that maintain tests.
 */
public class TestThread extends Thread{

	/** Delay in ms, can be changed on the fly. */
	private int delayms;

	/** Quit on next run? */
	private boolean quit = false;
	/** Pause indefinitely? */
	private boolean pause = false;

    /** The Test to be run. */
    private Test test;

    /** Create a new thread running a test set by the parameters given. 
     * 
     * @param test The Test object to run.
     * @param delayms How much to delay for each iteration in milliseconds.  1/speed.
     */
	public TestThread( Test test, int delayms ){
        this.test           = test;
        this.delayms        = delayms;
	}	

    /** Set the delay used when repeating
    * moves using run()
    *
    * @param delayms The delay time in milliseconds.
    */
	public void setDelay(int delayms){
		if(delayms >= 0)
			this.delayms = delayms;
	}

    /** Quit, stopping the test and removing
     */
	public void quit(){
        test.cancel();
		quit = true;
	}

    /** Return the current state of pause 
     */
	public boolean isPaused(){
		return pause;
	}

    /** Switches pause on/off
     */
	public void toggle_pause(){
		pause = !pause;
	}

    /** Returns the state of the simulation.
     * 
     * @return true if the simulation has stopped, else false.
     */
    public boolean isDone(){
        return test.isDone;
    }

    /** Repeatedly iterate until all bots are complete or quit() is called.
     */
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

