package bots;
import maize.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import java.util.*;
import java.util.regex.*;
import java.util.concurrent.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.text.*;
import javax.xml.parsers.*;
import javax.xml.parsers.*;

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

    	final JFileChooser fc = new JFileChooser();
    	FileNameExtensionFilter filter = new FileNameExtensionFilter( "JFlap File", "jff" );
    	fc.setFileFilter( filter );

    	final JButton openBtn = new JButton( "Load JFlap File" );
    	openBtn.addActionListener( new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			int returnVal = fc.showOpenDialog( null );
		    	if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            
		            mWinOut.println( "Opening: " + file.getName() + ".\n" );
		            mCurrentGraph = loadJFlapFile( file );

		            if( mCurrentGraph == null )
		            {
		            	JOptionPane.showMessageDialog(null, "Sorry! I didn't understand that file!", "Parsing Error", JOptionPane.ERROR_MESSAGE);
		            	mStatusLabel.setText( "Parsing Error! Check you have the correct file!" );
		            }
		            else
		            	mStatusLabel.setText( "Using: " +file.getName() );
		        }
    		}
    	} );
    	add( openBtn, BorderLayout.NORTH );

        pack();
		setVisible( true );
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

    	Pattern arcRegex = Pattern.compile( "^(!?)([FfBbLlRr][FfBbLlRr]?)$" );

    	if( mCurrentState == null || mCurrentState.mArcs.size() == 0 )
    	{
    		mCurrentState = mCurrentGraph.mStart;
    		mWinOut.println( "Restarted!" );
    	}
    	else
    		mWinOut.println( "Continuing..." );

		do {
			mWinOut.println( "STATE: " +mCurrentState.mName + "(" +mCurrentState.mID+ ")" );

			// Catch corner case where state has no arcs
			if( mCurrentState.mArcs.size() == 0 )
			{
				mWinOut.println( "Could not evaluate any arcs!" );
				break;
			}

			// Match any rules on this state //
    		ArrayList<Transition> matches = new ArrayList<>();
    		for( Transition t : mCurrentState.mArcs ) {

    			mWinOut.print( "\nEvaluating " + t );

    			Matcher search = arcRegex.matcher( t.mRead );
    			if( search.matches() ) // Is this a matching rule?
    			{
    				boolean sensorState = true;
    				if( search.group(1).equalsIgnoreCase("!") )
    					sensorState = false;

    				String direction = search.group(2).toLowerCase();

    				if( direction.equals("fl") && view[0][0] == sensorState )
    				{
    					mWinOut.print( " <-- MATCH!" );
    					matches.add( t );
    				}
    				else if( direction.equals("f") && view[1][0] == sensorState  )
    				{
    					mWinOut.print( " <-- MATCH!" );
    					matches.add( t );
    				}
    				else if( direction.equals("fr") && view[2][0] == sensorState  )
    				{
    					mWinOut.print( " <-- MATCH!" );
    					matches.add( t );
    				}
    				else if( direction.equals("r") && view[2][1] == sensorState  )
    				{
    					mWinOut.print( " <-- MATCH!" );
    					matches.add( t );
    				}
    				else if( direction.equals("br") && view[2][2] == sensorState  )
    				{
    					mWinOut.print( " <-- MATCH!" );
    					matches.add( t );
    				}
    				else if( direction.equals("b") && view[1][2] == sensorState  )
    				{
    					mWinOut.print( " <-- MATCH!" );
    					matches.add( t );
    				}
    				else if( direction.equals("bl") && view[0][2] == sensorState  )
    				{
    					mWinOut.print( " <-- MATCH!" );
    					matches.add( t );
    				}
    				else if( direction.equals("l") && view[0][1] == sensorState  )
    				{
    					mWinOut.print( " <-- MATCH!" );
    					matches.add( t );
    				}
    			}
    			else
    			{
    				mWinOut.println( "Rule was not matchable, could not do anything :(" );
    			}
    		}
    		mWinOut.println( "" );

    		int index = (int)(Math.random() * (matches.size()-1));
    		Transition action = matches.get( index );

    		mWinOut.println( (matches.size() > 1?"Deterministic     [ ]\nNon Deterministic [X]\n":"Deterministic     [X]\nNon Deterministic [ ]\n") );
    		mWinOut.println( "Chose: " + action.toString() );

    		updateWinLog( true );

    		mCurrentState = action.mTo;
    	} while( !mCurrentState.mName.matches("^[FfBbLlRr]$") );
    	mWinOut.println( "" );

    	int action = -1;
		if( mCurrentState != null )
		{
	    	if( mCurrentState.mName.equalsIgnoreCase( "F" ) ) // Forward
	    		action = Direction.FORWARD;
	    	else if( mCurrentState.mName.equalsIgnoreCase( "B" ) ) // Backwards
	    		action = Direction.BACK;
	    	else if( mCurrentState.mName.equalsIgnoreCase( "L" ) ) // Turn left
	    		action = Direction.LEFT;
	    	else if( mCurrentState.mName.equalsIgnoreCase( "R" ) ) // Turn right
	    		action = Direction.RIGHT;
	    	else
	    	{
	    		mWinOut.println( "Got stuck! Could not path out of this point in the graph!" );
	    		mWinOut.println( "The graph will restart from the START state." );
	    		mCurrentGraph = null;
	    	}
	    }

    	mWinOut.println( "Action: " +Direction.getName(action) );

    	mWinOut.println( "[FINISH]" );
    	updateWinLog( false );
	
    	return action;
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
    	if( mCurrentGraph != null ) {
    		mCurrentState = mCurrentGraph.mStart;
    	}
    }

    @Override
    public void destroy() {
        setVisible( false );
        dispose();
    }

}