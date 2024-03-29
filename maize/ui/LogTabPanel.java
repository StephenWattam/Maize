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
import java.text.*;
import java.util.*;


import maize.*;
public class LogTabPanel extends TabPanel implements ActionListener, LogListener{

    private static final String DELETE_BUTTON_LABEL = "Clear";
    private static final String SAVE_BUTTON_LABEL = "Save to File";

    // max lines to keep
    private static final int SCROLLBACK_LIMIT = MazeUISettingsManager.logScrollbackLimit;

	// The list and controlling buttons
	private JTextArea log;
    private JScrollPane logScrollPane;
	private JButton deleteButton	= new JButton(DELETE_BUTTON_LABEL);
	private JButton saveButton		= new JButton(SAVE_BUTTON_LABEL);

    public LogTabPanel(MazeTest mazeTest, JTabbedPane tabContainer, String name){
        super(mazeTest, tabContainer, name);
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());

		// listeners
		deleteButton.addActionListener(this);
		saveButton.addActionListener(this);

        // TODO: configure editability, etc
        log = new JTextArea();
        Font font = new Font("Courier", 0, 11);
        log.setFont(font);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);


		logScrollPane = new JScrollPane(log);
		/* logScrollPane.setPreferredSize(new Dimension(800, 400)); */

		//list
		gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill    = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.ipady = 10;
		gbc.ipadx = 10;
        gbc.weightx = 1;
        gbc.weighty = 1;
		gbc.gridwidth = 3;
        gbc.insets  = new Insets(10,10,0,10);
		this.add(logScrollPane,gbc);

		gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill    = GridBagConstraints.NONE;
		gbc.gridwidth = 1;
		gbc.ipadx = 100;
		gbc.ipady = 20;
		gbc.gridx = 0;
		gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets  = new Insets(10,10,10,0);
		this.add(saveButton,gbc);

		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 2;
		gbc.gridy = 1;
        gbc.insets  = new Insets(10,10,10,10);
		this.add(deleteButton,gbc);

		setVisible(true);
	}

	public void actionPerformed(ActionEvent Ae){
		if(Ae.getSource() == saveButton){
            saveLog();
		}else if(Ae.getSource() == deleteButton){
            log.setText("");
        }

	}

    private void saveLog(){
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		//fileChooser.setFileFilter(new SymbolFileFilter());

		if(fileChooser.showSaveDialog(this) == 0){
			try{
                Log.log("Writing log to " + fileChooser.getSelectedFile());

                PrintWriter fos = new PrintWriter( fileChooser.getSelectedFile() );
                fos.print(getInfo());
                fos.print(log.getText());
                fos.close();
			}catch(Exception e){
				Log.log("Error saving log");
				Log.logException(e);
				JOptionPane.showMessageDialog(this, "Error saving bot.");
			}
		}
    }

    private String getInfo(){
        String datetime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        
        String str =  "Maize " + MazeUISettingsManager.VERSION + "\n";
               str += "Log written at " + datetime + "\n";
               str += "\n";

        return str;
    }

    public void logEvent(String str){

        // Add a line and subtract one from the scrollback
        log.append( str + "\n" );

        if(log.getLineCount() > SCROLLBACK_LIMIT){
            // delete top line of text
            log.setText( log.getText().substring( log.getText().indexOf('\n') + 1 ) + "\n" );
        }

        // scroll to bottom
        JScrollBar vertical = logScrollPane.getVerticalScrollBar();
        if(vertical != null)
            vertical.setValue( vertical.getMaximum() );
            
    
    }

}


