package maize.compile;
import javax.tools.*;
import java.util.*;
import java.io.*;
import maize.*;

import maize.log.*;
public abstract class BotCompilerHelper{

    // Compileas and loads bots into a given mazeTest 
    public static Vector<Bot> compileAndLoadBots(String packageName, String dirname){
        Log.log("Compiling bots...");
        
        // Compile bots
        Vector<String> bot_classes = BotCompilerHelper.compileAllBots(dirname);
        Vector<Bot>           bots = new Vector<Bot>();

        // Instantiate classes
        for(String s: bot_classes){
            try{ 
                bots.add(BotCompilerHelper.loadBotClass(packageName + "." + s));
            }catch(Exception e){
                Log.log("Error loading bot " + s);
                Log.logException(e);
                System.out.println("Error loading bot class: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return bots;
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
            Log.log("No bots found!");
        }else{
            for(int i=0; i<children.length; i++){
                Log.log("Compiling bot " + children[i] + "...");
                if(compile(dirname + java.io.File.separator + children[i])){
                    Log.log(children[i] + " compiled successfully!");
                    compiled_bots.add(classNameFromBaseName(children[i]));
                    //compiled_bots.add(children[i].replaceAll(".java$", ".class"));
                }else{
                    Log.log("Failed to compile " + children[i]);
                }
            }
        }

        // Return the list of class names
        return compiled_bots;
    }



    // Get the class name from a file basename by removing the trailing ".java"
    public static String classNameFromBaseName(String baseName){
        return baseName.replaceAll(".java$", "");
    }



    // Load a bot from a class name
    //
    // Note that THIS ONLY WORKS BECAUSE BOT IS AN INTERFACE!
    public static Bot loadBotClass(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException{

        Log.log("Loading bot class: " + className);

        // Pass the current ClassLoader to the BotClassLoader
        ClassLoader parentClassLoader   = ClassReloader.class.getClassLoader();
        ClassReloader classLoader       = new ClassReloader(parentClassLoader, className);

        // Then load the desired bot with it
        Class myObjectClass             = classLoader.loadClass(className);

        // And return
        try {
            return (Bot) myObjectClass.newInstance();
        } catch( SecurityException err ) {
            Log.log( "Security Error!" );
            Log.logException( err );
            throw new InstantiationException( "Bot '" +className+ "' caused a SecurityException! (" +err.getMessage()+ ")" );
        } catch( IllegalAccessError err ) {
            Log.logException( err );
            throw new InstantiationException( "Bot '" +className+ "' caused an IllegalAccessError! (" +err.getMessage()+ ")" );
        }
    }


    // Load a maze factory from a class name from the current ClassLoader's set of loaded classes (i.e. merely instantiate a new one,
    // don't compile it)
    //
    // Note that THIS ONLY WORKS BECAUSE BOT IS AN INTERFACE!
    public static Object loadClass(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException{

        Log.log("Loading class: " + className);

        // Pass the current ClassLoader to the BotClassLoader
        ClassLoader parentClassLoader   = ClassReloader.class.getClassLoader();
        ClassReloader classLoader       = new ClassReloader(parentClassLoader, className);

        // Then load the desired bot with it
        Class myObjectClass             = classLoader.loadClass(className);

        // And return
        try {
            return myObjectClass.newInstance();
        } catch( SecurityException err ) {
            Log.log( "Security Error!" );
            Log.logException( err );
            throw new InstantiationException( "Class '" +className+ "' caused a SecurityException! (" +err.getMessage()+ ")" );
        } catch( IllegalAccessError err ) {
            Log.logException( err );
            throw new InstantiationException( "Class '" +className+ "' caused an IllegalAccessError! (" +err.getMessage()+ ")" );
        }
    }


    /** Check if the current instance of java has access to a compiler.
     *
     * @return true if a compiler is available, false otherwise.
     */
    public static boolean isCompilerAvailable(){
        return ToolProvider.getSystemJavaCompiler() != null;
    }


    // Compiles a filename
    public static boolean compile(String fname){

        // Check we're running within the JDK, not the standard JRE
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if(compiler == null){
            Log.log("");
            Log.log("IMPORTANT: No compiler is available on this platform.");
            Log.log("           Maize requires the java compiler to run bots, meaning you must");
            Log.log("           install the JDK, rather than the JRE.");
            Log.log("           If you already have both installed, check your classpath.");
            Log.log("");
            return false;
            /* System.exit(1); */
        }

        // Compile, logging to stdout/stderr
        int compilationResult = compiler.run(null, 
                new LogOutputStream("<stdout> "), 
                new LogOutputStream("<stderr> "), 
                fname);

        // Return true if it worked.
        return compilationResult == 0;
    }
}
