package maize.ui;
import java.awt.image.*;

import javax.swing.UIManager;
import java.io.IOException;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import java.util.*;

import org.json.simple.*;
import org.json.simple.parser.*;

// Singleton class to control settings
public class MazeUISettingsManager{

    public static final String VERSION = "0.1.1";

	private static MazeUISettingsManager instance = null;

	// Bot loading
	public static String botDirectory;
	public static String botPackageName;

	// UI Tiles
	public static MazeTileSet mazeTiles;
	public static BotTileSet[] botTileSets;

    // Program icon
    public static BufferedImage icon;


    // Load from JSON
    public static boolean loadConfig(String filename){
        // JSON parser
        JSONParser parser   = new JSONParser();
        JSONObject config   = null; 
        try{
            config = (JSONObject) parser.parse(new FileReader(filename));
        }catch(FileNotFoundException FNFe){
            Log.log("Could not find config file: " + filename);
            Log.logException(FNFe);
            return false;
        }catch(IOException IOe){
            Log.log("There was an error reading the config file.");
            Log.logException(IOe);
            return false;
        }catch(ParseException pe){
            Log.log("Failed to read config file at position: " + pe.getPosition());
            Log.logException(pe);
            return false;
        }

        // Check we loaded something
        if(config == null)
            return false;

        // constuct things 
		MazeTileSet mazeTiles;
		BotTileSet botTiles;
		Vector<BotTileSet> bts = new Vector<BotTileSet>();
		BotTileSet[] botTileSets;
		try{
			// Load the maze tile set
			BufferedImage space	    = ImageIO.read(new File(((JSONObject)config.get("maze")).get("space").toString()));
			BufferedImage wall	    = ImageIO.read(new File(((JSONObject)config.get("maze")).get("wall").toString()));
			BufferedImage start	    = ImageIO.read(new File(((JSONObject)config.get("maze")).get("start").toString()));
			BufferedImage finish	= ImageIO.read(new File(((JSONObject)config.get("maze")).get("finish").toString()));

            // and the icon
            BufferedImage icon      = ImageIO.read(new File(((JSONObject)config.get("ui")).get("icon").toString()));

            // then the maze tilesets
			mazeTiles = new MazeTileSet(space, wall, start, finish);


			// Load a series of bot tile-sets, up to 99
            int count = 0;
			for(int i=1;i<100;i++){
				botTiles = loadBotTiles(((JSONObject)config.get("bot")).get("botImages").toString(), 
                                        ((JSONObject)config.get("bot")).get("botImageExtension").toString(), 
                                        i+"");
				if(botTiles != null) {
                    count ++; 
                    Log.log("Successfully loaded image for bot " + count );
				    bts.add(botTiles);
                }
			}
			botTileSets = (BotTileSet[])bts.toArray(new BotTileSet[bts.size()]);

            // Ensure we have loaded some bots
            if( count == 0 ){
                Log.log("No bot images found!");
                return false;
            }


			// Load a settings manager
			MazeUISettingsManager.botDirectory		= ((JSONObject)config.get("bot")).get("botSource").toString();
			MazeUISettingsManager.botPackageName	= ((JSONObject)config.get("bot")).get("botPackage").toString();
			MazeUISettingsManager.mazeTiles			= mazeTiles;
			MazeUISettingsManager.botTileSets		= botTileSets;
            MazeUISettingsManager.icon              = icon;

        }catch(NullPointerException NPe){
            Log.log("Missing config key.");
            Log.logException(NPe);
            return false;
		}catch(IOException IOe){
			Log.log("Cannot load image resources!");
            Log.logException(IOe);
            return false;
		}

        return true;
    }

    
	// Constructs a BotTileSet from a directory pattern
	private static BotTileSet loadBotTiles(String prefix, String postfix, String number){
		try{
			BufferedImage botN	= ImageIO.read(new File(prefix + "bot" + number + postfix));
			return new BotTileSet(botN);
		}catch(IOException IOE){
			return null;
		}
	}



    // Ensures this remains as a singleton
	public static MazeUISettingsManager getInstance(){
		if(instance == null)
			instance = new MazeUISettingsManager();
		return instance;
	}
}
