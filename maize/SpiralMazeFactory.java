package maize;

import java.awt.*;
/**Draws a spiral maze, randomly rotated. Doesn't fill space perfectly.
 * @author Adam Howard
 * @version 1
 * */
public class SpiralMazeFactory implements MazeFactory
{
	Point start = new Point();
	Point finish = new Point();
	Point builder;
	boolean[][] mazeData;
	boolean penDown = true;
	public Maze getMaze(int width, int height)
	{
		//generate empty maze with walls
		mazeData = new boolean[width][height];
		for(int mx = 0; mx < width; mx++)
		{
			for(int my = 0; my < height; my++)
			{
				//fill in edges; evaluates to true only if this tile is at an edge
				mazeData[mx][my] = (mx == 0 || my == 0 || mx == (width-1) || my == (height-1));
			}
		}
		
		//centre the finish point
		finish.x = width/2;
		finish.y = height/2;
		
		//put the start somewhere nice
		start.x = 1;
		start.y = 1;
		
		boolean spiralDirection = (Math.random() < 0.5);//random flippedness
		//our 'builder bot' which moves around to draw the bricks
		builder = new Point(finish.x, finish.y);
		int currentOrientation = (int)(Math.random() * 4);//random rotation
		move(currentOrientation);//move off the finish point
		//System.err.println("currentOrientation: " + currentOrientation);
		
		for(int steps = 1;
		(steps < mazeData.length-4) || (steps < mazeData[0].length-4); //FIXME: could carry on drawing further out, with a better end condition
		steps++)
		{
			currentOrientation = rotate(spiralDirection, currentOrientation);
			//System.err.println("steps: " + steps);
			//System.err.println("currentOrientation: " + currentOrientation);
			drawSide(steps, Math.abs(currentOrientation));
		}
		
		return new Maze(mazeData, start.x, start.y, finish.x, finish.y);
	}
	//rotates the orientation;
	//more complex than you'd think because the value could go down or up,
	//depending on spiralDirec
	private int rotate(boolean spiralDirec, int orie)
	{
		if(spiralDirec)
		{
			return Math.abs((orie + 1)  % 4);
		}
		else
		{
			int jurgen = orie - 1;
			if(jurgen == -1)
			{
				jurgen = 3;
			}
			return jurgen;
		}
	}
	
	private void drawSide(int moves, int orientation)
	{
		boolean oldPenPos = penDown;
		penDown = true;
		for(int i = 0; i < moves; i++)
		{
			move(orientation);
		}
		penDown = oldPenPos;
	}
	
	private void move(int orien)
	{
		/*NESW
		 *0123*/
		if((orien <= 3) && (orien >= 0))
		{
			switch(orien)
			{
				case Orientation.NORTH:
				if(builder.y > 0)
				{builder.y--;}
				break;
				
				case Orientation.EAST:
				if(builder.x < mazeData.length-2)
				{builder.x++;}
				break;
				
				case Orientation.SOUTH:
				if(builder.y < mazeData[0].length-2)
				{builder.y++;}
				break;
				
				case Orientation.WEST:
				if(builder.x > 0)
				{builder.x--;}
				break;
			}
			if(penDown)
			{
				mazeData[builder.x][builder.y] = true;
			}
		}
		else{return;}
	}
}
