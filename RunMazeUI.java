import maize.*;
import maize.ui.*;

import javax.swing.UIManager;
import java.io.IOException;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import java.util.*;


import maize.log.*;
public class RunMazeUI{

    // config file
    private static String CONFIG_LOCATION       = "maize.cfg";

	public static void main(String[] args){

		// For windows, try to acquire native L+F
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
		}catch(Exception e) {}

		// For linux, attempt to access GTK.  Falls through if on windows,
		try{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		}catch(Exception e){}


		// Construct a series of maze factories
		MazeTest mt = new MazeTest();
		mt.factories.add( new RandomTransformMazeFactory( new FullDFSMazeFactory() ) );
		mt.factories.add( new RandomTransformMazeFactory( new CircleMazeFactory() ) );
		mt.factories.add( new RandomTransformMazeFactory( new ScatterMazeFactory() ) );
		mt.factories.add( new RandomTransformMazeFactory( new RandomScatterMazeFactory() ) );
		mt.factories.add( new RandomTransformMazeFactory( new LineMazeFactory() ) );
		mt.factories.add( new RandomTransformMazeFactory( new EmptyMazeFactory() ) );
		mt.factories.add( new RandomTransformMazeFactory( new BaffleMazeFactory() ) );
		//mt.factories.add( new ());
		mt.factories.add( new FullDFSMazeFactory() );
		mt.factories.add( new CircleMazeFactory() );
		mt.factories.add( new ScatterMazeFactory() );
		mt.factories.add( new RandomScatterMazeFactory() );
		mt.factories.add( new LineMazeFactory() );
		mt.factories.add( new EmptyMazeFactory() );
		mt.factories.add( new BaffleMazeFactory() );

        if(!MazeUISettingsManager.loadConfig(CONFIG_LOCATION)){
            Log.log("Error loading resources.  Please attend to your config file, to be found at " + CONFIG_LOCATION);
            System.exit(1);
        }


		// Launch the UI itself.
        new MazeUI(mt);
	}	
}
