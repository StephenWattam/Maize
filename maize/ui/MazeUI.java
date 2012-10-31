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

import maize.*;
public class MazeUI extends JFrame implements ActionListener{

	private static int DEFAULT_MAZE_WIDTH = 20;
	private static int DEFAULT_MAZE_HEIGHT = 20;

	// state
	private MazeTest mazeTest = null;
	/**The menu bar*/
	private JMenuBar menuBar;
	/**The tab handler which holds all of the panels*/
	private JTabbedPane tabs = new JTabbedPane();

	private MazeTabPanel mazeTab;
	private BotTabPanel botTab;
    private LogTabPanel logTab;

	private MultiTestTabPanel multiTestTab;


	public MazeUI(MazeTest mazeTest)throws IOException{
		super("Maize UI");

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

		//super.setIconImage(new ImageIcon(ICON_PATH).getImage());
		setSize(900, 630);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
        this.setIconImage(MazeUISettingsManager.icon);


		this.mazeTest = mazeTest;
		BotCompilerHelper.compileAndLoadBots(this.mazeTest, MazeUISettingsManager.botPackageName, MazeUISettingsManager.botDirectory);
		constructDefaultMazes();


		//menu
		menuBar = buildMenu();
		this.setJMenuBar(menuBar);

		//tabs
		mazeTab = new MazeTabPanel(mazeTest);
		tabs.add("Manage Mazes", mazeTab);

		botTab = new BotTabPanel(mazeTest);
		tabs.add("Manage Bots", botTab);

		multiTestTab = new MultiTestTabPanel(mazeTest);
		tabs.add("Run Tests", multiTestTab);

        logTab  =   new LogTabPanel(mazeTest);
        Log.addLogListener(logTab);
        tabs.add("Log", logTab);

		setContentPane(tabs);
		setVisible(true);

		updatePanes();
        Log.log("Started Maize main UI");
	}

	// Create a set of default mazes, one for each factory
	private void constructDefaultMazes(){
        Log.log("Constructing default mazes");
		for(MazeFactory mf : this.mazeTest.factories){
			Maze m = mf.getMaze(DEFAULT_MAZE_WIDTH, DEFAULT_MAZE_HEIGHT);
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

		return menuBar;
	}


	/**fired when a component performs an action whilst this class is listening to it.
	  @param Ae the action even generated
	 */
	public void actionPerformed(ActionEvent Ae){
		if(Ae.getActionCommand().equals("exit_all")){
			quit();
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
		}else{
			System.err.println("Unable to find handler for action command: " + Ae.getActionCommand().toString());
		}

		updatePanes();
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
        Log.removeLogListener(logTab);
        Log.log("Goodbye.");
		System.exit(0);
	}

	// Update all of the panes
	public void updatePanes(){
		this.mazeTab.update();
		this.botTab.update();
		this.multiTestTab.update();
	}

}

