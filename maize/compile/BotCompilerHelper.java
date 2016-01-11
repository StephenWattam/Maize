package maize.compile;

import maize.Bot;
import maize.log.*;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public abstract class BotCompilerHelper{

    // Compiles and loads bots into a given mazeTest
    public static Vector<Bot> compileAndLoadBots(String packageName, String dirname){
        Log.log("Compiling bots...");

        // Compile bots
        ArrayList<BotSources> bot_classes = BotCompilerHelper.compileAllBots(dirname);
        Vector<Bot>           bots = new Vector<>();

        // Instantiate classes
        for(BotSources source: bot_classes){
            try{ 
                bots.add( source.instantiate() );
            }catch(Exception e){
                Log.log("Error loading bot: " + source);
                Log.logException(e);
                System.out.println("Error loading bot class: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return bots;
    }


    // returns a list of class files to load as bots
    public static ArrayList<BotSources> compileAllBots(String dirname){
        ArrayList<BotSources> compiled_bots = new ArrayList<>();

        try {
            List<BotSources> botSource = BotSources.scanDirectory( new File(dirname) );

            for( BotSources bot : botSource ) {
                System.out.println(bot);
                if( bot.compile() ) {
                    Log.log("Compiler", "Compiled OK!");
                    compiled_bots.add( bot );
                }
                else
                    Log.log( "Compiler", "Compilation Failed!" );
            }
        } catch (IOException e) {
            e.printStackTrace();
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
