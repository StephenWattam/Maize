package maize.ui;

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





public class TestThread extends Thread{

	// store state on one run
	public MazePanel panel;
	public BotTest[] agents;

	// ui to update
	public Component agentList;

	// flipped when the bot hits the end
	public boolean isDone = false;

	// delay, can be changed on the fly
	private int delayms;

	// quit on next run
	private boolean quit = false;
	// pause indefinitely
	private boolean pause = false;

	public TestThread(Maze m, Bot[] bs, MazePanel mp, int delayms, JList agentList){
		if(m==null) return;
		AgentFactory af = new AgentFactory();
		this.agents = new BotTest[bs.length];

		this.delayms = delayms;
		this.panel = mp;
		// Add each bot
		for(int i=0; i<bs.length; i++){
            Log.log("Building runtime environment for bot " + (i+1) + "/" + bs.length);
			agents[i] = new BotTest();
			agents[i].bot = bs[i];

			agents[i].agent = af.getAgent(m, bs[i]);
			agents[i].moves = 0;
			panel.addAgent(agents[i].agent);
		}


		// update the UI
		this.agentList = agentList;
		agentList.setListData(agents);
	}	

	public void setDelay(int delayms){
		if(delayms >= 0)
			this.delayms = delayms;
	}

	public void quit(){
		for(int i=0;i<agents.length;i++)
			panel.remAgent(agents[i].agent);
		quit = true;
	}

	public boolean isPaused(){
		return pause;
	}

	public void toggle_pause(){
		pause = !pause;
	}



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
				// move the agent
				agent.agent.move();
				// update ui
				agent.moves++;
			}else{
				this.panel.remAgent(agent.agent);
			}
		}

		panel.repaint();
		return keepRunning;
	}

	public void run(){
		while(iterate() == true){

			//update ui
			this.agentList.repaint();

			try{ Thread.currentThread().sleep(delayms); }catch(java.lang.InterruptedException ie){ }

			// exit without completion
			if( quit ){
				for(int i=0;i<agents.length;i++)
					panel.remAgent(agents[i].agent);
				panel.repaint();
				return;
			} 

			// hang but allow for response from other calls
			while(pause)
				try{ Thread.currentThread().sleep(delayms); }catch(java.lang.InterruptedException ie){}
		}
		isDone = true;
		//update ui
		this.agentList.repaint();
	}
}

