package bots.FSMBot;

public abstract class Utility
{

    public static String listJoin( java.util.List<?> list, String sep )
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

}
