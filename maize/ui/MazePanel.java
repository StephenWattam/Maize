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

	// if everything is dirty also erase background
	private boolean blankBeforePaint = false;

	public MazePanel(Maze maze, MazeTileSet mazeTiles, BotTileSet[] botTileSets){ 

		// Set maze
		this.maze = maze;

		// Populate images	
		this.mazeTiles      = mazeTiles;
		this.botTileSets    = botTileSets;

        // Get original size
        currentSize = getSize();

		// Ensure the first render refreshes everything
		dirtyEverything();
	}

	public MazePanel(MazeTileSet mazeTiles, BotTileSet[] botTileSets){

		// Populate images	
		this.mazeTiles = mazeTiles;
		this.botTileSets = botTileSets;

		// Ensure the first render refreshes everything
		dirtyEverything();
	}

	// Set everything to be rendered next time swing gets around to it.
	public void dirtyEverything(){

		if(maze != null)
			for(int j=0; j<maze.getHeight(); j++){
				synchronized(dirty_tiles){
					for(int i=0; i<maze.getWidth(); i++)
						dirty_tiles.add(new Point(i, j));
				}
			}

		this.blankBeforePaint = true;
	}

    public void resizeCache(){
        // Simply return if the size is the same
        if( currentSize == getSize() && bgBuffer != null && mazeTileCache != null && botTileSetCache != null)
            return;

        // Load the size.
        currentSize = getSize();

        // Rescale tiles from original to avoid lossiness
        mazeTileCache = new MazeTileSet( 
                rescaleImage(currentSize, mazeTiles.space),
                rescaleImage(currentSize, mazeTiles.wall),
                rescaleImage(currentSize, mazeTiles.start),
                rescaleImage(currentSize, mazeTiles.finish));

        // Scale bots from original to avoid lossiness.
        botTileSetCache = new BotTileSet[botTileSets.length];
        for(int i=0; i<botTileSets.length; i++)
            botTileSetCache[i] = new BotTileSet( rescaleImage(currentSize, botTileSets[i].botN) );
        


        /* // Reconstruct the background to that size */
        bgBuffer = new BufferedImage(currentSize.width, currentSize.height, BufferedImage.TYPE_INT_ARGB);
        /* renderBackground( bgBuffer.getGraphics() ); */
    }

    private BufferedImage rescaleImage(Dimension targetSize, BufferedImage img){

        //rescale the image to be done
        float tilex = (float)(targetSize.width / maze.getWidth());
        float tiley = (float)(targetSize.height / maze.getHeight());

        AffineTransform transform = AffineTransform.getScaleInstance(
                (float)tilex / (float)img.getWidth(),  
                (float)tiley / (float)img.getWidth());
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(img, null);
    }

	// Add an agent to the list to render
	public boolean addAgent(Agent a){
		if( agents.indexOf(a) == -1){
			agents.add(a);
			return true;
		}
		return false;
	}

	// Removes an agent from the list to simulate
	public boolean remAgent(Agent a){
		if(agents.indexOf(a) == -1)
			return false;
		agents.remove(a);
		return true;
	}

	// Render what is dirty
	public void paint(Graphics g)
	{

		// One-off filling in of the background
		if(blankBeforePaint){
			g.setColor(Color.WHITE);
			g.fillRect(0,0, this.getWidth(), this.getHeight());
			this.blankBeforePaint = false;
		}

		// If no maze then make sure we render everything when the maze is added
		if(maze != null){
			dirtyEverything();
			update(g);
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

		// Check for maze background section
		if(maze.getEntX() == p.x && maze.getEntY() == p.y)
			img = mazeTileCache.start;
		//renderTile(start, p.x, p.y, bg);
		else if(maze.getExiX() == p.x && maze.getExiY() == p.y)
			img = mazeTileCache.finish;
		//renderTile(finish, p.x, p.y, bg);
		else if( mdata[p.x][p.y] )
			img = mazeTileCache.wall;
		else
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
        resizeCache();

		// Blank if we have been asked to do so
		if(blankBeforePaint){
			g.setColor(Color.WHITE);
			g.fillRect(0,0, this.getWidth(), this.getHeight());
			this.blankBeforePaint = false;
		}

		// Else update the dirty areas
		if(maze != null){
			dirtyAgentAreas();  // render agent
			renderDirtyAreas(this.bgBuffer.getGraphics());
			dirtyAgentAreas();  // render trails
		}
        g.drawImage(this.bgBuffer, 0, 0, this);
	}

	// Sets a given maze
	public void setMaze(Maze m){
		this.maze = m;
		dirtyEverything();
	}

}


