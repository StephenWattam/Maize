package maize.ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.swing.event.*;
import java.awt.geom.*;
import javax.imageio.*;
import java.util.*;


import maize.*;
// Renders two mazes side-by-side
public class DoubleMazeFrame extends JFrame {


    private Maze m1 = null;

    private MazePanel m1Panel;

    public DoubleMazeFrame() throws IOException{
	super("Auxiliary Mazes");
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//setResizable(false);
   
        //assign both	
	//this.m1 = m1;


	setSize(new Dimension(520, 530));
	setResizable(false);
	GridBagConstraints gbc = new GridBagConstraints();
	setLayout(new GridBagLayout());
	

	this.m1Panel = new MazePanel(null, MazeUISettingsManager.mazeTiles, MazeUISettingsManager.botTileSets, MazeUISettingsManager.fastMazeRendering);
	this.m1Panel.setSize(500,500);

	// mazes are canvases -- reign in their greedy getSize();
	JPanel m1PanelPanel = new JPanel();

	m1PanelPanel.add(this.m1Panel);
	m1PanelPanel.setSize(500,500);	

	// type
	gbc.gridx = 0;
	gbc.gridy = 0;
	this.add(new JLabel("Maze 1"), gbc);

	gbc.gridx = 0;
	gbc.gridy = 1;
	this.add(m1PanelPanel, gbc);	
	
	setVisible(true);
	this.repaint();
	

    }


    public MazePanel getM1Panel(){
		return this.m1Panel;
    }

}	
