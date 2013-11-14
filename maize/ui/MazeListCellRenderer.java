package maize.ui;

import javax.swing.*;
import java.awt.*;


import maize.*;
public class MazeListCellRenderer extends JLabel implements ListCellRenderer{
	public MazeListCellRenderer(){
		setOpaque(true);
	}


	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		// PRint the bot's name
		Maze maze = (Maze)value;


        // Find route length and compute elitism if possible
        String routeLengthStr = null;

        if(maze.getRoute() != null){
            int routeSize = maze.getRoute().size();
            float elitism = (float)routeSize / (float)maze.getTotalPathLength();

            routeLengthStr = String.format("%d][%.3f", routeSize, elitism);
        }else{
            routeLengthStr = "" + maze.getTotalPathLength();
        }
        

        String str = "" + maze.getWidth() + "x" + maze.getHeight() + " " + maze.getName() + "(" + maze.toString() + ")";
            str = "[" + routeLengthStr + "] " + str;

		setText(str);



		if(isSelected)
			setBackground(Color.LIGHT_GRAY);
		else
			setBackground(null);

		return this;
	}	
}


