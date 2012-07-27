package maize.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.swing.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.io.*;
import javax.imageio.*;


import maize.*;
public class MazeTabPanel extends JPanel implements ActionListener, ListSelectionListener{

    private MazeTest mazeTest;

    private JList mazeList;
    private JButton deleteButton = new JButton("Delete");
    private JButton saveButton = new JButton("Save...");

    private JPanel mazePanelPanel = new JPanel();
    private MazePanel mazePanel;

    public MazeTabPanel(MazeTest mazeTest) throws IOException{
	this.mazeTest = mazeTest;

	GridBagConstraints gbc = new GridBagConstraints();
	setLayout(new GridBagLayout());



	// maze panel
	mazePanel = new MazePanel(MazeUISettingsManager.mazeTiles, MazeUISettingsManager.botTileSets);
	mazePanel.setSize(500,500);

	mazePanelPanel.add(mazePanel);

	
	deleteButton.addActionListener(this);
	saveButton.addActionListener(this);
	

	////symbol list
	mazeList = new JList(mazeTest.mazes);
	mazeList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	mazeList.setCellRenderer(new MazeListCellRenderer());
	mazeList.addListSelectionListener(this);

	JScrollPane mazeListPanel = new JScrollPane(mazeList);
	mazeListPanel.setPreferredSize(new Dimension(300, 440));


	// panel
	gbc.gridx = 0;
	gbc.gridy = 0;
	gbc.ipadx = 40;
	gbc.ipady = 40;
	gbc.gridheight = 2;
	gbc.fill = GridBagConstraints.CENTER;
	this.add(mazePanelPanel,gbc);

	//list
	gbc.gridwidth = 3;
	gbc.gridheight = 1;
	gbc.gridx = 1;
	gbc.gridy = 0;
	gbc.ipadx = 300;
	gbc.ipady = 440;
	this.add(mazeListPanel,gbc);

	//button
	gbc.gridwidth = 1;
	gbc.ipadx = 100;
	gbc.ipady = 20;
	//gbc.gridx = 1;
	//gbc.gridy = 1;
	//gbc.ipadx = 0;
	//gbc.ipady = 0;
	//gbc.weightx = 0.5;
	//this.add(refreshButton,gbc);

	gbc.gridx = 2;
	gbc.gridy = 1;
	gbc.weightx = 0.5;
	this.add(saveButton,gbc);

	gbc.gridx = 3;
	gbc.gridy = 1;
	gbc.weightx = 0.5;
	this.add(deleteButton,gbc);





	setVisible(true);
    }

    public void actionPerformed(ActionEvent Ae){
	if(Ae.getSource() == saveButton){
	    saveMaze((Maze) mazeList.getSelectedValue());
	}else if(Ae.getSource() == deleteButton){
	    deleteMaze((Maze) mazeList.getSelectedValue());
	}
	
	//System.out.println("number of mazes: " + mazeTest.mazes.size());
    }

    //called to regtresjh state changes
    public void update(){
	mazeList.setListData(mazeTest.mazes);
    }

    private void deleteMaze(Maze m){
	mazePanel.setMaze(null);
	mazeTest.mazes.remove(m);
	update();
    }

    public void valueChanged(ListSelectionEvent LSe){
	if(LSe.getSource() != mazeList)
		return;

	mazePanel.setMaze((Maze) mazeList.getSelectedValue());
	mazePanel.repaint();
    }


    private void saveMaze(Maze m){
	final JFileChooser fileChooser = new JFileChooser();
	fileChooser.setMultiSelectionEnabled(false);
	//fileChooser.setFileFilter(new SymbolFileFilter());
	if(fileChooser.showSaveDialog(this) == 0){
	    try{
		ClassSerializer.save( fileChooser.getSelectedFile(), m);	
	    }catch(Exception e){
		JOptionPane.showMessageDialog(this, "Error saving maze.");
	    }
	}
    }


    public Maze getSelectedMaze(){
	return (Maze) mazeList.getSelectedValue();
    }
}
