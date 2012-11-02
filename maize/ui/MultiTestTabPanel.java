package maize.ui;

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


import maize.*;
public class MultiTestTabPanel extends TabPanel implements ActionListener, ChangeListener, ListSelectionListener, MouseWheelListener{

    private static final int SPEED_SCROLL_AMOUNT            = 1;
	private static final int MAX_DELAY                      = 500;
	private static final int MAZE_DISPLAY_MAX_NAME_LENGTH   = 25;
	private static final String BOT_NAME_PLACEHOLDER	    = "No bot";
	private static final String MAZE_NAME_PLACEHOLDER	    = "No maze";
	private static final String MOVE_COUNT_PLACEHOLDER	    = "No moves yet";

	// Lists of potential bots and mazes
	private JList botList;
	private ListSelectionModel botLSM;
	private JList mazeList;


	// Maze UI stuff
	private MazePanel mazePanel;

	// Controls
	private JButton startButton                             = new JButton("Start");
	private JButton pauseButton                             = new JButton("Pause/play");
	private JButton stopButton                              = new JButton("Stop");
	private JButton refreshButton                           = new JButton("New Test");
	private JSlider speedSlider                             = new JSlider(JSlider.HORIZONTAL, 0, MAX_DELAY, MAX_DELAY/2);
	private JLabel mazeNameLabel                            = new JLabel(MAZE_NAME_PLACEHOLDER);
	private JList testList;



	// Current running things.
	// Holds selected bots
	private Vector<Bot> selectedBots                        = new Vector<Bot>();
	private Maze maze                                       = null;
	private TestThread test                                 = null; 


    public MultiTestTabPanel(MazeTest mazeTest, JTabbedPane tabContainer, String name){
        super(mazeTest, tabContainer, name);
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());

		// speed slider
		speedSlider.setSize(new Dimension(200, 10));
		speedSlider.addChangeListener(this);
        speedSlider.addMouseWheelListener(this);


		mazeNameLabel.setMaximumSize(new Dimension(80, 20));

		// button
		pauseButton.addActionListener(this);
		startButton.addActionListener(this);
		stopButton.addActionListener(this);
		refreshButton.addActionListener(this);

		// Bot List
		botList = new JList(mazeTest.bots);
		botList.setCellRenderer(new BotListCellRenderer());
		botLSM = botList.getSelectionModel();
		botList.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		botList.getSelectionModel().addListSelectionListener(this);
		JScrollPane botListPanel = new JScrollPane(botList);
		botListPanel.setMinimumSize(new Dimension(80, 80));

		// Maze List
		mazeList = new JList(mazeTest.mazes);
		mazeList.setCellRenderer(new MazeListCellRenderer());
		mazeList.addListSelectionListener(this);
		mazeList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane mazeListPanel = new JScrollPane(mazeList);
		mazeListPanel.setMinimumSize(new Dimension(80, 80));

		// Test panel
		testList = new JList();
		testList.setCellRenderer(new TestListCellRenderer());
		testList.setBackground(null);
		JScrollPane testListPanel = new JScrollPane(testList);
		testListPanel.setMinimumSize(new Dimension(160, 150));
		testListPanel.setBackground(null);
		//testListPanel.setViewportBorder(null);


		// maze panel
		mazePanel		= new MazePanel(MazeUISettingsManager.mazeTiles, MazeUISettingsManager.botTileSets);
		mazePanel.setMinimumSize(new Dimension(500,500));




		gbc.fill        = GridBagConstraints.BOTH;
		gbc.anchor      = GridBagConstraints.LINE_START;
        gbc.insets      = new Insets(10,10,10,10);
		// panel
		gbc.gridheight  = 7;
		gbc.gridx       = 0;
		gbc.gridy       = 0;
		gbc.ipadx       = 0;
		gbc.ipady       = 0;
        gbc.weightx     = 1;
        gbc.weighty     = 1;
		this.add(mazePanel,gbc);

		// slider
        gbc.insets      = new Insets(0,0,0,10);
		gbc.fill        = GridBagConstraints.HORIZONTAL;
		gbc.gridheight  = 1;
		gbc.gridwidth   = 2;
		gbc.gridx       = 1;
        gbc.weightx     = 0;
        gbc.weighty     = 0;
		this.add(new JLabel("Speed: "),gbc);
		gbc.gridy       = 1;
		gbc.gridx       = 1;
		this.add(speedSlider,gbc);

		// maze name
        gbc.insets      = new Insets(10,0,0,10);
		gbc.fill        = GridBagConstraints.VERTICAL;
		gbc.gridwidth   = 1;
		gbc.gridheight  = 1;
		gbc.gridx       = 1;
		gbc.gridy       = 2;
		this.add(new JLabel("Maze: "),gbc);
        gbc.insets      = new Insets(0,0,0,10);
		gbc.gridx       = 2;
		this.add(mazeNameLabel,gbc);

        // list panel
		gbc.anchor      = GridBagConstraints.CENTER;
		gbc.fill        = GridBagConstraints.BOTH;
        gbc.weighty     = 0.5;
        gbc.insets      = new Insets(10,10,0,10);
		gbc.gridwidth   = 2;
		gbc.gridheight  = 1;
		gbc.gridx       = 1;
		gbc.gridy       = 3;
		this.add(testListPanel, gbc);

		// First row of buttons 
        gbc.insets      = new Insets(10,10,0,10);
        gbc.weightx     = 0;
        gbc.weighty     = 0;
		gbc.gridwidth   = 1;
		gbc.gridheight  = 1;
		gbc.gridx       = 1;
		gbc.gridy       = 4;
		this.add(refreshButton,gbc);
        gbc.insets      = new Insets(10,0,0,10);
		gbc.gridx       = 2;
		this.add(startButton,gbc);

		// Second row of buttons 
		gbc.gridheight  = 1;
		gbc.gridx       = 1;
		gbc.gridy       = 5;
        gbc.insets      = new Insets(10,10,0,10);
		this.add(pauseButton,gbc);
		gbc.gridx       = 2;
        gbc.insets      = new Insets(10,0,0,10);
		this.add(stopButton,gbc);

		// Bot list
		gbc.gridwidth   = 1;
		gbc.gridheight  = 1;
		gbc.gridx       = 1;
		gbc.gridy       = 6;
		gbc.ipady       = 90;
		gbc.ipadx       = 80;
        gbc.insets      = new Insets(10,10,10,10);
		this.add(botListPanel, gbc);
		// Maze list
		gbc.gridx       = 2;
        gbc.insets      = new Insets(10,0,10,10);
		this.add(mazeListPanel, gbc);



		setVisible(true);
	}

	public void actionPerformed(ActionEvent Ae){
		if(Ae.getSource() == refreshButton){
			reset();
			mazePanel.repaint();
		}else if(Ae.getSource() == startButton){
			start();
		}else if(Ae.getSource() == stopButton){
			stop();
		}else if(Ae.getSource() == pauseButton){
			pause();
		}
	}

	// Fires when the user selects something in the list
	public void valueChanged(ListSelectionEvent LSe){
		if(LSe.getValueIsAdjusting()) return;

		if(LSe.getSource() == this.botLSM){

			ListSelectionModel lsm = (ListSelectionModel)LSe.getSource();
			if(lsm.isSelectionEmpty()){
				System.out.println("Selection is empty.");
			}else{

				int minI = lsm.getMinSelectionIndex();
				int maxI = lsm.getMaxSelectionIndex();
				this.selectedBots.clear();

				for(int i=minI; i<=maxI;  i++){
					if(lsm.isSelectedIndex(i)){
						//System.out.println("Bot" + i + " selected: " + this.mazeTest.bots.get(i));
						this.selectedBots.add(this.mazeTest.bots.get(i));
					}
				}
			}
		}else if(LSe.getSource() == this.mazeList){
		}	

	}

	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		if (!source.getValueIsAdjusting()) {

			int speedval = (int)source.getValue();
			if(this.test != null){
				//System.out.println("Changing delay to " + (MAX_DELAY-speedval));
				this.test.setDelay( MAX_DELAY - speedval);
			}
		}
	}

    public void mouseWheelMoved(MouseWheelEvent e){
        int notches = e.getWheelRotation();
        int speed = this.speedSlider.getValue();
        BoundedRangeModel model = this.speedSlider.getModel();
            
        if(notches < 0){
            // up, decrease speed
            speed = speed - SPEED_SCROLL_AMOUNT;
            model.setValue( Math.max( speed, model.getMinimum() ) );
        }else{
            //down, increase speed
            speed = speed + SPEED_SCROLL_AMOUNT;
            model.setValue( Math.min( speed, model.getMaximum() ) );
        }

    }


	private void stop(){
		if(test == null){
			JOptionPane.showMessageDialog(this, "No test is running.");
			return;
		}

        Log.log("Stopping test.");
		test.quit();
		this.test = null;
		mazePanel.repaint();
	}

	private void pause(){
		if(test == null){
			JOptionPane.showMessageDialog(this, "No test is running.");
			return;
		}

        Log.log("Pausing test.");
		this.test.toggle_pause();
	}

	private void start(){
		//System.out.println("maze: " + maze + ", bot size: " + this.selectedBots.size() + ", test: " + test);
		if(this.test == null){
			JOptionPane.showMessageDialog(this, "You must first select a maze and some bots and click \"New Test\".");
			return;
		}else if(this.test.isDone){
			this.test.quit();
			loadMazeFromUI();
			newTest();
		}else if(this.test.getState() != Thread.State.NEW){
			JOptionPane.showMessageDialog(this, "Please stop the current test and create a new one.");
			return;
		}

		//this.test = new TestThread(maze, bot, mazePanel, MAX_DELAY - speedSlider.getValue());
        Log.log("Starting maze test: maze: " + maze + ", bots: " + this.selectedBots.size());
		this.test.start();
	}

	public void reset(){
		loadMazeFromUI();

		// Ensure we have the data to start doing things
		if(this.maze == null || this.selectedBots.size() == 0){
			JOptionPane.showMessageDialog(this, "You must first select a maze and some bots and stop any running test.");
			return;
		}

		newTest();

	}

	private void loadMazeFromUI(){
		//System.out.println("maze: " + maze + ", bot size: " + this.selectedBots.size() + ", test: " + test);
		// New test if one is running
		if(this.test != null && this.test.isDone == false){
			this.test.quit();
		}

		/* ("Maze selected: " + this.mazeList.getSelectedValue()); */
		this.maze = (Maze)this.mazeList.getSelectedValue();
		if(this.maze == null){
			setMazeName(MAZE_NAME_PLACEHOLDER);
		}else{
			setMazeName(maze.getName());
		}
		this.mazePanel.setMaze(this.maze);
		this.mazePanel.repaint();
		
	}	

	private void newTest(){
		if(this.selectedBots.size() == 0)
			return;
			
        Log.log("Creating new test.");
		// Create a test thread
		Bot[] bots = (Bot[])this.selectedBots.toArray(new Bot[this.selectedBots.size()]);
		this.test = new TestThread(this.maze, bots, mazePanel, MAX_DELAY - speedSlider.getValue(), this.testList);
	}

	//called to regtresjh state changes
	public void update(){
		if(maze == null){
			setMazeName(MAZE_NAME_PLACEHOLDER);
		}else{
			setMazeName(maze.getName());
			mazePanel.setMaze(maze);
		}


		// Reload from model
		botList.setListData(this.mazeTest.bots);
		mazeList.setListData(this.mazeTest.mazes);
	}

	
	private void setMazeName(String name){
		if(name.length() > MAZE_DISPLAY_MAX_NAME_LENGTH){
			mazeNameLabel.setText(name.substring(0, MAZE_DISPLAY_MAX_NAME_LENGTH-3) + "...");
		}else{
			mazeNameLabel.setText(name);
		}
	}


}


