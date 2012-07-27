package maize.ui;

import javax.swing.*;
import java.awt.*;


import maize.*;
public class TestListCellRenderer extends JLabel implements ListCellRenderer{
	public TestListCellRenderer(){
		setOpaque(true);
	}


	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		// PRint the bot's name
		BotTest botTest = (BotTest)value;
		setText(botTest.bot.getName() + "(" + botTest.moves + " moves, finished? " + botTest.isFinished + ")");

		if(botTest.isFinished){
			setBackground(Color.GREEN);
		}else{
			setBackground(null);
		}


		// find this bot's icon from the ordering in the renderer
		setIcon(new ImageIcon( MazeUISettingsManager.botTileSets[index % MazeUISettingsManager.botTileSets.length].botN ));

		return this;
	}	
}
