package bots;
import maize.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.xml.parsers.*;
import javax.xml.parsers.*;

public class JFlapBot extends JFrame implements Bot {

	public class Graph {
		public State mStart;
		public ArrayList<State>       mFinish       = new ArrayList<>();
		public TreeMap<Integer,State> mStates       = new TreeMap<>();
		public ArrayList<Transition>   mTransitions = new ArrayList<>();
	}

	public class Transition {
		State mFrom;
		State mTo;
		ArrayList<Character> mRead = new ArrayList<>();
		ArrayList<Character> mPush = new ArrayList<>();
		ArrayList<Character> mPop = new ArrayList<>();

		@Override
		public String toString()
		{
			return "{" +mFrom.mName+ "}\t" +listJoin(mRead, "")+ "\t/\t" +listJoin(mPop, "")+ "\t/\t" +listJoin(mPush, "")+ "\t{" +mTo.mName+ "}";
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
			Document               doc = db.parse( file );

			String type = getNodeValue( doc, "structure.type", null );

			// Only bother loading PDA type automata
			if( type == null || !type.equalsIgnoreCase( "pda" ) )
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

					transition.mRead = new ArrayList<Character>();
					for( char c : getNodeValue( arcs.item(i), "read", null ).toCharArray() )
						transition.mRead.add( c );

					transition.mFrom.mArcs.add( transition );

					transition.mPush = new ArrayList<Character>();
					for( char c : getNodeValue( arcs.item(i), "push", null ).toCharArray() )
						transition.mPush.add( c );

					transition.mPop = new ArrayList<Character>();
					for( char c : getNodeValue( arcs.item(i), "pop", null ).toCharArray() )
						transition.mPop.add( c );					

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

	public JFlapBot()
	{
		super( "JFlap Interpreter" );
		setLayout( new BorderLayout() );
		
    	final JFileChooser fc = new JFileChooser();
    	FileNameExtensionFilter filter = new FileNameExtensionFilter( "JFlap File", "jff", "jflap" );
    	fc.setFileFilter( filter );

    	final JButton openBtn = new JButton( "Select JFlap Graph" );
    	openBtn.addActionListener( new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			int returnVal = fc.showOpenDialog( null );
		    	if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            
		            System.out.println( "Opening: " + file.getName() + ".\n" );
		            mCurrentGraph = loadJFlapFile( file );

		            if( mCurrentGraph == null )
		            	JOptionPane.showMessageDialog(null, "Sorry! I didn't understand that file!", "Parsing Error", JOptionPane.ERROR_MESSAGE);
		        }
    		}
    	} );
    	add( openBtn, BorderLayout.CENTER );

        pack();
		setVisible( true );
	}


	// ///////////////////////// //
	// ACTUAL BOT IMPLEMENTATION //
	// ///////////////////////// //
	ConcurrentLinkedQueue<Integer> mActionList = new ConcurrentLinkedQueue<Integer>();
	Graph mCurrentGraph = null;
	State mCurrentState = null;
	Stack<Character> mStack = null;
	ArrayList<Character> mInputTape = null;
	int mInputCursor = 0;

	private Character readTape()
	{
		if( mInputCursor > -1 && mInputCursor < mInputTape.size() )
			return mInputTape.get(mInputCursor++);
		return ' ';
	}

	private Character peekTape( int offset )
	{
		if( mInputCursor > -1 && mInputCursor < mInputTape.size() )
			return mInputTape.get(mInputCursor+offset);
		return ' ';
	}

	private int remainingTape()
	{
		return mInputTape.size() - mInputCursor;
	}

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
    	System.out.println( "\n" );

    	if( mActionList.size() > 0 )
    	{
    		int action = mActionList.remove();

    		switch( action )
    		{
    			case Direction.FORWARD: System.out.println( "Action: FORWARD" ); break;
    			case Direction.BACK:    System.out.println( "Action: BACK" );    break;
    			case Direction.LEFT:    System.out.println( "Action: LEFT" );    break;
    			case Direction.RIGHT:   System.out.println( "Action: RIGHT" );   break;
    		}

    		return action;
    	}

    	if( mCurrentGraph != null )
    	{
    		// Load sensor data  - Note: These may need to be rearranged! //
    		mInputTape = new ArrayList<>();
    		mInputCursor = 0;
    		mInputTape.add( (view[0][0]?'1':'0') );
    		mInputTape.add( (view[1][0]?'1':'0') );
    		mInputTape.add( (view[2][0]?'1':'0') );
    		mInputTape.add( (view[2][1]?'1':'0') );
    		mInputTape.add( (view[2][2]?'1':'0') );
    		mInputTape.add( (view[1][2]?'1':'0') );
    		mInputTape.add( (view[0][2]?'1':'0') );
    		mInputTape.add( (view[0][1]?'1':'0') );

			// Reset stack //
	    	mStack = new Stack<>();

    		do {
    			System.out.println( "STATE: " +mCurrentState.mName + "(" +mCurrentState.mID+ ")" );

    			System.out.println( "STACK: " +listJoin(mStack, "") );

    			System.out.println( "TAPE:  " +listJoin(mInputTape, "") );
    			System.out.print(   "       " );
    			for( int i=0; i<mInputCursor; i++ )
    				System.out.print( " " );
    			System.out.println( "^" );

	    		// Match any rules on this state //
	    		ArrayList<Transition> matches = new ArrayList<>();
	    		for( Transition t : mCurrentState.mArcs ) {

	    			System.out.print( "\nMatching " + t );

	    			// Does this rule require a read?
	    			if( t.mRead.size() > 0 )
	    			{
	    				// Is there enough data to make this possible on the tape?
		    			if( t.mRead.size() > remainingTape() )
		    				continue;

		    			// Does it all match?
		    			boolean readOK = true;
		    			for( int i=0; i<t.mRead.size(); i++ )
		    			{
		    				if( t.mRead.get(i) != '?' && (int)t.mRead.get(i) != (int)peekTape(i) )
		    				{
		    					readOK = false;
		    					break;
		    				}
		    			}
		    			if( !readOK )
		    				continue;
		    		}
	    			
	    			// Does this rule require a stack pop?
	    			if( t.mPop.size() > 0 )
	    			{
	    				// Does the request fit?
	    				if( t.mPop.size() > mStack.size() )
	    					continue;
	    			}

	    			System.out.print( " <-- MATCH!" );
	    			matches.add( t );
	    		}
	    		System.out.println( "" );

	    		int index = (int)(Math.random() * (matches.size()-1));
	    		Transition action = matches.get( index );

	    		System.out.println( "Performing: " + action );

	    		for( Character c : action.mRead )
	    			readTape();

	    		for( Character c : action.mPop ) {
	    			if( !(""+c).equalsIgnoreCase( ""+mStack.peek()) )
	    				System.out.println( "SANITY CHECK FAIL: " +c+ " IS NOT " + mStack.peek() );
	    		}

	    		for( Character c : action.mPush )
	    			mStack.push( c );

	    		while( mStack.size() > 0 )
	    		{
	    			char c = Character.toLowerCase( mStack.pop() );

	    			switch( c )
	    			{
	    				case 'f':
	    					mActionList.add( Direction.FORWARD );
	    					System.out.print( " {FWD} " );
	    					break;

	    				case 'b':
	    					mActionList.add( Direction.BACK );
	    					System.out.print( " {BACK} " );
	    					break;

	    				case 'l':
	    					mActionList.add( Direction.LEFT );
	    					System.out.print( " {LEFT} " );
	    					break;

	    				case 'r':
	    					mActionList.add( Direction.RIGHT );
	    					System.out.print( " {RIGHT} " );
	    					break;

	    				default:
	    					System.out.println( "Invalid action: '" +c+ "'" );
	    			}
	    		}

	    		mCurrentState = action.mTo;
	    	} while( !mCurrentGraph.mFinish.contains( mCurrentState ) );
	    	mCurrentState = mCurrentGraph.mStart;

	    	System.out.println( "[FINISH]" );
    	}
    	return Direction.FORWARD;
    }

    /** Implementation of the Bot interface.
     *
     * @return           Bot name.
     */
    @Override
    public String getName(){
        return "JFlapBot";
    }

    /** Implementation of the Bot interface.
     *
     * @return           Bot Description.
     */
    @Override
    public String getDescription(){
        return "A bot using logic from a JFlap graph";
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