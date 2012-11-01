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
public class AboutDialog extends JDialog implements ActionListener{
	MazeTest mazeTest;
	MazeUI parent;

    private static final String OKAY_LABEL = "Yeah, I get it.";

	public AboutDialog(MazeTest mazeTest, MazeUI owner){
		super((Frame)owner,"About Maize", true);	//1.6 only
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);

        // Set vars
		this.parent = owner;
		this.mazeTest = mazeTest;

        // Fix window size
		setSize(new Dimension(310, 160));
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());


        // Icon
        JPanel iconPanel = new ImagePanel(MazeUISettingsManager.icon);
        iconPanel.setSize(new Dimension(MazeUISettingsManager.icon.getWidth(),
                                        MazeUISettingsManager.icon.getHeight()));

        // About
        JLabel aboutText = new JLabel(getAboutText());

		// okay button
        JButton okayButton = new JButton(OKAY_LABEL);
        okayButton.addActionListener(this);
        

		//this.add(new JLabel("Instatiate Class");
		gbc.anchor = GridBagConstraints.CENTER;

        // icon
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.ipadx = 32;
		gbc.ipady = 32;
		this.add(iconPanel, gbc);

		// about
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.ipadx = 0;
		gbc.ipady = 20;
		this.add(aboutText, gbc);

        // okay button
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.ipady = 0;
		gbc.anchor = GridBagConstraints.LAST_LINE_END;
		this.add(okayButton,gbc);


		setVisible(true);
	}

    public class ImagePanel extends JPanel{
        private BufferedImage image;
        public ImagePanel(BufferedImage image) {
            this.image = image;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(this.image, 
                    this.getWidth()/2  - image.getWidth()/2,  
                    this.getHeight()/2 - image.getHeight()/2, null); // see javadoc for more info on the parameters            
        }

    }




    private String getAboutText(){
        String str = "<html>";
            
        str += "<b>Maize v" + MazeUISettingsManager.VERSION + "</b>";
        str += " on Java " + System.getProperty("java.version") + "<br>";
        str += "<br>";
        str += "Written by Stephen Wattam, Carl Ellis, <i>et al.</i>";
        /* str += "James Cheese, John Vidler and Adam Howard."; */

        return str + "</html>";
    }


	public void actionPerformed(ActionEvent Ae){
        dispose();
	}


}	


