package maize.ui;

import javax.swing.*;
import java.awt.*;


import maize.*;
import maize.trial.BotTest;
public class TestListCellRenderer extends JLabel implements ListCellRenderer{

    // The maze panel to get icons from
    private MazePanel mazePanel;

	public TestListCellRenderer(MazePanel mazePanel){
		setOpaque(true);
        this.mazePanel = mazePanel;
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


        // Load the icon from the MazePanel
        ImageIcon icon = null;
        if( this.mazePanel != null ){
            BotTileSet tiles = this.mazePanel.getTileSet( botTest.agent );
            if(tiles != null){
                /* icon = new ImageIcon( tiles.botN ); */
                switch( botTest.agent.getOrientation() ){
                    case Orientation.NORTH:
                        icon = new ImageIcon( tiles.botN );
                        break;
                    case Orientation.EAST:
                        icon = new ImageIcon( tiles.botE );
                        break;
                    case Orientation.SOUTH:
                        icon = new ImageIcon( tiles.botS );
                        break;
                    case Orientation.WEST:
                        icon = new ImageIcon( tiles.botW );
                        break;
                }
            }
        }
        setIcon( icon );

		return this;
	}	
}
