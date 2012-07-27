package maize.ui;

import javax.swing.*;
import java.awt.*;


import maize.*;
public class BotListCellRenderer extends JLabel implements ListCellRenderer{
	public BotListCellRenderer(){
		setOpaque(true);
	}


	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		// PRint the bot's name
		Bot bot = (Bot)value;
		setText(bot.getName() + "(" + bot.toString() + ")");



		if(isSelected)
			setBackground(Color.LIGHT_GRAY);
		else
			setBackground(null);

		return this;
	}	
}

