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
public class NewBotDialog extends JDialog implements ActionListener{
	MazeTest mazeTest;
	MazeUI parent;

	private JButton createButton = new JButton("Create");    
	private JButton cancelButton = new JButton("Cancel");    

	private JTextField nameField;

	public NewBotDialog(MazeTest mazeTest, MazeUI owner, String botPackageName){
		super((Frame)owner,"Instantiate Bot", true);	//1.6 only
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);

		// Set the package name automagically, from MazeUI.
		nameField = new JTextField(botPackageName + ".");

		this.parent = owner;
		this.mazeTest = mazeTest;

		createButton.addActionListener(this);
		cancelButton.addActionListener(this);

		setSize(new Dimension(300, 100));
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());

		// Title


		//this.add(new JLabel("Instatiate Class");

		// type
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.ipadx = 80;
		this.add(new JLabel("Class: "), gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.ipadx = 80;
		this.add(nameField, gbc);


		// buttons
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.anchor = GridBagConstraints.LAST_LINE_START;
		this.add(cancelButton,gbc);


		gbc.gridx = 1;
		gbc.gridy = 8;
		gbc.anchor = GridBagConstraints.LAST_LINE_END;
		this.add(createButton,gbc);


		setVisible(true);

	}



	public void actionPerformed(ActionEvent Ae){
		if(Ae.getSource() == cancelButton){
			dispose();
		}else if(Ae.getSource() == createButton){
			try{ 
				Class theClass  = Class.forName( nameField.getText() );
				Bot b = (Bot)theClass.newInstance();
				mazeTest.bots.add(b);


				this.parent.updatePanes();
			}catch(Exception e){
				JOptionPane.showMessageDialog(this, "Error loading bot, check path and case.");
				e.printStackTrace();
			}
			dispose();

		}
	}


}	

