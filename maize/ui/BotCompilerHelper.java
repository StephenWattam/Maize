package maize.ui;
import javax.tools.*;
import java.util.*;
import java.io.*;
import maize.*;

public abstract class BotCompilerHelper{

	// Compileas and loads bots into a given mazeTest 
	public static void compileAndLoadBots(MazeTest mazeTest, String packageName, String dirname){
		Log.log("Compiling bots...");
		Vector<String> bot_classes = compileAllBots(dirname); // compile
		for(String s: bot_classes){ // and load
			try{ 
				mazeTest.bots.add(loadBot(packageName + "." + s));
			}catch(Exception e){
                Log.log("Error loading bot " + s);
                Log.logException(e);
			}
		}
	}



	// returns a list of class files to load as bots
	public static Vector<String> compileAllBots(String dirname){


		// Filter all .java files from the filename
		FilenameFilter filter = new FilenameFilter(){
			public boolean accept(File dir, String name){
				return name.endsWith(".java") && !name.startsWith(".");
			}
		};

		// Read the file listing
		File pwd = new File(dirname);
		String[] children = pwd.list(filter);
		Vector<String> compiled_bots = new Vector<String>();

		// check through the list and compile stuff
		if(children == null){
			System.err.println("No bots found!");
		}else{
			for(int i=0; i<children.length; i++){
                Log.log("Compiling bot " + children[i]);
				if(compile(dirname + java.io.File.separator + children[i])){
                    Log.log(children[i] + " compiled successfully!");
					compiled_bots.add(classNameFromBaseName(children[i]));
					//compiled_bots.add(children[i].replaceAll(".java$", ".class"));
				}else{
					System.err.println("Failed to compile " + children[i]);
                    Log.log("Failed to compile " + children[i]);
				}
			}
		}

		// Return the list of class names
		return compiled_bots;
	}

	public static String classNameFromBaseName(String baseName){
		return baseName.replaceAll(".java$", "");
	}

	// Load a bot from a class name
	public static Bot loadBot(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		Class theClass  = Class.forName( className );
		return (Bot)theClass.newInstance();
	}

	// Compiles a filename
	public static boolean compile(String fname){
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		int compilationResult =	compiler.run(null, null, null, fname);

		return compilationResult == 0;
	}

}
