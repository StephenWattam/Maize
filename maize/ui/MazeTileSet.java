package maize.ui;
import java.awt.image.*;

public class MazeTileSet{

	public BufferedImage space;
	public BufferedImage wall;
	public BufferedImage start;
	public BufferedImage finish;
    public BufferedImage bg;

	public MazeTileSet(BufferedImage bg, BufferedImage space, BufferedImage wall, BufferedImage start, BufferedImage finish){
        this.bg     = bg;
		this.space  = space;
		this.wall   = wall;
		this.start  = start;
		this.finish = finish;
	}

	// Tile dimensions.
	public int getWidth(){
		return space.getWidth(); // TODO: make this return mean of filesizes, or perhaps avg of wall, space size?
	}
	public int getHeight(){
		return space.getHeight();
	}

}

