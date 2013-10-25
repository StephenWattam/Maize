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
import maize.log.*;
public class NewMazeDialog extends JDialog implements ActionListener{
	MazeTest mazeTest;
	MazeUI parent;

	// buttons
	private JButton createButton = new JButton("Create");    
	private JButton cancelButton = new JButton("Cancel");    

	// Size of maze
	private JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(MazeUISettingsManager.defaultMazeWidth,1,250,1));
	private JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(MazeUISettingsManager.defaultMazeHeight,1,250,1));

	// Combo box of possible maze types
	private JComboBox factoryCombo;

	// Set the name of the maze
	private JTextField nameField = new JTextField();

	public NewMazeDialog(MazeTest mazeTest, MazeUI owner){
		super((Frame)owner,"New Maze", true);	//1.6 only, modality
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);

		// Make sure we have a handle to the mazetest, and be modal
		this.parent = owner;
		this.mazeTest = mazeTest;

		// Listen to create, cancel buttons
		createButton.addActionListener(this);
		cancelButton.addActionListener(this);

		// Set the layout for the dialog
		setSize(new Dimension(600, 150));
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());

		// Create a combo box with all the factories in it
		factoryCombo = new JComboBox( mazeTest.factories);






		// type
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.ipadx = 80;
		this.add(new JLabel("Type: "), gbc);

		gbc.gridwidth = 2;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.ipadx = 80;
		this.add(factoryCombo, gbc);

		// dimensions
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.ipadx = 80;
		this.add(new JLabel("Dim WxH: "),gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.ipadx = 40;
		this.add(widthSpinner, gbc);

		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.ipadx = 40;
		this.add(heightSpinner, gbc);

		//name
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.ipadx = 80;
		gbc.ipady = 10;
		this.add(new JLabel("Name: "), gbc);

		gbc.gridwidth = 2;
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.ipadx = 80;
		this.add(nameField, gbc);


		gbc.gridwidth = 2;
		// buttons
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.anchor = GridBagConstraints.LAST_LINE_START;
		this.add(cancelButton,gbc);


		gbc.gridwidth = 1;
		gbc.gridx = 2;
		gbc.gridy = 8;
		gbc.anchor = GridBagConstraints.LAST_LINE_END;
		this.add(createButton,gbc);


		setVisible(true);

	}


	// Handle input.
	public void actionPerformed(ActionEvent Ae){
		if(Ae.getSource() == cancelButton){
			dispose();
		}else if(Ae.getSource() == createButton){
			int mw = ((SpinnerNumberModel)widthSpinner.getModel()).getNumber().intValue();
			int mh = ((SpinnerNumberModel)heightSpinner.getModel()).getNumber().intValue();


			Maze m = ((MazeFactory)factoryCombo.getSelectedItem()).getMaze(mw, mh);
			m.setName(nameField.getText());

            Log.log("Creating new " + m.getWidth() + "x" + m.getHeight() + " maze ('" + nameField.getText() + "') using " + ((MazeFactory)factoryCombo.getSelectedItem()));


			mazeTest.mazes.add( m );
			this.parent.updatePanes();
			dispose();

		}
	}


}	
