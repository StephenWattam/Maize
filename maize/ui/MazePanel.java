package maize.ui;
import maize.log.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import maize.*;
import java.awt.geom.*;


public class MazePanel extends Canvas{

	// The maze to show
	private Maze maze = null;

    // Render using fast or HQ algorithm?
    private boolean fastRender = false;

	// Keep lists of stuff to render
	private HashMap<Agent, Integer> agents      = new HashMap<Agent, Integer>();
	private Vector<Point> dirty_tiles           = new Vector<Point>();

	// Images used to render
	private MazeTileSet mazeTiles;
	private MazeTileSet mazeTileCache       = null;
	private BotTileSet[] botTileSets;
	private BotTileSet[] botTileSetCache    = null;

    // Caching
    private Dimension currentSize           = null;
	private Image bgBuffer                  = null;


    // Construct with maze
	public MazePanel(Maze maze, MazeTileSet mazeTiles, BotTileSet[] botTileSets, boolean fastRender){ 

		// Set maze
		this.maze = maze;

        // Set fast rendering
        this.fastRender = fastRender;

		// Populate images	
		this.mazeTiles      = mazeTiles;
		this.botTileSets    = botTileSets;

        // Get original size
        currentSize = getSize();
	}

    // Construct with maze
	public MazePanel(Maze maze, MazeTileSet mazeTiles, BotTileSet[] botTileSets){ 
        new MazePanel(maze, mazeTiles, botTileSets, false);
    }

    // Construct without maze
	public MazePanel(MazeTileSet mazeTiles, BotTileSet[] botTileSets){
        new MazePanel(null, mazeTiles, botTileSets);
	}

    // Set fast rendering or not.
    public void setFastRendering(boolean fast){
        // Re-render if different.
        if(this.fastRender != fast){
            this.fastRender = fast;
            resizeCache();
        }
    }

    // Resizes all cache images from the originals.
    public void resizeCache(){
        // Simply return if the size is the same
        if( getSize().equals(currentSize) && 
                this.bgBuffer           != null && 
                this.mazeTileCache      != null && 
                this.botTileSetCache    != null &&
                this.maze               != null)
            return;

        // Load the size.
        this.currentSize = getSize();

        // Rescale tiles from original
        // This also sets the tiles to be original-sized for
        // the rendering of the background image below
        this.mazeTileCache = mazeTiles;

        // Don't render bots when pre-rendering bg image
        this.botTileSetCache = null;

        /* High quality rendering:
         *
         * Render at full size, THEN scale.  Allows contiguous walls to look good. */
        if(!this.fastRender){
            Dimension targetSize = new Dimension( mazeTileCache.space.getWidth()  * this.maze.getWidth(),
                                                  mazeTileCache.space.getHeight() * this.maze.getHeight() );
            BufferedImage newBuffer = new BufferedImage(targetSize.width, targetSize.height, BufferedImage.TYPE_INT_ARGB);
            renderBackground( targetSize, newBuffer.getGraphics() );
            this.bgBuffer = rescaleImage( this.currentSize, newBuffer );
        }


        // Scale the cache
        this.mazeTileCache = new MazeTileSet(
                mazeTiles.bg,
                rescaleTile(currentSize, mazeTiles.space),
                rescaleTile(currentSize, mazeTiles.wall),
                rescaleTile(currentSize, mazeTiles.start),
                rescaleTile(currentSize, mazeTiles.finish),
                rescaleTile(currentSize, mazeTiles.route)
        );

        // Scale the bot tile set
        this.botTileSetCache = new BotTileSet[botTileSets.length];
        for(int i=0; i<botTileSets.length; i++){
            // set, get it to map and rotate 
            this.botTileSetCache[i] = new BotTileSet( botTileSets[i].botN );
            //then adjust for size
            this.botTileSetCache[i].botN = rescaleTile(currentSize, botTileSetCache[i].botN);
            this.botTileSetCache[i].botE = rescaleTile(currentSize, botTileSetCache[i].botE);
            this.botTileSetCache[i].botS = rescaleTile(currentSize, botTileSetCache[i].botS);
            this.botTileSetCache[i].botW = rescaleTile(currentSize, botTileSetCache[i].botW);
        }
        
        /* Low quality rendering.
         *
         * Pre-scale everything, then render onto a small plane.
         * Very very fast, but can have problems with placing things.
         */
        if(this.fastRender){
            this.bgBuffer = new BufferedImage(currentSize.width, currentSize.height, BufferedImage.TYPE_INT_ARGB);
            renderBackground(  currentSize, this.bgBuffer.getGraphics());
        }

    }

    // Rescales a tile to fit the current maze size for a given screen size.
    private BufferedImage rescaleTile(Dimension targetSize, BufferedImage img){
        //rescale the image to be done
        int tilex = (int)(targetSize.width / maze.getWidth());
        int tiley = (int)(targetSize.height / maze.getHeight());
        return rescaleImage(new Dimension(tilex, tiley), img);
    }

    // Arbitrarily scales an image using bilinear filtering
    private BufferedImage rescaleImage(Dimension targetSize, BufferedImage img){
        AffineTransform transform = AffineTransform.getScaleInstance(
                (float)targetSize.width / (float)img.getWidth(),  
                (float)targetSize.height / (float)img.getHeight());
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(img, null);
    }


	// Add an agent to the list to render
	public boolean addAgent(Agent a){
		if( agents.containsKey(a) == false){
            Log.log("Adding agent " + a + " to maze panel "+ this);
			agents.put(a, (agents.size() + 1) % this.botTileSets.length );
			return true;
		}
		return false;
	}

    // Retrieve the bot icons for a given agent.
    public BotTileSet getTileSet(Agent a){
        if( agents.containsKey(a) == false)
            return null;

        return this.botTileSets[agents.get(a)];
    }

    // Retrieve the scaled bot icons for a given agent
    public BotTileSet getScaledTileSet(Agent a){
        if( agents.containsKey(a) == false)
            return null;

        if( this.botTileSetCache == null )
            resizeCache();

        return this.botTileSetCache[agents.get(a)];
    }

	// Removes an agent from the list to simulate
	public boolean remAgent(Agent a){
		if(agents.containsKey(a) == false)
			return false;
        
        Log.log("Removing agent " + a + " from maze panel "+ this);
		agents.remove(a);
		return true;
	}

	// Render what is dirty
	public void paint(Graphics g)
	{
		// If no maze then make sure we render everything when the maze is added
		if(maze == null){
            g.drawImage(rescaleImage(new Dimension(this.getWidth(), this.getHeight()), mazeTiles.bg), 0, 0, this);
            /* g.setColor(Color.BLACK); */
            /* g.drawString("No Maze.", (this.getWidth()/2 - 15), (this.getHeight()/2 - 5)); */
		}else{
            resizeCache();
            g.drawImage(this.bgBuffer, 0, 0, this);
        }
	}


    // Draws the whole of the background, equivalent to dirtying everything
    // then rendering without any bots added
    private void renderBackground(Dimension planeSize, Graphics g){

        // Set bg to a scaled version of the background image
        g.drawImage(rescaleImage(planeSize, mazeTiles.bg), 0, 0, this);
        

        // and then render all the tiles
        for(int i=0; i < maze.getWidth(); i++){
            for(int j=0; j < maze.getHeight(); j++){
                drawTile(new Point(i, j), g, planeSize);
            }
        }	
    }

	// Renders only the dirty bits, keeps rendering quick
	private void renderDirtyAreas( Graphics g ){
		synchronized(dirty_tiles){
			for(Iterator<Point> ip = dirty_tiles.iterator(); ip.hasNext(); )
				drawTile( ip.next(), g);

			dirty_tiles.clear();
		}
	}

	// Makes the agent's trails dirty so they get refreshed
	private void dirtyAgentAreas(){
        Set<Map.Entry<Agent, Integer>> entries = this.agents.entrySet();
        for(Map.Entry<Agent, Integer> e : entries ){
			dirty_tiles.add(new Point( e.getKey().getX(), e.getKey().getY()));
        }
	}


	private void drawTile(Point p, Graphics bg){
        drawTile(p, bg, this.currentSize );
        //new Dimension(this.maze.getWidth(), this.maze.getHeight()) );
    }

	// Plots a tile at a given point
	// takes into account agents, etc
	private void drawTile(Point p, Graphics bg, Dimension planeSize)
	{
		//System.out.println("No. Agents on this MazePanel: " + agents.size());
		BufferedImage img = null;

        if( p.x > maze.getWidth() || p.y > maze.getHeight() )
            return;

		// Check for maze background section
		if(maze.getEntX() == p.x && maze.getEntY() == p.y)
			img = mazeTileCache.start;
		//renderTile(start, p.x, p.y, bg);
		else if(maze.getExiX() == p.x && maze.getExiY() == p.y)
			img = mazeTileCache.finish;
		//renderTile(finish, p.x, p.y, bg);
		else if( maze.getPoint(p.x, p.y) ){
			img = mazeTileCache.wall;
        }else
			img = mazeTileCache.space;

        // Check for any route
        if(maze.getRoute() != null && maze.getIsOnRoute(p)){
            img = mazeTileCache.route;
        }

		// Check each agent
		//hopefully now renders last agent in the vector in blue.
        //
        
        // Don't try to render agents if there is no tile set cache yet
        // this nicely prevents the baking of bot images onto the background
        if(this.botTileSetCache != null){
            Agent current;
            Set<Map.Entry<Agent, Integer>> entries = this.agents.entrySet();
            for(Map.Entry<Agent, Integer> e : entries ){
                current = e.getKey();
                int set = e.getValue();

                if(current.getX() == p.x && current.getY() == p.y)
                {
                    switch(current.getOrientation())
                    {
                        case Orientation.NORTH:
                            img = botTileSetCache[set].botN;
                            break;

                        case Orientation.EAST:
                            img = botTileSetCache[set].botE;
                            break;

                        case Orientation.WEST:
                            img = botTileSetCache[set].botW;
                            break;

                        default:
                            img = botTileSetCache[set].botS;
                    }
                }
            }
        }

		// If we found something, scale and render the tile
		if(img != null)
			renderTile(img, p.x, p.y, bg, planeSize);
	}


	private void renderTile(BufferedImage tile, int x, int y, Graphics g){
        renderTile(tile, x, y, g, this.currentSize);
            //new Dimension(this.getWidth(), this.getHeight()));
    }

	// Renders a tile with a given buffered image
	private void renderTile(BufferedImage tile, int x, int y, Graphics g, Dimension planeSize){
		g.drawImage(tile, 
                (int)(x*((float)planeSize.getWidth()     / (float)maze.getWidth())), 
                /* Computer rendering (0,0) in the top left) */
				(int)(y*((float)planeSize.getHeight()    / (float)maze.getHeight())), 
				/* Euclidean rendering (0,0) in the bottom-left
                 *
                 * this.getHeight() - (int)((y+1)*((float)this.getHeight()    / (float)maze.getHeight())),  */
                this);
	}

	// Update the render surface, dirties the agent's moved tiles automatically.
	public void update(Graphics g){

		// Else update the dirty areas
		if(maze != null){
            // rebuild the cache if the window has resized
            // FIXME: this shouldn't really be done here...
            resizeCache();
            
			dirtyAgentAreas();  // render agent
			renderDirtyAreas(this.bgBuffer.getGraphics());
			dirtyAgentAreas();  // render trails
		}

        /* g.drawImage(this.bgBuffer, 0, 0, this); */
        paint(g);
	}

	// Sets a given maze
	public void setMaze(Maze m){
        Log.log("Setting Maze " + m + " to render on panel " + this);
		this.maze = m;
        this.bgBuffer = null;
	}

}


