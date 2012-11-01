package maize.ui;
import javax.swing.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.swing.event.*;
import java.awt.geom.*;
import javax.imageio.*;
import javax.swing.filechooser.*;
import java.security.Permission;
import java.util.*;

import maize.*;
public class MazeUI extends JFrame implements ActionListener, WindowListener{

	// state
	private MazeTest            mazeTest        = null;
	/**The menu bar*/
	private JMenuBar            menuBar;
	/**The tab handler which holds all of the panels*/
	private JTabbedPane         tabs            = new JTabbedPane();


    // Panels, and a list of them
    private Vector<TabPanel>    panels          = new Vector<TabPanel>();
	private MazeTabPanel        mazeTab;
	private BotTabPanel         botTab;
	private MultiTestTabPanel   multiTestTab;
    private LogTabPanel         logTab;


	public MazeUI(MazeTest mazeTest){
		super("Maize UI");
        Log.log("Starting Maize UI...");

/*		// Initialize our security manager nice and early
		System.setSecurityManager(new SecurityManager ()
		{
			public void checkPermission(Permission perm)
			{
				boolean deny = false;
				Class[] classContext = getClassContext();
				Class botClass = null;
				try {
					botClass = Class.forName("Maize.Bot");
				} catch (ClassCircularityError e) {
					return;
				} catch (NoClassDefFoundError e) {
					return;
				} catch (ClassNotFoundException e) {
					return;
				}
				for (int i = 0; i < classContext.length; i++)
				{
					if (classContext[i].isAssignableFrom(botClass))
					{
						deny = true;
						break;
					}
				}
				if (deny)
				{
					throw new SecurityException("Stop it.");
				}
			}
		});*/

		/* setSize(MazeUISettingsManager.uiWidth, MazeUISettingsManager.uiHeight); */
		setPreferredSize(new Dimension(MazeUISettingsManager.uiWidth, MazeUISettingsManager.uiHeight));
		setMinimumSize(new Dimension(MazeUISettingsManager.uiMinWidth, MazeUISettingsManager.uiMinHeight));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
        this.setIconImage(MazeUISettingsManager.icon);
        this.addWindowListener(this);


		this.mazeTest = mazeTest;
		BotCompilerHelper.compileAndLoadBots(this.mazeTest, MazeUISettingsManager.botPackageName, MazeUISettingsManager.botDirectory);
		constructDefaultMazes();


		//menu
		menuBar = buildMenu();
		this.setJMenuBar(menuBar);



		//tabs
		mazeTab         = new MazeTabPanel      (mazeTest, tabs, "Manage Mazes");
		botTab          = new BotTabPanel       (mazeTest, tabs, "Manage Bots");
		multiTestTab    = new MultiTestTabPanel (mazeTest, tabs, "Run Tests");
        logTab          = new LogTabPanel       (mazeTest, tabs, "Log");



        // List all panels for easy use later
        panels.add(mazeTab);
        panels.add(botTab);
        panels.add(multiTestTab);
        panels.add(logTab);

        // Attach the log
        Log.addLogListener(logTab);

		updatePanes();

        // Set part of the window and make us visible
		setContentPane(tabs);
        this.pack();
		setVisible(true);

        Log.log("Maize UI running.");
	}

	// Create a set of default mazes, one for each factory
	private void constructDefaultMazes(){
        Log.log("Constructing default mazes");
		for(MazeFactory mf : this.mazeTest.factories){
			Maze m = mf.getMaze(MazeUISettingsManager.defaultMazeWidth, MazeUISettingsManager.defaultMazeHeight);
			m.setName("Default " + mf.getClass().getName());
			mazeTest.mazes.add( m );
		}
	}

	// Builds a single menu item
	private JMenuItem buildMenuItem(String label, String actionCommand){
		JMenuItem menuItem = new JMenuItem(label);
		menuItem.setActionCommand(actionCommand);
		menuItem.addActionListener(this);
		return menuItem;
	}

	// Builds the whole drop-down menu
	private JMenuBar buildMenu(){
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		/*fileMenu.add(buildMenuItem("Analyse","analyse"));*/
		fileMenu.add(buildMenuItem("Exit","exit_all"));
		menuBar.add(fileMenu);


		JMenu botMenu = new JMenu("Bots");
		botMenu.add(buildMenuItem("Reload all bots (" + MazeUISettingsManager.botDirectory + ")","reload_bots"));
		botMenu.add(buildMenuItem("Compile (" + MazeUISettingsManager.botDirectory + ")","compile_bot"));
		botMenu.add(buildMenuItem("Instantiate...","inst_bot_choose"));
		botMenu.add(buildMenuItem("Instantiate (advanced)...","inst_bot"));
		botMenu.add(buildMenuItem("Load...","load_bot"));
		menuBar.add(botMenu);

		JMenu mazeMenu = new JMenu("Mazes");
		mazeMenu.add(buildMenuItem("New...","new_maze"));
		mazeMenu.add(buildMenuItem("Load...","load_maze"));
		menuBar.add(mazeMenu);
		
        JMenu tabMenu = new JMenu("Panels");
		tabMenu.add(buildMenuItem("Attach All","attach_all"));
		tabMenu.add(buildMenuItem("Detach All","detach_all"));
		menuBar.add(tabMenu);

        // RHS
        menuBar.add(Box.createHorizontalGlue());

		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(buildMenuItem("About...","about"));
		menuBar.add(helpMenu);

		return menuBar;
	}


	/**fired when a component performs an action whilst this class is listening to it.
	  @param Ae the action even generated
	 */
	public void actionPerformed(ActionEvent Ae){
		if(Ae.getActionCommand().equals("exit_all")){
            this.dispose();
		}else if(Ae.getActionCommand().equals("new_maze")){
			new NewMazeDialog(mazeTest, this);
		}else if(Ae.getActionCommand().equals("load_maze")){
			loadMaze();
		}else if(Ae.getActionCommand().equals("load_bot")){
			loadBot();
		}else if(Ae.getActionCommand().equals("inst_bot")){
			new NewBotDialog(mazeTest, this, MazeUISettingsManager.botPackageName);
		}else if(Ae.getActionCommand().equals("inst_bot_choose")){
			compileBot();
		}else if(Ae.getActionCommand().equals("compile_bot")){
			BotCompilerHelper.compileAllBots(MazeUISettingsManager.botDirectory); 
		}else if(Ae.getActionCommand().equals("reload_bots")){
			this.mazeTest.bots.clear();
			BotCompilerHelper.compileAndLoadBots(this.mazeTest, MazeUISettingsManager.botPackageName, MazeUISettingsManager.botDirectory);
		}else if(Ae.getActionCommand().equals("about")){
            helpAbout();
		}else if(Ae.getActionCommand().equals("attach_all")){
            attachTabs();
		}else if(Ae.getActionCommand().equals("detach_all")){
            detachTabs();
		}else{
			Log.log("Unable to find handler for action command: " + Ae.getActionCommand().toString());
		}

		updatePanes();
	}

    private void helpAbout(){
        new AboutDialog(mazeTest, this);
    }

	// Load a maze from a serialised file
	private void loadMaze(){
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		//fileChooser.setFileFilter(new ImageFileFilter());
		if(fileChooser.showOpenDialog(this) == 0){
			try{
                Log.log("Loading maze from " + fileChooser.getSelectedFile());
				Maze m = (Maze)ClassSerializer.load(fileChooser.getSelectedFile());
				mazeTest.mazes.add(m);
				updatePanes();
			}catch(Exception e){
				JOptionPane.showMessageDialog(this, "Error loading maze.");
                Log.log("Error loading maze.");
                Log.logException(e);
			}
		}
	}

	// Load a bot from a serialised file
	private void loadBot(){
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		//fileChooser.setFileFilter(new ImageFileFilter());
		if(fileChooser.showOpenDialog(this) == 0){
			try{
                Log.log("Loading bot from " + fileChooser.getSelectedFile());
				Bot b = (Bot)ClassSerializer.load(fileChooser.getSelectedFile());
				mazeTest.bots.add(b);
				updatePanes();
			}catch(Exception e){
				JOptionPane.showMessageDialog(this, "Error loading bot.");
                Log.log("Error loading bot.");
                Log.logException(e);
			}
		}
	}


	private void compileBot(){
		final JFileChooser fileChooser = new JFileChooser(MazeUISettingsManager.botDirectory);
		fileChooser.setMultiSelectionEnabled(false);

		// Filter all .java files from the filename
		javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter(){
			public boolean accept(File file){
				String name = file.getName();
				return file.canRead() && name.endsWith(".java") && !name.startsWith(".");
			}

			public String getDescription(){
				return "Java source files only";
			}
		};

		fileChooser.setFileFilter(filter);
		if(fileChooser.showOpenDialog(this) == 0){
			try{
				String filename = fileChooser.getSelectedFile().getName();
				//System.out.println("DEBUG: " + filename);
                Log.log("Compiling bot from file: " + filename);
				if(BotCompilerHelper.compile(MazeUISettingsManager.botDirectory + java.io.File.separator + filename)){
					mazeTest.bots.add(BotCompilerHelper.loadBot( MazeUISettingsManager.botPackageName + "." + 
								BotCompilerHelper.classNameFromBaseName(filename)));
					updatePanes();
				}
			}catch(Exception e){
				JOptionPane.showMessageDialog(this, "Error loading bot.");
                Log.log("Error loading bot.");
                Log.logException(e);
			}
		}

	}



	// Quit
	private void quit(){
        dispose();
        Log.removeLogListener(logTab);
        Log.log("Goodbye.");
	}

	// Update all of the panes
	public void updatePanes(){
		this.mazeTab.update();
		this.botTab.update();
		this.multiTestTab.update();
	}

    private void attachTabs(){
        for(TabPanel tp : panels){
            if(!tp.isAttached())
                tp.attach();
        }
    }

    private void detachTabs(){
        for(TabPanel tp : panels){
            if(tp.isAttached())
                tp.detach();
        }
    }

    // WindowListener
    public void windowClosing(WindowEvent e) { /* displayMessage("Window closing", e); */ 
        quit();
    }
    public void windowClosed(WindowEvent e) {/* displayMessage("Window closed", e); */}
    public void windowOpened(WindowEvent e) { /* displayMessage("Window opened", e); */ }
    public void windowIconified(WindowEvent e) { /* displayMessage("Window iconified", e); */ }
    public void windowDeiconified(WindowEvent e) { /* displayMessage("Window deiconified", e); */ }
    public void windowActivated(WindowEvent e) { /* displayMessage("Window activated", e); */ }
    public void windowDeactivated(WindowEvent e) {/* displayMessage("Window deactivated", e); */ }


}

