import maize.*;
import maize.ui.*;

import javax.swing.UIManager;
import java.io.IOException;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import java.util.*;
public class RunMazeUI{


	private static String BOT_DIRECTORY         = "./bots";
	private static String BOT_PACKAGE_NAME      = "bots";
	private static String BOT_IMG_DIRECTORY     = "imgres/bots/";
	private static String BOT_IMG_EXT           = ".png";

    // PRogram icon
    private static String ICON                  = "imgres/icorn.gif";

	private static final String SPACE	= "imgres/space.png";
	private static final String WALL	= "imgres/wall.png";
	private static final String START	= "imgres/start.png";
	private static final String FINISH	= "imgres/finish.png";

	// Constructs a BotTileSet from a directory pattern
	public static BotTileSet loadBotTiles(String prefix, String postfix, String number){
		try{
			BufferedImage botN	= ImageIO.read(new File(prefix + "bot" + number + postfix));
			return new BotTileSet(botN);
		}catch(IOException IOE){
			return null;
		}
	}

	public static void main(String[] args){

		// For windows, try to acquire native L+F
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
		}catch(Exception e) {}

		// For linux, attempt to access GTK.  Falls through if on windows,
		try{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		}catch(Exception e){}


		// Construct a series of maze factories
		MazeTest mt = new MazeTest();
		mt.factories.add( new FullDFSMazeFactory());
		mt.factories.add( new CircleMazeFactory());
		mt.factories.add( new ScatterMazeFactory());
		mt.factories.add( new RandomScatterMazeFactory());
		mt.factories.add( new LineMazeFactory());
		mt.factories.add( new EmptyMazeFactory());
		//mt.factories.add( new ());


		MazeTileSet mazeTiles;
		BotTileSet botTiles;
		Vector<BotTileSet> bts = new Vector<BotTileSet>();
		BotTileSet[] botTileSets;
		try{
			// Load the maze tile set
			BufferedImage space	    = ImageIO.read(new File(SPACE));
			BufferedImage wall	    = ImageIO.read(new File(WALL));
			BufferedImage start	    = ImageIO.read(new File(START));
			BufferedImage finish	= ImageIO.read(new File(FINISH));

            // and the icon
            BufferedImage icon      = ImageIO.read(new File(ICON));

            // then the maze tilesets
			mazeTiles = new MazeTileSet(space, wall, start, finish);


			// Load a series of bot tile-sets, up to 99
			for(int i=1;i<100;i++){
				botTiles = loadBotTiles(BOT_IMG_DIRECTORY, BOT_IMG_EXT, i+"");
				if(botTiles == null) break;
				System.out.println("Loaded image for bot " + i );
				bts.add(botTiles);
			}
			botTileSets = (BotTileSet[])bts.toArray(new BotTileSet[bts.size()]);


			// Load a settings manager
			MazeUISettingsManager.botDirectory		= BOT_DIRECTORY;
			MazeUISettingsManager.botPackageName	= BOT_PACKAGE_NAME;
			MazeUISettingsManager.mazeTiles			= mazeTiles;
			MazeUISettingsManager.botTileSets		= botTileSets;
            MazeUISettingsManager.icon              = icon;

		}catch(IOException IOe){
			System.err.println("Cannot load image resources!");
			System.exit(1);
		}


		// Launch the UI itself.
		try{
			new MazeUI(mt);
		}catch(IOException IOe){
			System.err.println("Could not load some resources!");
		}
	}	
}
