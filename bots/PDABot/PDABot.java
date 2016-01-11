package bots.PDABot;
import bots.PDABot.model.Graph;
import bots.PDABot.model.JFlapLoader;
import bots.PDABot.model.State;
import bots.PDABot.model.Transition;
import maize.*;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.text.*;

public class PDABot extends JFrame implements Bot {

	/* UI variables */
	private final String mInstanceName = NameGen.getNewRandomName();
	private final ByteArrayOutputStream mWinBuffer;
	private final PrintStream mWinOut;
	private final JTextPane mWinLogPane;

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

	public PDABot()
	{
		super( "PDA JFlap Interpreter" );
		setLayout( new BorderLayout() );
		setMinimumSize( new Dimension(640, 480) );

		setTitle( "PDA JFlap Interpreter (" +mInstanceName+ ")" );

		// Buffers
		mWinBuffer = new ByteArrayOutputStream();
		mWinOut = new PrintStream( mWinBuffer, true );

		// UI Elements
		mWinLogPane = new JTextPane();
		mWinLogPane.setFont( new Font(Font.MONOSPACED, Font.PLAIN, 10) );
		mWinLogPane.setEditable( false );
		DefaultCaret caret = (DefaultCaret)mWinLogPane.getCaret();
		caret.setUpdatePolicy( DefaultCaret.ALWAYS_UPDATE );
		add( mWinLogPane, BorderLayout.CENTER );

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
		            mCurrentGraph = JFlapLoader.loadJFlapFile( file );

		            if( mCurrentGraph == null )
		            	JOptionPane.showMessageDialog(null, "Sorry! I didn't understand that file!", "Parsing Error", JOptionPane.ERROR_MESSAGE);
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
    	mWinOut.println( "\n" );

    	if( mActionList.size() > 0 )
    	{
    		int action = mActionList.poll();

    		switch( action )
    		{
    			case Direction.FORWARD: mWinOut.println( "Action: FORWARD" ); break;
    			case Direction.BACK:    mWinOut.println( "Action: BACK" );    break;
    			case Direction.LEFT:    mWinOut.println( "Action: LEFT" );    break;
    			case Direction.RIGHT:   mWinOut.println( "Action: RIGHT" );   break;
    		}

    		updateWinLog( true );

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
    			mWinOut.println( "STATE: " +mCurrentState.getName() + "(" +mCurrentState.getID()+ ")" );

    			mWinOut.println( "STACK: " +Utility.listJoin(mStack, "") );

    			mWinOut.println( "TAPE:  " +Utility.listJoin(mInputTape, "") );
    			mWinOut.print(   "       " );
    			for( int i=0; i<mInputCursor; i++ )
    				mWinOut.print( " " );
    			mWinOut.println( "^" );

	    		// Match any rules on this state //
	    		ArrayList<Transition> matches = new ArrayList<>();
	    		for( Transition t : mCurrentState.getTransitions() ) {

	    			mWinOut.print( "\nEvaluating " + t );

	    			// Does this rule require a read?
	    			if( t.getRead().size() > 0 )
	    			{
	    				// Is there enough data to make this possible on the tape?
		    			if( t.getRead().size() > remainingTape() )
		    				continue;

		    			// Does it all match?
		    			boolean readOK = true;
		    			for( int i=0; i<t.getRead().size(); i++ )
		    			{
		    				if( t.getRead().get(i) != '?' && (int)t.getRead().get(i) != (int)peekTape(i) )
		    				{
		    					readOK = false;
		    					break;
		    				}
		    			}
		    			if( !readOK )
		    				continue;
		    		}
	    			
	    			// Does this rule require a stack pop?
	    			if( t.getPop().size() > 0 )
	    			{
	    				// Does the request fit?
	    				if( t.getPop().size() > mStack.size() )
	    					continue;

	    				ListIterator<Character> stackIter = (ListIterator<Character>)mStack.listIterator();
	    				for( Character c : t.getPop() )
	    					if( !(""+c).equalsIgnoreCase( ""+stackIter.next() ) )
	    						continue;
	    			}

	    			mWinOut.print( " <-- MATCH!" );
	    			matches.add( t );
	    		}
	    		mWinOut.println( "" );

	    		int index = (int)(Math.random() * (matches.size()-1));
	    		Transition action = matches.get( index );

	    		mWinOut.println( "\nPushing Command: " +action );
	    		mWinOut.println( (matches.size() > 1?"Deterministic     [ ]\nNon Deterministic [X]\n":"Deterministic     [X]\nNon Deterministic [ ]\n") );

	    		for( Character c : action.getRead() )
	    			readTape();

	    		for( Character c : action.getPop() ) {
	    			if( (""+c).equalsIgnoreCase( ""+mStack.pop()) )
	    				mWinOut.println( "Bad stack pop, something is very wrong!" );
	    		}

	    		for( Character c : action.getPush() )
	    			mStack.push( c );

	    		mCurrentState = action.getTo();
	    	} while( !mCurrentGraph.mFinish.contains( mCurrentState ) );
	    	mCurrentState = mCurrentGraph.mStart;

	    	while( mStack.size() > 0 )
    		{
    			char c = Character.toLowerCase( mStack.pop() );

    			switch( c )
    			{
    				case 'f':
    					mActionList.add( Direction.FORWARD );
    					mWinOut.println( "Moving FORWARD!" );
    					break;

    				case 'b':
    					mActionList.add( Direction.BACK );
    					mWinOut.println( "Moving BACK!" );
    					break;

    				case 'l':
    					mActionList.add( Direction.LEFT );
    					mWinOut.println( "Moving LEFT!" );
    					break;

    				case 'r':
    					mActionList.add( Direction.RIGHT );
    					mWinOut.println( "Moving RIGHT!" );
    					break;

    				default:
    					//mWinOut.println( "Invalid action: '" +c+ "'" );
    			}
    		}

	    	mWinOut.println( "[FINISH]" );
	    	updateWinLog( false );
    	}
    	return -1;
    }

    /** Implementation of the Bot interface.
     *
     * @return           Bot name.
     */
    @Override
    public String getName(){
        return "JFlapBot - " +mInstanceName;
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