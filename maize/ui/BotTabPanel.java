package maize.ui;
import maize.log.*;

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
public class BotTabPanel extends TabPanel implements ActionListener, ListSelectionListener{

	// Text labels
	private static final String BOT_NAME_PLACEHOLDER	= "Name";
	private static final String BOT_DESC_PLACEHOLDER	= "Description";
	private static final String REFRESH_BUTTON_LABEL	= "Refresh";
	private static final String DELETE_BUTTON_LABEL	    = "Delete";
	private static final String SAVE_BUTTON_LABEL	    = "Save...";

	// The list and controlling buttons
	private JList botList;
	private JButton deleteButton	= new JButton(DELETE_BUTTON_LABEL);
	private JButton saveButton		= new JButton(SAVE_BUTTON_LABEL);
	// Selected bot
	private JLabel botNameLabel		= new JLabel(BOT_NAME_PLACEHOLDER);
	private JLabel botDescriptionLabel	= new JLabel(BOT_DESC_PLACEHOLDER);

    public BotTabPanel(MazeTest mazeTest, JTabbedPane tabContainer, String name){
        super(mazeTest, tabContainer, name);
        GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());

		//label style
		botNameLabel.setHorizontalAlignment( JLabel.CENTER );
		botNameLabel.setVerticalTextPosition( JLabel.CENTER );
		botNameLabel.setFont(new Font("Serif", Font.BOLD, 24));


		botDescriptionLabel.setHorizontalAlignment( JLabel.CENTER );
		botNameLabel.setVerticalTextPosition( JLabel.CENTER );


		// listeners
		deleteButton.addActionListener(this);
		saveButton.addActionListener(this);


		////symbol list
		botList = new JList(mazeTest.bots);
		botList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		botList.setCellRenderer(new BotListCellRenderer());
		botList.addListSelectionListener(this);

		JScrollPane botListPanel = new JScrollPane(botList);
		botListPanel.setPreferredSize(new Dimension(800, 400));


		gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill    = GridBagConstraints.BOTH;
		// label1
		gbc.gridwidth = 3;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.ipadx = 0;
		gbc.weightx = 0.25;
		gbc.weighty = 0.25;
		gbc.ipady = 10;
        gbc.insets  = new Insets(10,10,0,10);
		this.add(botNameLabel,gbc);

		// label2
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.ipadx = 0;
		gbc.ipady = 10;
        gbc.insets  = new Insets(0,10,10,10);
		this.add(botDescriptionLabel,gbc);

		//list
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.ipady = 0;
		gbc.weightx = 0.8;
		gbc.weighty = 0.8;
		gbc.gridwidth = 3;
        gbc.insets  = new Insets(10,10,0,10);
		this.add(botListPanel,gbc);


		gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill    = GridBagConstraints.NONE;
		gbc.gridwidth = 1;
		gbc.ipadx = 100;
		gbc.ipady = 20;
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.weightx = 0;
		gbc.weighty = 0;
        gbc.insets  = new Insets(10,10,10,0);
		this.add(saveButton,gbc);

		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 2;
		gbc.gridy = 3;
        gbc.insets  = new Insets(10,10,10,10);
		this.add(deleteButton,gbc);


		setVisible(true);
	}

	public void actionPerformed(ActionEvent Ae){
		if(Ae.getSource() == saveButton){
			saveBot((Bot) botList.getSelectedValue());
		}else if(Ae.getSource() == deleteButton){
			deleteBot((Bot) botList.getSelectedValue());
		}

	}

	//called to regtresjh state changes
	public void update(){
		botList.setListData(mazeTest.bots);
	}

	// Deletes a bot from the list and from the mazeTest object
	private void deleteBot(Bot b){
		mazeTest.bots.remove(b);
		update();
	}

	// Fires when the user selects something in the list
	public void valueChanged(ListSelectionEvent LSe){
		if(LSe.getSource() != botList)
			return;

		if( botList.getSelectedIndex() == -1){
			botNameLabel.setText( BOT_NAME_PLACEHOLDER);
			botDescriptionLabel.setText( BOT_DESC_PLACEHOLDER);
		}else{
			botNameLabel.setText( ((Bot)botList.getSelectedValue()).getName());
			botDescriptionLabel.setText( ((Bot)botList.getSelectedValue()).getDescription());
		}
	}

	// Saves a bot to disk as a serialised object
	private void saveBot(Bot b){
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		//fileChooser.setFileFilter(new SymbolFileFilter());
		if(fileChooser.showSaveDialog(this) == 0){
			try{
				ClassSerializer.save( fileChooser.getSelectedFile(), b);	
			}catch(Exception e){
				Log.log("Error saving bot in bot tab panel, saveBot(Bot):\n");
				Log.logException(e);
				JOptionPane.showMessageDialog(this, "Error saving bot.");
			}
		}
	}

	// Returns the selected bot from the list
	public Bot getSelectedBot(){
		return (Bot)botList.getSelectedValue();
	}

}

