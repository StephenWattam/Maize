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
import java.text.*;
import java.util.*;


import maize.*;
public class LogTabPanel extends JPanel implements ActionListener, LogListener{

    private static final String DELETE_BUTTON_LABEL = "Clear";
    private static final String SAVE_BUTTON_LABEL = "Save to File";

    private MazeTest mazeTest;

	// The list and controlling buttons
	private JTextArea log;
	private JButton deleteButton	= new JButton(DELETE_BUTTON_LABEL);
	private JButton saveButton		= new JButton(SAVE_BUTTON_LABEL);


	public LogTabPanel(MazeTest mazeTest) throws IOException{
		this.mazeTest = mazeTest;

		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());

		// listeners
		deleteButton.addActionListener(this);
		saveButton.addActionListener(this);

        // TODO: configure editability, etc
        log = new JTextArea();


		JScrollPane logScrollPane = new JScrollPane(log);
		logScrollPane.setPreferredSize(new Dimension(800, 400));

		//list
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.ipady = 0;
		gbc.gridwidth = 3;
		this.add(logScrollPane,gbc);

		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.gridwidth = 1;
		gbc.ipadx = 100;
		gbc.ipady = 20;
		gbc.gridx = 0;
		gbc.gridy = 3;
		this.add(saveButton,gbc);

		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 2;
		gbc.gridy = 3;
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
        
        String str = "Maize " + MazeUISettingsManager.VERSION + "\n";
        str += "Log written at " + datetime + "\n";
        str += "\n";

        return str;
    }

    public void logEvent(String str){
        log.setText( log.getText() + str + "\n" );
    }

	/* //called to regtresjh state changes */
	/* public void update(){ */
	/* 	botList.setListData(mazeTest.bots); */
	/* } */

}


