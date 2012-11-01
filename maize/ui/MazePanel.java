package maize.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import maize.*;
import java.awt.geom.*;


public class MazePanel extends Canvas{

	// The maze to show
	private Maze maze = null;

	// Keep lists of stuff to render
	private Vector<Agent> agents = new Vector<Agent>();
	private Vector<Point> dirty_tiles = new Vector<Point>();

	// Images used to render
	private MazeTileSet mazeTiles;
	private BotTileSet[] botTileSets;

    // Caching
    private Dimension currentSize = null;
	private Image bgBuffer = null;
	private MazeTileSet mazeTileCache = null;
	private BotTileSet[] botTileSetCache = null;

	public MazePanel(Maze maze, MazeTileSet mazeTiles, BotTileSet[] botTileSets){ 

		// Set maze
		this.maze = maze;

		// Populate images	
		this.mazeTiles      = mazeTiles;
		this.botTileSets    = botTileSets;

        // Get original size
        currentSize = getSize();
	}

	public MazePanel(MazeTileSet mazeTiles, BotTileSet[] botTileSets){

		// Populate images	
		this.mazeTiles = mazeTiles;
		this.botTileSets = botTileSets;
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
        currentSize = getSize();

        // Rescale tiles from original to avoid lossiness
        this.mazeTileCache = new MazeTileSet(
                mazeTiles.bg,
                rescaleTile(currentSize, mazeTiles.space),
                rescaleTile(currentSize, mazeTiles.wall),
                rescaleTile(currentSize, mazeTiles.start),
                rescaleTile(currentSize, mazeTiles.finish));

        // Scale bots from original to avoid lossiness.
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
        


        /* // Reconstruct the background to that size */
        this.bgBuffer = new BufferedImage(currentSize.width, currentSize.height, BufferedImage.TYPE_INT_ARGB);
        renderBackground( this.bgBuffer.getGraphics() );
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
                (float)targetSize.height / (float)img.getWidth());
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(img, null);
    }


	// Add an agent to the list to render
	public boolean addAgent(Agent a){
		if( agents.indexOf(a) == -1){
            Log.log("Adding agent " + a + " to maze panel "+ this);
			agents.add(a);
			return true;
		}
		return false;
	}

	// Removes an agent from the list to simulate
	public boolean remAgent(Agent a){
		if(agents.indexOf(a) == -1){
            Log.log("Removing agent " + a + " from maze panel "+ this);
			return false;
        }
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
    private void renderBackground(Graphics g){
        // Set bg to a scaled version of the background image
        g.drawImage(rescaleImage(new Dimension(this.getWidth(), this.getHeight()), mazeTiles.bg), 0, 0, this);
        

        // and then render all the tiles
        // TODO: prevent things from rendering the bots onto the background!
        for(int i=0; i < maze.getWidth(); i++){
            for(int j=0; j < maze.getHeight(); j++){
                drawTile(new Point(i, j), g);
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
		Iterator<Agent> ia = agents.iterator();
		Agent current;
		while( ia.hasNext()){
			current = ia.next();
			dirty_tiles.add(new Point( current.getX(), current.getY()));
		}
	}

	// Plots a tile at a given point
	// takes into account agents, etc
	private void drawTile(Point p, Graphics bg)
	{
		//System.out.println("No. Agents on this MazePanel: " + agents.size());
		boolean[][] mdata = maze.getData();
		BufferedImage img = null;

        if(p.x > mdata.length)
            return;

		// Check for maze background section
		if(maze.getEntX() == p.x && maze.getEntY() == p.y)
			img = mazeTileCache.start;
		//renderTile(start, p.x, p.y, bg);
		else if(maze.getExiX() == p.x && maze.getExiY() == p.y)
			img = mazeTileCache.finish;
		//renderTile(finish, p.x, p.y, bg);
		else if( mdata[p.x][p.y] ){
            if( p.y > mdata[p.x].length )
                return;
			img = mazeTileCache.wall;
        }else
			img = mazeTileCache.space;


		// Check each agent
		//hopefully now renders last agent in the vector in blue.
		Iterator<Agent> ia = agents.iterator();
		Agent current;
		int tileSetCount = 0;
		while( ia.hasNext()){
			int set = tileSetCount++ % this.botTileSets.length;
			current = ia.next();

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

		// If we found something, scale and render the tile
		if(img != null)
			renderTile(img, p.x, p.y, bg);
	}

	// Renders a tile with a given buffered image
	private void renderTile(BufferedImage tile, int x, int y, Graphics g){
		g.drawImage(tile, 
                (int)(x*((float)this.getWidth()     / (float)maze.getWidth())), 
				(int)(y*((float)this.getHeight()    / (float)maze.getHeight())), 
                this);
	}

	// Update the render surface, dirties the agent's moved tiles automatically.
	public void update(Graphics g){
        // rebuild the cache if the window has resized
        // FIXME: this shouldn't really be done here...
        resizeCache();

		// Else update the dirty areas
		if(maze != null){
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


