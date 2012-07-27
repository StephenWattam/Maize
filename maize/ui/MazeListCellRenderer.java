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
		setText(maze.getWidth() + "x" + maze.getHeight() + " " + maze.getName() + "(" + maze.toString() + ")");



		if(isSelected)
			setBackground(Color.LIGHT_GRAY);
		else
			setBackground(null);

		return this;
	}	
}


