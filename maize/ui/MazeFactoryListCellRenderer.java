
package maize.ui;

import javax.swing.*;
import java.awt.*;


import maize.*;
public class MazeFactoryListCellRenderer extends JLabel implements ListCellRenderer{
	public MazeFactoryListCellRenderer(){
		setOpaque(true);
	}


	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		// PRint the bot's name
		MazeFactory mf = (MazeFactory)value;

		setText(mf.getName());

		if(isSelected)
			setBackground(Color.LIGHT_GRAY);
		else
			setBackground(null);

		return this;
	}	
}


