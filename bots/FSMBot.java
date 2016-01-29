package bots;
import maize.Bot;
import maize.Direction;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultCaret;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

public class FSMBot extends JFrame implements Bot {

	private static final String mAnimalNames[] = new String[]{ "Aardvark", "Albatross", "Alligator", "Alpaca", "Ant", "Anteater", "Antelope", "Ape", "Armadillo", "Donkey", "Baboon", "Badger", "Barracuda", "Bat", "Bear", "Beaver", "Bee", "Bison", "Boar", "Buffalo", "Butterfly", "Camel", "Capybara", "Caribou", "Cassowary", "Cat", "Caterpillar", "Cattle", "Chamois", "Cheetah", "Chicken", "Chimpanzee", "Chinchilla", "Clam", "Coati", "Cobra", "Cockroach", "Cod", "Cormorant", "Coyote", "Crab", "Crane", "Crocodile", "Crow", "Curlew", "Deer", "Dinosaur", "Dog", "Dogfish", "Dolphin", "Donkey", "Dotterel", "Dove", "Dragonfly", "Duck", "Dugong", "Dunlin", "Eagle", "Echidna", "Eel", "Eland", "Elephant", "Elk", "Emu", "Falcon", "Ferret", "Finch", "Fish", "Flamingo", "Fly", "Fox", "Frog", "Gaur", "Gazelle", "Gerbil", "Giraffe", "Gnat", "Gnu", "Goat", "Goose", "Goldfinch", "Goldfish", "Gorilla", "Goshawk", "Grasshopper", "Grouse", "Guanaco", "GuineaPig", "Gull", "Hamster", "Hare", "Hawk", "Hedgehog", "Heron", "Herring", "Hippopotamus", "Hornet", "Horse", "Hummingbird", "Hyena", "Ibex", "Ibis", "Jackal", "Jaguar", "Jay", "Jellyfish", "Kangaroo", "Kingfisher", "Kinkajou", "Koala", "Kookabura", "Kouprey", "Kudu", "Lapwing", "Lark", "Lemur", "Leopard", "Lion", "Llama", "Lobster", "Locust", "Loris", "Louse", "Lyrebird", "Magpie", "Mallard", "Manatee", "Mandrill", "Mantis", "Marten", "Meerkat", "Mink", "Mole", "Mongoose", "Monkey", "Moose", "Mouse", "Mosquito", "Mule", "Narwhal", "Newt", "Nightingale", "Octopus", "Okapi", "Opossum", "Oryx", "Ostrich", "Otter", "Owl", "Oyster", "Panther", "Parrot", "Panda", "Partridge", "Peafowl", "Pelican", "Penguin", "Pheasant", "Pig", "Pigeon", "PolarBear", "Pony", "Porcupine", "Porpoise", "PrairieDog", "Quail", "Quelea", "Quetzal", "Rabbit", "Raccoon", "Rail", "Ram", "Rat", "Raven", "Reindeer", "Rhinoceros", "Rook", "Salamander", "Salmon", "Sandpiper", "Sardine", "Scorpion", "Seahorse", "Seal", "Shark", "Sheep", "Shrew", "Skunk", "Sloth", "Snail", "Snake", "Sparrow", "Spider", "Spoonbill", "Squid", "Squirrel", "Starling", "Stingray", "Stinkbug", "Stork", "Swallow", "Swan", "Tapir", "Tarsier", "Termite", "Tiger", "Toad", "Trout", "Turkey", "Turtle", "Vicuna", "Viper", "Vulture", "Wallaby", "Walrus", "Wasp", "Weasel", "Whale", "Wildcat", "Wolf", "Wolverine", "Wombat", "Woodcock", "Woodpecker", "Worm", "Wren", "Yak", "Zebra" };
	private static final String mColourNames[] = new String[]{ "White", "Silver", "Gray", "Black", "Navy", "Blue", "Cerulean", "Turquoise", "Azure", "Teal", "Cyan", "Green", "Lime", "Olive", "Yellow", "Gold", "Amber", "Orange", "Brown", "Red", "Maroon", "Rose", "Pink", "Magenta", "Purple", "Indigo", "Violet", "Peach", "Apricot", "Ochre", "Plum" };
	private static final String mAdjectives[]  = new String[]{ "Adorable", "Agreeable", "Alive", "Angry", "Beautiful", "Better", "Bewildered", "Big", "Boiling", "Brave", "Breezy", "Broken", "Bumpy", "Calm", "Careful", "Chilly", "Clean", "Clever", "Clumsy", "Cold", "Colossal", "Cool", "Creepy", "Crooked", "Cuddly", "Curly", "Damaged", "Damp", "Dead", "Defeated", "Delightful", "Dirty", "Drab", "Dry", "Dusty", "Eager", "Easy", "Elegant", "Embarrassed", "Faithful", "Famous", "Fancy", "Fat", "Fierce", "Filthy", "Flaky", "Fluffy", "Freezing", "Gentle", "Gifted", "Gigantic", "Glamorous", "Great", "Grumpy", "Handsome", "Happy", "Helpful", "Helpless", "Hot", "Huge", "Immense", "Important", "Inexpensive", "Itchy", "Jealous", "Jolly", "Kind", "Large", "Lazy", "Little", "Lively", "Long", "Magnificent", "Mammoth", "Massive", "Miniature", "Mushy", "Mysterious", "Nervous", "Nice", "Obedient", "Obnoxious", "Odd", "Panicky", "Petite", "Plain", "Powerful", "Proud", "Puny", "Quaint", "Relieved", "Repulsive", "Rich", "Scary", "Scrawny", "Short", "Shy", "Silly", "Small", "Sparkling", "Tall", "Tender", "Thankful", "Thoughtless", "Tiny", "Ugliest", "Unsightly", "Uptight", "Vast", "Victorious", "Warm", "Wet", "Witty", "Worried", "Wrong", "Zealous" };

	private String getNewRandomName()
	{
		int animal    = (int)(Math.random() * (double)mAnimalNames.length);
		int colour    = (int)(Math.random() * (double)mColourNames.length);
		int adjective = (int)(Math.random() * (double)mAdjectives.length);

		return mAdjectives[adjective] +" "+ mColourNames[colour] +" "+ mAnimalNames[colour];
	}

	public class Graph {
		public State mStart;
		public ArrayList<State>        mFinish      = new ArrayList<>();
		public TreeMap<Integer,State>  mStates      = new TreeMap<>();
		public ArrayList<Transition>   mTransitions = new ArrayList<>();
	}

	public class Transition {
		State mFrom;
		State mTo;
		String mRead;

		@Override
		public String toString()
		{
			return "{" +mFrom.mName+ "}\t->\t" +(mRead == null?"\u03BB":mRead)+ "\t->\t{" +mTo.mName+ "}";
		}
	}

	public class State {
		ArrayList<Transition> mArcs = new ArrayList<>();
		int mID = -1;
		String mName = null;
	}

	public String listJoin( java.util.List<?> list, String sep )
	{
		StringBuffer buffer = new StringBuffer();

		for( int i=0; i<list.size(); i++ )
		{
			buffer.append( list.get(i) );
			if( i<list.size() )
				buffer.append( sep );
		}

		return buffer.toString();
	}

	private String getNodeValue_r( Node root, ArrayList<String> nodespec, String _default ) {
		if( nodespec.size() > 0 ) {
			String next = nodespec.remove(0);

			NodeList subNodes = root.getChildNodes();
			for( int i=0; i<subNodes.getLength(); i++ ) {
				if( subNodes.item(i).getNodeName().equalsIgnoreCase(next) ) {
					return getNodeValue_r( subNodes.item(i), nodespec, _default );
				}
			}
			return _default;
		}

		String result = root.getTextContent();
		if( result == null )
			result = _default;
		return result;
	}

	private String getNodeValue( Node root, String nodespec, String _default ) {
		String value = getNodeValue_r( root, new ArrayList<String>(Arrays.asList(nodespec.split( "\\." )) ), _default );

		System.out.println( "{" +nodespec+ " = " +(value==null?"null":value)+ "}" );

		return value;
	}

	private Graph loadJFlapFile( File file ) {
		Graph graph = new Graph();

		boolean v8 = false;
		if( file.getName().endsWith(".jflap") )
			v8 = true;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder        db  = dbf.newDocumentBuilder();
			org.w3c.dom.Document   doc = db.parse( file );

			String type = getNodeValue( doc, "structure.type", null );

			// Only bother loading PDA type automata
			if( type == null || !type.equalsIgnoreCase( "fa" ) )
				return null;

			NodeList states = doc.getElementsByTagName( "state" );
			NodeList arcs = doc.getElementsByTagName( "transition" );

			if( v8 ) {
				System.out.println( "[[[V8]]]"); // NOT SUPPORTED AT THE MOMENT - John.
			}
			else {
				for( int i=0; i<states.getLength(); i++ ) {
					State newState = new State();
					newState.mID = Integer.parseInt(states.item(i).getAttributes().getNamedItem( "id" ).getTextContent());
					newState.mName = states.item(i).getAttributes().getNamedItem( "name" ).getTextContent();

					graph.mStates.put( newState.mID, newState );
					System.out.println( "State(" +newState.mID+ ")" );

					if( getNodeValue(states.item(i), "initial", null ) != null )
						graph.mStart = newState;

					if( getNodeValue(states.item(i), "final", null ) != null )
						graph.mFinish.add( newState );
				}
   
				for( int i=0; i<arcs.getLength(); i++ ) {
					Transition transition = new Transition();

					transition.mTo   = graph.mStates.get( Integer.parseInt( getNodeValue( arcs.item(i), "to",   "-1" ) ) );
					transition.mFrom = graph.mStates.get( Integer.parseInt( getNodeValue( arcs.item(i), "from", "-1" ) ) );
					transition.mRead = getNodeValue( arcs.item(i), "read", null );

					transition.mFrom.mArcs.add( transition );

					graph.mTransitions.add( transition );
				}
			}
		}
		catch( IOException | SAXException | ParserConfigurationException err )
		{
			System.err.println( err.getMessage() );
			err.printStackTrace( System.err );
		}

		System.out.println( "Done!" );

		return graph;
	}

	/* UI variables */
	private final String mInstanceName = getNewRandomName();
	private final ByteArrayOutputStream mWinBuffer;
	private final PrintStream mWinOut;
	private final JTextPane   mWinLogPane;
	private final JLabel      mStatusLabel;
	private       File        mCurrentFile = null;

	private void clearWinLog() { mWinLogPane.setText(""); }

	private void updateWinLog( boolean append )
	{
		mWinOut.flush();
		String output = new String( mWinBuffer.toByteArray(), java.nio.charset.StandardCharsets.UTF_8 );

		if( append )
			output = mWinLogPane.getText() + output;

		mWinLogPane.setText( output );
		System.out.print( output );
		mWinBuffer.reset();
	}

	public FSMBot()
	{
		super( "FSM JFlap Interpreter" );
		setLayout( new BorderLayout() );
		setMinimumSize( new Dimension(640, 480) );

		setTitle( "FSM JFlap Interpreter (" +mInstanceName+ ")" );
		
		// Buffers
		mWinBuffer = new ByteArrayOutputStream();
		mWinOut = new PrintStream( mWinBuffer, true );

		// UI Elements
		mWinLogPane = new JTextPane();
		mWinLogPane.setFont( new Font(Font.MONOSPACED, Font.PLAIN, 10) );
		mWinLogPane.setEditable( false );
		DefaultCaret caret = (DefaultCaret)mWinLogPane.getCaret();
		caret.setUpdatePolicy( DefaultCaret.ALWAYS_UPDATE );
		
		JScrollPane scrollPane = new JScrollPane( mWinLogPane );
		scrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		add( scrollPane, BorderLayout.CENTER );

		mStatusLabel = new JLabel("Waiting...");
		add( mStatusLabel, BorderLayout.SOUTH );

		final JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
    	FileNameExtensionFilter filter = new FileNameExtensionFilter( "JFlap File", "jff" );
    	fc.setFileFilter( filter );

		JPanel toolbar = new JPanel( new FlowLayout(FlowLayout.LEFT) );
		add( toolbar, BorderLayout.NORTH );

    	final JButton openBtn = new JButton( "Load FSM" );
    	openBtn.addActionListener( new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			int returnVal = fc.showOpenDialog( null );
		    	if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
					mCurrentFile = file;

					uiLoadFile( file );
		        }
    		}
    	} );
		toolbar.add( openBtn );

		final JButton reloadGraphBtn = new JButton( "Reload FSM" );
		reloadGraphBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if( mCurrentFile == null ) {
					mWinOut.println( "No current file to reload!" );
					updateWinLog( true );
				} else {
					uiLoadFile( mCurrentFile );
				}

			}
		});
		toolbar.add( reloadGraphBtn );


        pack();
		setVisible( true );
		repaint();
	}

	private void uiLoadFile( File file ) {
		mWinOut.println( "Opening: " + file.getName() + ".\n" );
		updateWinLog( true );
		mCurrentGraph = loadJFlapFile( file );

		if( mCurrentGraph == null )
		{
			JOptionPane.showMessageDialog( null, "Sorry! I didn't understand that file!", "Parsing Error", JOptionPane.ERROR_MESSAGE );
			mStatusLabel.setText( "Parsing Error! Check you have the correct file!" );
			mWinOut.println( "Parsing Error! Check you have the correct file!" );
			updateWinLog( true );
		}
		else
		{
			mStatusLabel.setText("Using: " + file.getName());
			mWinOut.println( "Loaded '" +file.getName()+ "' successfully!" );

			mWinOut.printf( "\tStates: %d\n\tEdges: %d\n",
					mCurrentGraph.mStates.size(),
					mCurrentGraph.mTransitions.size() );

			updateWinLog( true );
		}
	}


	// ///////////////////////// //
	// ACTUAL BOT IMPLEMENTATION //
	// ///////////////////////// //
	Graph mCurrentGraph = null;
	State mCurrentState = null;

	/** Implementation of the Bot interface.
     * @see Bot
     * 
     * @param    view    View matric from the perspective of the bot, orientated so
     *                   the top of the matrix is facing the same direction as the 
     *                   bot.
     * @param    x       X coord of the bot.
     * @param    y       Y coord of the bot.
     * @param    o       Orientation of the bot @see Orientation
     * @param    fx      X coord of the finish.
     * @param    fy      Y coord of the finish.
     *
     * @return     Next move in form of Direction.####
     */
    @Override
    public int nextMove(boolean[][] view, int x, int y, int o, int fx, int fy) {
		clearWinLog();

		int maxLoops = 30;
		do {
			if( mCurrentState == null || mCurrentState.mArcs.size() == 0 )
			{
				mCurrentState = mCurrentGraph.mStart;
				mWinOut.println( "Restarted!" );
				break;
			}

			mWinOut.println( "STATE: " +mCurrentState.mName + "(" +mCurrentState.mID+ ")" );

			// Match any rules on this state //
    		ArrayList<Transition> matches = new ArrayList<>();
    		for( Transition t : mCurrentState.mArcs ) {

				mWinOut.print("\nEvaluating " + t);

				if (t.mRead == null || t.mRead.length() == 0 ) {
					mWinOut.print( " <-- MATCH!" );
					matches.add( t );
				} else {
					try {
						// This is probably a terrible way of doing it, but it's unclear
						ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName( "JavaScript" );
						String sensors[] = {
								"FrontLeft = "  +!view[0][0],
								"Front = "      +!view[1][0],
								"FrontRight = " +!view[2][0],
								"Right = "      +!view[2][1],
								"BackRight = "  +!view[2][2],
								"Back = "       +!view[1][2],
								"BackLeft = "   +!view[0][2],
								"Left = "       +!view[0][1]
						};
						for ( String sensor : sensors )
							scriptEngine.eval(sensor);

						if( (boolean)scriptEngine.eval(t.mRead) == true ) {
							mWinOut.print( " <-- MATCH!" );
							matches.add( t );
						}

					} catch (ScriptException e) {
						e.printStackTrace();
						mWinOut.print( " <-- " );
						mWinOut.print( e.getMessage() );
					}
				}

    		}
    		mWinOut.println( "" );

			if( matches.size() > 0 ) {
				int index = ThreadLocalRandom.current().nextInt(matches.size());
				Transition action = matches.get(index);

				mWinOut.println((matches.size() > 1 ? "Deterministic     [ ]\nNon Deterministic [X]\n" : "Deterministic     [X]\nNon Deterministic [ ]\n"));
				mWinOut.println("Chose: " + action.toString());

    			mCurrentState = action.mTo;
			}
    	} while( maxLoops-- > 0 && !mCurrentState.mName.matches("^[FfBbLlRr]$") && mCurrentState.mArcs.size() != 0 );
    	mWinOut.println( "" );

		if( maxLoops < 1 )
			mWinOut.println( "Reaching maximum loop count, breakout of of this iteration! (will continue next tick)" );

    	int movement = -1;
		if( mCurrentState != null )
		{
	    	if( mCurrentState.mName.equalsIgnoreCase( "F" ) ) // Forward
				movement = Direction.FORWARD;
	    	else if( mCurrentState.mName.equalsIgnoreCase( "B" ) ) // Backwards
				movement = Direction.BACK;
	    	else if( mCurrentState.mName.equalsIgnoreCase( "L" ) ) // Turn left
				movement = Direction.LEFT;
	    	else if( mCurrentState.mName.equalsIgnoreCase( "R" ) ) // Turn right
				movement = Direction.RIGHT;
	    	else
	    	{
				if( mCurrentState.mArcs.size() == 0 ) {
					mWinOut.println("Got stuck! Could not path out of this point in the graph!");
					mWinOut.println("The graph will restart from the START state.");
					mCurrentState = null;
				} else
					mWinOut.println( "Nothing to do..." );
	    	}
	    }

    	mWinOut.println( "Action: " +Direction.getName(movement) );

    	mWinOut.println( "[FINISH]" );
    	updateWinLog( false );
	
    	return movement;
    }

    /** Implementation of the Bot interface.
     *
     * @return           Bot name.
     */
    @Override
    public String getName(){
        return "FSMBot - " +mInstanceName;
    }

    /** Implementation of the Bot interface.
     *
     * @return           Bot Description.
     */
    @Override
    public String getDescription(){
        return mInstanceName + ", a bot using logic from a JFlap graph";
    }


    @Override
    public void start(){
		mCurrentState = null;
    }

    @Override
    public void destroy() {
        setVisible( false );
        dispose();
    }

}