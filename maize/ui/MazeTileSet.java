package maize.ui;
import maize.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.*;

public class MazeTileSet{

	private BufferedImage[] space = null;
	private BufferedImage[] wall = null;
	private BufferedImage start = null;
	private BufferedImage finish = null;
    private BufferedImage[] bg = null;

    private int width = 1;
    private int height = 1;


	public MazeTileSet(BufferedImage[] bg, BufferedImage[] space, BufferedImage[] wall, BufferedImage start, BufferedImage finish){
        this.bg     = bg;
		this.space  = space;
		this.wall   = wall;
		this.start  = start;
		this.finish = finish;

        width = (int)space[0].getWidth();
        height = (int)space[0].getHeight();

        System.out.println( "New tile set [spaces:" +space.length+ ", walls:" +wall.length+ ", backgrounds:" +bg.length+ "]" );
	}

    public MazeTileSet rescale( Dimension newSize, Maze maze ) {
        MazeTileSet newSet = new MazeTileSet( this.bg,
                                              this.space,
                                              this.wall,
                                              this.start,
                                              this.finish );

        newSet.start = rescaleTile( newSize, maze, newSet.start );
        newSet.finish = rescaleTile( newSize, maze, newSet.finish );

        // Spaces
        for( int idx = 0; idx<newSet.space.length; idx++ )
            newSet.space[idx] = rescaleTile( newSize, maze, newSet.space[idx] );

        // Walls
        for( int idx = 0; idx<newSet.wall.length; idx++ )
            newSet.wall[idx] = rescaleTile( newSize, maze, newSet.wall[idx] );

        // Backgrounds
        for( int idx = 0; idx<newSet.bg.length; idx++ )
            newSet.bg[idx] = rescaleTile( newSize, maze, newSet.bg[idx] );

        newSet.width  = (int)newSize.getWidth();
        newSet.height = (int)newSize.getHeight();

        return newSet;
    }
    
    // Rescales a tile to fit the current maze size for a given screen size.
    private BufferedImage rescaleTile(Dimension targetSize, Maze maze, BufferedImage img){
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

	// Tile dimensions.
	public int getWidth() {
		return space[0].getWidth(); // TODO: make this return mean of filesizes, or perhaps avg of wall, space size?
	}
	public int getHeight() {
		return space[0].getHeight();
	}

    public BufferedImage getSpace() {
        return space[(int)(Math.random()*space.length)];
    }

    public BufferedImage getWall() {
        return wall[(int)(Math.random()*wall.length)];
    }

    public BufferedImage getStart() {
        return start;
    }

    public BufferedImage getFinish() {
        return finish;
    }

    public BufferedImage getBackground() {
        return bg[(int)(Math.random()*bg.length)];
    }


}

