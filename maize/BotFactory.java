package maize;
import maize.compile.*;

import java.io.File;

/** Loads bots from a file, using the compiler helper to instantiate it.
 */
public class BotFactory{


    /** File to load/compile from */
    private File botFile;

    /** Class name.  Filled in when compiled. */
    private String botClass;

    /** Package bots are loaded into. */
    private String packageName; // i.e. "bots" 

    /** Create a new BotFactory from a given .java filepath and package name.
     *
     * @param botFile A relative path to a ,java file containing the bot code.
     * @param packageName The name of the package (without trailing dot)
     */
    public BotFactory(File botFile, String packageName){
        this.botFile = botFile;
        this.botClass = BotCompilerHelper.classNameFromBaseName(botFile.getName());
        this.packageName = packageName + ".";
    }

    /** Returns a new instance of the bot. 
     *
     * @return An instance of the class loaded from disk.  It will have content
     * from when #compile() was last called.
     * */
    public Bot getBot() 
        throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        // Try to load and return bot
        return BotCompilerHelper.loadBotClass(packageName + botClass);
    }

    /** Recompiles the bot from the data on disk, using the original filename. 
     *
     * @return True if compilation worked, false if not.
     */
    public boolean compile(){
        return BotCompilerHelper.compile(botFile.getPath());
    }

    /** Returns the filename this BotFactory uses.
     *
     * @return the bot .java filename.
     */
    public File getFile(){
        return botFile;
    }

    /** Returns the class name (without package) this bot uses when compiling.
     *
     * @return The bot's class name.
     */
    public String getClassName(){
        return botClass;
    }

    /** Returns the package name used when compiling the bot.
     *
     * @return The bot's package name.
     */
    public String getPackageName(){
        return packageName;
    }
}
