package bots;
import maize.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import java.util.*;
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

		public Transition() {

		}
	}

	public class State {
		ArrayList<Transition> mArcs = new ArrayList<>();
		int mID = -1;
		String mName = null;

		public State() {
		}
	}

	private TreeMap<Integer,State>        mKnownStates      = new TreeMap<>();
	

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
		return getNodeValue_r( root, new ArrayList<String>(Arrays.asList(nodespec.split( "\\." )) ), _default );
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

					if( getNodeValue(states.item(i), "initial", null ) != null )
						graph.mStart = newState;

					if( getNodeValue(states.item(i), "initial", null ) != null )
						graph.mFinish.add( newState );
				}
   
				for( int i=0; i<arcs.getLength(); i++ ) {
					Transition transition = new Transition();
					transition.mTo   = mKnownStates.get( Integer.parseInt( getNodeValue( arcs.item(i), "to",   "-1" ) ) );
					transition.mFrom = mKnownStates.get( Integer.parseInt( getNodeValue( arcs.item(i), "from", "-1" ) ) );

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
		            Graph graph = loadJFlapFile( file );
		        }
    		}
    	} );
    	add( openBtn );

        pack();
		setVisible( true );
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
    	
    }

    @Override
    public void destroy() {
        setVisible( false );
        dispose();
    }

}