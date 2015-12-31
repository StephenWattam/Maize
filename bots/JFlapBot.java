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
		ArrayList<Character> mPush = new ArrayList<>();
		ArrayList<Character> mPop = new ArrayList<>();
	}

	public class State {
		ArrayList<Transition> mArcs = new ArrayList<>();
		int mID = -1;
		String mName = null;
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

					System.out.println( transition );
					System.out.println( transition.mFrom );
					System.out.println( transition.mFrom.mArcs );

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
		        }
    		}
    	} );
    	add( openBtn );

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
    	if( mActionList.size() > 0 )
    		return mActionList.remove();

    	if( mCurrentGraph != null )
    	{
    		System.out.println( "[START] -> " );

    		do {
    			System.out.print( mCurrentState.mName + " -> " );

	    		// Load sensor data //
	    		ArrayList<Character> mInputTape = new ArrayList<>();



	    		// Reset stack //
	    		mStack = new Stack<>();


	    		ArrayList<Transition> matches = new ArrayList<>();
	    		for( Transition t : mCurrentState.mArcs ) {
	    			if( t.mPop.size() == 0 )
	    				matches.add( t );
	    		}

	    		int index = (int)(Math.random() * (matches.size()-1));
	    		Transition action = matches.get( index );

	    		for( Character c : action.mPop ) {
	    			if( !(""+c).equalsIgnoreCase( ""+mStack.peek()) )
	    				System.out.println( "SANITY CHECK FAIL: " +c+ " IS NOT " + mStack.peek() );

	    			mStack.pop();
	    		}

	    		for( Character c : action.mPush ) {
	    			mStack.push( c );
	    		}

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