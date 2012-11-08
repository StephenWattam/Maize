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

    public static final String VERSION = "0.1.2";

	private static MazeUISettingsManager instance = null;

    // How long bots can work before being timed out
    public static int botWorkTimeout = 10000;

	// Bot loading
	public static String botDirectory;
	public static String botPackageName;

	// UI Tiles
	public static MazeTileSet mazeTiles;
	public static BotTileSet[] botTileSets;

    // Program icon
    public static BufferedImage icon;

    public static int uiWidth       = 900;
    public static int uiHeight      = 630;
    public static int uiMinWidth    = 500;
    public static int uiMinHeight   = 400;

    // Attach/detach tabs
    public static BufferedImage attachIcon = null;
    public static BufferedImage detachIcon = null;

	public static int          defaultMazeWidth      = 20;
	public static int          defaultMazeHeight     = 20;

    public static int           logScrollbackLimit  = 1000;

    // SecurityManager stuff
    public static boolean   smEnabled = true;
    public static long      smLogLevel = 0;
    public static boolean   smAllowThreading = false;
    public static JSONArray smBlackList = null;
    public static JSONArray smDangerList = null;

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
            String mazeTilePrefix   = ((JSONObject)config.get("maze")).get("tileSet").toString();
            System.out.println( "Using " +mazeTilePrefix+ " as a tile source" );
            File tileSet = new File( mazeTilePrefix );

            ArrayList<BufferedImage> spaceTiles = new ArrayList<BufferedImage>();
            ArrayList<BufferedImage> wallTiles = new ArrayList<BufferedImage>();
            ArrayList<BufferedImage> bgTiles = new ArrayList<BufferedImage>();

            for( File tile : tileSet.listFiles() ) {
                if( tile.getName().endsWith( ".png" ) ) {

                    // Is it a space?
                    if( tile.getName().contains( "space" ) )
                        spaceTiles.add( ImageIO.read(tile) );

                    // Is it a wall?
                    if( tile.getName().contains( "wall" ) )
                        wallTiles.add( ImageIO.read(tile) );

                    // Is is a background?
                    if( tile.getName().contains( "bg" ) )
                        bgTiles.add( ImageIO.read(tile) );
                }
            }

			BufferedImage start	    = ImageIO.read(new File(mazeTilePrefix + "/" + "start.png"));
			BufferedImage finish	= ImageIO.read(new File(mazeTilePrefix + "/" + "finish.png"));
            // then the maze tilesets
			mazeTiles = new MazeTileSet( bgTiles.toArray( new BufferedImage[1] ),
                                         spaceTiles.toArray( new BufferedImage[1] ),
                                         wallTiles.toArray( new BufferedImage[1] ),
                                         start,
                                         finish);


			// Load a series of bot tile-sets, up to 99
            int count = 0;
			for(int i=1;i<100;i++){
				botTiles = loadBotTiles(mazeTilePrefix + "/" + "bots/", ".png", i+"");
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
            MazeUISettingsManager.icon              = ImageIO.read(new File(((JSONObject)config.get("ui")).get("icon").toString()));

	        MazeUISettingsManager.defaultMazeWidth  = Integer.parseInt(((JSONObject)config.get("ui")).get("defaultMazeWidth").toString());
	        MazeUISettingsManager.defaultMazeHeight  = Integer.parseInt(((JSONObject)config.get("ui")).get("defaultMazeHeight").toString());
	        
            MazeUISettingsManager.logScrollbackLimit  = Integer.parseInt(((JSONObject)config.get("ui")).get("logScrollback").toString());

            MazeUISettingsManager.botWorkTimeout  = Integer.parseInt(((JSONObject)config.get("ui")).get("botTimeout").toString());

			MazeUISettingsManager.attachIcon = ImageIO.read(new File(((JSONObject)config.get("ui")).get("attachIcon").toString()));
			MazeUISettingsManager.detachIcon = ImageIO.read(new File(((JSONObject)config.get("ui")).get("detachIcon").toString()));

            // SecurityManager flags/settings
            JSONObject security = (JSONObject)config.get( "security" );
            if( security != null )
            {
                MazeUISettingsManager.smEnabled        = (Boolean)(security.get( "enabled" ));
                MazeUISettingsManager.smLogLevel       = (Long)(security.get( "logLevel" ));
                MazeUISettingsManager.smAllowThreading = (Boolean)(security.get( "allowThreading" ));

                Object blackList = security.get( "blackList" );
                if( blackList != null && blackList instanceof JSONArray )
                    MazeUISettingsManager.smBlackList = (JSONArray)blackList;

                Object dangerList = security.get( "dangerList" );
                if( dangerList != null && dangerList instanceof JSONArray )
                    MazeUISettingsManager.smDangerList = (JSONArray)dangerList;
            }

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
