package maize.ui;
import java.awt.image.*;

public class MazeTileSet{

	public BufferedImage space;
	public BufferedImage wall;
	public BufferedImage start;
	public BufferedImage finish;

	public MazeTileSet(BufferedImage space, BufferedImage wall, BufferedImage start, BufferedImage finish){
		this.space = space;
		this.wall = wall;
		this.start = start;
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

