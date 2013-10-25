package maize;
import maize.compile.*;

import java.io.File;

/** Loads bots from a file, using the compiler helper.
 *
 */

public class BotFactory{


    // File to load/compile from
    private File botFile;

    // Class name.  Filled in when compiled.
    private String botClass;

    // Package bots are loaded into.
    private String packageName; // i.e. "bots" 

    public BotFactory(File botFile, String packageName){
        this.botFile = botFile;
        this.botClass = BotCompilerHelper.classNameFromBaseName(botFile.getName());
        this.packageName = packageName + ".";
    }

    /** Returns a new instance of the bot. */
    public Bot getBot() 
        throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        // Try to load and return bot
        return BotCompilerHelper.loadBotClass(packageName + botClass);
    }

    /** Recompiles the bot from the data on disk, using the original filename. */
    public boolean compile(){
        return BotCompilerHelper.compile(botFile.getPath());
    }

    public File getFile(){
        return botFile;
    }

    public String getClassName(){
        return botClass;
    }

    public String getPackageName(){
        return packageName;
    }
}
