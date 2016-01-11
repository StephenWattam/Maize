package bots.PDABot.model;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by john on 11/01/16.
 */
public class JFlapLoader {

    private static String getNodeValue_r(Node root, ArrayList<String> nodespec, String _default ) {
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

    private static String getNodeValue( Node root, String nodespec, String _default ) {
        String value = getNodeValue_r( root, new ArrayList<String>(Arrays.asList(nodespec.split( "\\." )) ), _default );

        System.out.println( "{" +nodespec+ " = " +(value==null?"null":value)+ "}" );

        return value;
    }

    public static Graph loadJFlapFile( File file ) {
        Graph graph = new Graph();

        boolean v8 = false;
        if( file.getName().endsWith(".jflap") )
            v8 = true;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db  = dbf.newDocumentBuilder();
            org.w3c.dom.Document   doc = db.parse( file );

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
}
