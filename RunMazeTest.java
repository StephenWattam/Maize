import maize.*;
import maize.log.*;
import maize.trial.*;

import java.util.*;
import java.io.*;
import java.awt.Dimension;
import java.nio.charset.spi.CharsetProvider;
import java.nio.charset.Charset;

// see http://javacsv.sourceforge.net/
import com.csvreader.CsvWriter;

public class RunMazeTest{

    // TODO: Move into config file
    public static final boolean INSTANTIATE_EVERY_RUN   = false;
    public static final boolean INSTANTIATE_EVERY_MAZE  = true;
    public static final int TESTS_PER_FACTORY           = 5;
    public static final int TESTS_PER_MAZE              = 5;
    public static final Dimension MAZE_SIZE             = new Dimension(20, 20);
    public static final int BOT_START_TIMEOUT           = 100;
    public static final int BOT_WORK_TIMEOUT            = 10;
    public static final String BOT_PACKAGE              = "bots";
    public static final int TICK_LIMIT                  = 5000;

    // If the bot times out over this number of times on a maze,
    // kill it.
    public static final int BOT_STUCK_LIMIT             = 50;

    public static void main(String[] argv){

        // TODO: usage
        if(argv.length < 2){
            printUsage();
            System.exit(1);
        }


		// Construct a series of maze factories
        // TODO: load from config file
        Vector<MazeFactory> mfs = new Vector<MazeFactory>();
		mfs.add( new FullDFSMazeFactory() );
		mfs.add( new CircleMazeFactory() );
		mfs.add( new ScatterMazeFactory() );
		mfs.add( new RandomScatterMazeFactory() );
		mfs.add( new LineMazeFactory() );
		mfs.add( new EmptyMazeFactory() );
		//mfs.add( new ());


        // Construct a list of bots from the command line
        Vector<BotFactory> bfs = new Vector<BotFactory>();
        for(int i=0; i<argv.length - 1; i++){
            bfs.add(new BotFactory(new File(argv[i]), BOT_PACKAGE));
        }
        
        // Run the actual thing!
        new RunMazeTest(
                mfs,  
                bfs,
                new File(argv[argv.length-1]),
                INSTANTIATE_EVERY_RUN,
                INSTANTIATE_EVERY_MAZE,
                TESTS_PER_FACTORY,
                TESTS_PER_MAZE,
                MAZE_SIZE,
                BOT_START_TIMEOUT,
                BOT_WORK_TIMEOUT,
                TICK_LIMIT,
                BOT_STUCK_LIMIT
            );

    }

    private static void printUsage(){
        System.out.println("USAGE INFO HERE: [BOTFILE [BOTFILE [ ...]]] OUTCSVFILE");
    }

    // ----------------------------------------------------------------------------------------

    // How long to wait in between simulation ticks.
    public static final int TICK_DELAY = 0;

    // Store bot details
    private Vector<MazeFactory> mfs;
    private Vector<BotFactory> bfs;

    // Settings for the run.
    private boolean instantiateEveryRun = false;
    private boolean instantiateEveryMaze = false;
    private int testsPerMaze;
    private int testsPerFactory;
    private Dimension mazeSize;

    // Timeouts.
    private int botStartTimeout;
    private int botWorkTimeout;
    private int tickLimit;
    private int seqTimeoutLimit;    // number of sequential timeouts before failure.

    // Keep track of count
    private int testCount = 0;

    // Settings for output
    private File outputFile;
    private CsvWriter cout;

    // Bot cache if re-using instances
    private Vector<Bot> botCache;
    private Vector<String> rowCache = new Vector<String>();

    /** Create a new RunMazeTest with various parameters for producing output. 
     *
     * @param mt The MazeTest object with pre-loaded bots and/or maze factories
     * @param botFiles A list of filenames to compile/reload
     * @param outputFile The file to output CSVs to
     * @param instantiateEveryRun Should the bots be re-instantiated for each run?
     * @param testsPerFactory Number of tests to run per MazeFactory
     * @param testsPerMaze Number of tests to run per individual maze
     */
    public RunMazeTest(
            Vector<MazeFactory> mazeFactories, 
            Vector<BotFactory> botFactories, 
            File outputFile,
            boolean instantiateEveryRun,
            boolean instantiateEveryMaze,
            int testsPerFactory,
            int testsPerMaze,
            Dimension mazeSize,
            int botStartTimeout,
            int botWorkTimeout,
            int tickLimit,
            int seqTimeoutLimit
            ){


        // Load initial settings
        this.mfs                    = mazeFactories;
        this.bfs                    = botFactories;
        this.outputFile             = outputFile;
        this.instantiateEveryRun    = instantiateEveryRun;
        this.instantiateEveryMaze    = instantiateEveryMaze;
        this.testsPerFactory        = testsPerFactory;
        this.testsPerMaze           = testsPerMaze;
        this.mazeSize               = mazeSize;
        this.botStartTimeout        = botStartTimeout;
        this.botWorkTimeout         = botWorkTimeout;
        this.tickLimit              = tickLimit;
        this.seqTimeoutLimit        = seqTimeoutLimit;
        this.cout                   = new CsvWriter(outputFile.getPath());//, ',', CharsetProvider.charsetForName("UTF-8"));

        // Tell the user stuff
        Log.log("Started the test class with " + mfs.size() + 
                " maze type[s], " + bfs.size() + " bot[s].");
        Log.log("Running " + testsPerFactory + " tests per factory, " + 
                testsPerMaze + " per maze, totalling " + (testsPerFactory * mfs.size() * testsPerMaze) + " tests.");

        // Compile all the bots.
        compileBots();

        // Output the header of the CSV
        rowCache.add("count");                 // count
        rowCache.add("maze_factory"); // maze class
        rowCache.add("bot_class");  // bot class
        rowCache.add("maze");                      // maze
        rowCache.add("bot");               // bot
        rowCache.add("bot_name");     // Bot name
        rowCache.add("moves");             // Moves
        rowCache.add("finished");        // Is finished
        rowCache.add("stuck");        // Got stuck?
        rowCache.add("bot_duration_ms");       // Duration in ms
        rowCache.add("bot_duration_s");// Duration in s
        rowCache.add("test_duration_ms");                  // Test duration in ms
        rowCache.add("test_duration_s");           // Test duration in s
        outputCSVRow();
        /* rowCache.add(""+); */


        try{
            // Instantiate here if not to be re-done
            if(!instantiateEveryRun && !instantiateEveryMaze)
                botCache = instantiateBots();

            // Run the tests
            runTests();

        }catch(ClassNotFoundException CNFe){
            Log.log("Class not found: " + CNFe.getMessage());
            Log.logException(CNFe);
        }catch(InstantiationException Ie){
            Log.log("Instantiation exception: " + Ie.getMessage());
            Log.logException(Ie);
        }catch(IllegalAccessException IAe){
            Log.log("Illegal access exception: " + IAe.getMessage());
            Log.logException(IAe);
        }

        // Flush
        this.cout.close();
    }

    // Compile all the bots in the BotFactory lists.
    private void compileBots(){
        for(BotFactory bf: bfs){

            Log.log("Compiling " + bf.getFile() + "...");

            if(!bf.compile()){
                Log.log("Error compiling " + bf.getFile() + " --- I presume you don't want to continue.");
                System.exit(1);
            }else{
                Log.log("Compiled " + bf.getFile() + " successfully.");
            }
        }
    }


    // Loop over the tests and actually run them.
    private void runTests()
        throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        for(MazeFactory mf: mfs){
            Log.log("Testing " + testsPerFactory + " mazes from " + mf + "...");

            for(int i=0; i<testsPerFactory; i++){
                // Generate the maze
                Maze maze = mf.getMaze((int)mazeSize.getWidth(), (int)mazeSize.getHeight());
                Log.log("Maze generated is " + maze.getWidth() + "x" + maze.getHeight());

                testMaze(maze, mf);
            }

        }
        
        Log.log("All tests complete.");
    }

    // Test a single maze testsPerMaze timnes with all bots.
    private void testMaze(Maze maze, MazeFactory mf)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {

        // Reinstantiate before each individual maze if necessary
        Vector<Bot> bots = new Vector<Bot>();
        if(instantiateEveryMaze)
            bots = instantiateBots();

        // And then run.
        for(int i=0; i<testsPerMaze; i++){
            
            // Increment the global count of tests
            testCount ++;

            Log.log("" + testCount + "/" + (testsPerFactory * mfs.size() * testsPerMaze),
                    "Testing maze " + maze + " (" + mf.getClass().getName() + ") " + i + "/" + testsPerMaze + " time[s], with all " + bfs.size() + " bot[s].");

            // Check we have some bots to do stuff with
            if(!instantiateEveryRun && !instantiateEveryMaze){
                bots = botCache;
            }else if(instantiateEveryRun){
                bots = instantiateBots();
            }

            // Then run the thing
            StatTest test = new StatTest(maze, bots, tickLimit, mf);
            long duration = test.run();

            Log.log("Test completed in " + (duration/1000.0) + "s.");
        }
    }


    // Instantiate all bots.
    private Vector<Bot> instantiateBots()
        throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        Log.log("Instantiating " + bfs.size() + " bot[s].");
        Vector<Bot> bots = new Vector<Bot>();

        for(BotFactory bf: bfs){
            bots.add(bf.getBot());
        }

        return bots;
    }


    // Holds the statistics from a single test of many bots on one maze.
    class StatTest implements TestListener{

        // Create a new hashmap to store the times of execution
        // Everything else be stored in the BotTest itself.
        private long timeStarted;
        private HashMap<BotTest, Long> times = new HashMap<BotTest, Long>();

        // Store the list of bots
        private Vector<Bot> bots;
        private Maze maze;

        // Store progress for updates
        private Vector<BotTest> runningTests = new Vector<BotTest>();
        private int tickLimit;

        // Store this for data
        private MazeFactory mf;

        // Create a new test but don't actually launch it yet.
        public StatTest(Maze maze, Vector<Bot> bots, int tickLimit, MazeFactory mf){

            Log.log("Creating new test with maze " + maze + " and " + bots.size() + " bot[s].");

            this.bots = bots;
            this.maze = maze;
            this.tickLimit = tickLimit;
            this.mf = mf;
        }


        // Run the test, counting the time taken for each of the items to complete.
        public long run(){
            Log.log("Running test with maze " + maze + "...");

            // Create a test thread
            Bot[] botArray = (Bot[])bots.toArray(new Bot[bots.size()]);

            // Start timer
            timeStarted = System.currentTimeMillis();

            // Start thread (which starts the sim).
            Test currentTest = new Test(
                    maze,
                    botArray,
                    this, 
                    botStartTimeout,
                    botWorkTimeout,
                    seqTimeoutLimit
                );

            long ticks = 0;
            while(currentTest.iterate() == true){
               
                // increment ticks
                ticks ++;

                // Quit if over the tick limit
                if(ticks > tickLimit){
                    Log.log("Tick limit (" + tickLimit + ") reached.  Stopping test...");
                    break;
                }

            }

            // Time stopped (before thread pickiness)
            long duration = System.currentTimeMillis() - timeStarted;

            // Account for the currently running items
            BotTest bt;
            while(runningTests.size() > 0){
                bt = runningTests.remove(0);
                times.put(bt, new Long(-1));
            }


            // TODO: some kind of logging or output of an object
            // that can be sent to a CSV system.
            for(Map.Entry<BotTest, Long> map: times.entrySet()){
                BotTest botTest  = map.getKey();
                Long    botTime  = map.getValue();

                rowCache.add(""+testCount);                 // count
                rowCache.add(""+mf.getClass().getName()); // maze class
                rowCache.add(""+botTest.bot.getClass().getName());  // bot class
                rowCache.add(""+maze);                      // maze
                rowCache.add(""+botTest.bot);               // bot
                rowCache.add(""+botTest.bot.getName());     // Bot name
                if(botTest.isFinished){
                    rowCache.add(""+botTest.moves);             // Moves
                }else{
                    rowCache.add("");             // Moves missing if it didn't complete
                }
                rowCache.add(""+botTest.isFinished);        // Is finished
                rowCache.add(""+botTest.isStuck);        // Is finished

                if(botTest.isFinished){
                    rowCache.add(""+botTime.longValue());       // Duration in ms
                    rowCache.add(""+botTime.longValue()/1000.0);// Duration in s
                }else{
                    rowCache.add("");                       // N/A for things that didn't end
                    rowCache.add("");
                }
                rowCache.add(""+duration);                  // Test duration in ms
                rowCache.add(""+duration/1000.0);           // Test duration in s
                /* rowCache.add(""+); */

                // Output and flush
                outputCSVRow();
            }

            // Return duration
            return duration;
        }


        /* ---------- Callbacks for TestListener interface ---------- */


        /** Called when a new bot is added to the test. */
        public void addAgent(BotTest bt){
            runningTests.add(bt);
        }

        /** Called when an agent is removed from the running test. */
        public void remAgent(BotTest bt){
            runningTests.remove(bt);

            // TODO: count time taken if bt.finished
            if(bt.isFinished){
                times.put(bt, new Long(System.currentTimeMillis() - timeStarted));
            }
        }

        /** Called when the simulation ticks and updates info. */
        public void updateAgents(){
        }

    }

    // Output details to CSV
    private void outputCSVRow(){
       
        // Cook up a new array
        String[] record = (String[])rowCache.toArray(new String[rowCache.size()]);
        rowCache.clear();

        // Write as a single line
        try{
            this.cout.writeRecord(record);
            this.cout.flush();
        }catch(IOException IOe){
            Log.log("Error writing row to CSV.");
            Log.logException(IOe);
        }

    }

}





