package maize.fancy;

import maize.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public class FancyMaze extends Maze
{
    private int mTileData[][] = null;
    private TileSet mTileset  = null;

    private BufferedImage mCompositeStart = null;
    private BufferedImage mCompositeEnd   = null;

    public FancyMaze( String name, boolean[][] data, int tiles[][], TileSet tileSet, int entX, int entY, int exiX, int exiY )
    {
        super( data, entX, entY, exiX, exiY );

        this.setName( name );
        mTileData = tiles.clone();
        mTileset = tileSet;
    }

    public BufferedImage getTile( int x, int y )
    {
        System.out.println( "Lookup: " +mTileData[x][y] );
        return mTileset.getTile( mTileData[x][y] );
    }

    //public
}