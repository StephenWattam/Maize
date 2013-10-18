package maize.fancy;

import java.awt.image.BufferedImage;
import java.lang.*;
import java.lang.Integer;
import java.lang.String;
import java.lang.System;
import java.lang.Throwable;
import java.util.HashMap;
import java.util.TreeMap;

/**
 *
 * @author John Vidler
 * @version 1
 */
public class TileSet
{
    protected TreeMap<Integer, BufferedImage> mTiles = null;
    protected TreeMap<Integer, TreeMap<String, String>> mTileProperties = null;

    public TileSet()
    {
        mTiles = new TreeMap<Integer, BufferedImage>();
        mTileProperties = new TreeMap<Integer, TreeMap<String, String>>();
    }

    public void loadImage( int offset, BufferedImage source, int cols, int rows )
    {
        int width = source.getWidth() / cols;
        int height = source.getHeight() / rows;

        System.out.println( "[" +width+ "x" +height+ "]" );

        for( int y=0; y<rows; y++ )
            for( int x=0; x<cols; x++ )
                mTiles.put( offset + x + (y*cols), source.getSubimage( x*width, y*height, width, height ) );
    }

    public BufferedImage getTile( int index )
    {
        return mTiles.get( index );
    }

    public TreeMap<String,String> getTileProperties( int index )
    {
        if( !mTileProperties.containsKey( index ) )
            return null;
        return mTileProperties.get( index );
    }

    public String getTileProperty( int index, String property, String _default )
    {
        if( !mTileProperties.containsKey( index ) )
            return _default;
        if( !mTileProperties.get( index ).containsKey( property ) )
            return _default;
        return mTileProperties.get( index ).get( property );
    }

    public String getTileProperty( int index, String property )
    {
        return getTileProperty( index, property, null );
    }

    public Boolean getTileFlag( int index, String property )
    {
        String value = getTileProperty( index, property );
        if( value == null )
            return false;
        return value.equals( "1" );
    }

    public void setTileProperty( int index, String property, String value )
    {
        System.out.println( "#" +index+ " -> '" +property+ "' = '" +value+ "'" );
        if( !mTileProperties.containsKey( index ) )
            mTileProperties.put( index, new TreeMap<String, String>() );
        mTileProperties.get( index ).put( property, value );
    }
}