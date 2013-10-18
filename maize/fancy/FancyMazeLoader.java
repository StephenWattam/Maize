package maize.fancy;

import maize.*;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.System;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Loads fancy 'mzip' mazes.
 *
 * @author John Vidler
 */
public class FancyMazeLoader
{

    public static Maze load( File source ) throws Exception
    {
        System.out.println( "Opening '" +source.getName()+ "'..." );

        String name = source.getName().replace( ".mzip", "" );

        ZipFile mzip = new ZipFile( source, ZipFile.OPEN_READ );
        ZipEntry zipMap = mzip.getEntry( name+".js" );

        FancyMaze newMazeInstance = FancyMazeLoader.loadMazeData( mzip, zipMap );

        return newMazeInstance;
    }

    protected static FancyMaze loadMazeData( ZipFile source, ZipEntry zipMap ) throws IOException
    {
        Reader in = new InputStreamReader( source.getInputStream( zipMap ) );
        JSONParser jsonParser = new JSONParser();

        try
        {
            System.out.println( "Parsing..." );
            JSONObject root = (JSONObject)jsonParser.parse( in );

            System.out.println( root );

            // Check this map is the correct orientation!
            if( !root.get( "orientation").toString().equals( "orthogonal" ) )
                throw new IOException( "Map is not an orthoganal-format map! Sorry, I can't load these!" );

            // Pull some metadata
            JSONObject zMapProperties = (JSONObject)root.get( "properties" );
            String mapAuthor = "" + zMapProperties.get( "author" );
            String mapName = "" + zMapProperties.get( "name" );

            TileSet mapTileSet = new TileSet();
            JSONArray zTileSets = (JSONArray)root.get( "tilesets" );
            for( int set = 0; set < zTileSets.size(); set++ )
            {
                JSONObject zTileSet = (JSONObject)zTileSets.get( set );
                String imageFile = ""+zTileSet.get( "image" );
                int offset = Integer.parseInt( "" + zTileSet.get( "firstgid" ) );
                int cols = Integer.parseInt( ""+ zTileSet.get( "imagewidth" ) ) / Integer.parseInt( ""+ zTileSet.get( "tilewidth" ) );
                int rows = Integer.parseInt( ""+ zTileSet.get( "imageheight" ) ) / Integer.parseInt( ""+ zTileSet.get( "tileheight" ) );

                mapTileSet.loadImage( offset, ImageIO.read( source.getInputStream( source.getEntry( imageFile ) ) ), cols, rows );

                JSONObject zTileProperties = (JSONObject)zTileSet.get( "tileproperties" );
                if( zTileProperties != null )
                {
                    for( Object tile : zTileProperties.keySet() )
                    {
                        JSONObject properties = (JSONObject)zTileProperties.get( ""+tile );
                        for( Object key : properties.keySet() )
                            mapTileSet.setTileProperty( offset + Integer.parseInt( ""+tile ), ""+key, ""+properties.get( ""+key ) );
                    }
                }
            }

            // Pull the map dimensions
            int width = Integer.parseInt( ""+root.get( "width" ) );
            int height = Integer.parseInt( ""+root.get( "height" ) );

            int tileData[][]    = new int[width][height];
            boolean mapData[][] = new boolean[width][height];
            int startX = 0, startY = 0, endX = 0, endY = 0;

            // Pull all the layers, and construct the map data arrays.
            JSONArray zLayers = (JSONArray)root.get( "layers" );
            for( int layer = 0; layer < zLayers.size(); layer++ )
            {
                JSONObject zLayer = (JSONObject)zLayers.get( layer );

                String zLayerType = ""+zLayer.get( "type" );
                String zLayerClass = ""+zLayer.get( "name" );
                JSONArray zLayerData = (JSONArray)zLayer.get( "data" );

                // Skip non-tile layers!
                if( !zLayerType.equalsIgnoreCase( "tilelayer" ) )
                    continue;

                for( int y=0; y<height; y++ )
                {
                    for( int x=0; x<width; x++ )
                    {
                        if( zLayerClass.equalsIgnoreCase( "terrain" ) )
                        {
                            tileData[x][y] = Integer.parseInt( ""+zLayerData.get( x+(y*width) ) );
                            mapData[x][y] = mapTileSet.getTileFlag( tileData[x][y], "wall" );
                        }
                        else if( zLayerClass.equalsIgnoreCase( "entities" ) )
                        {
                            int entityID = Integer.parseInt( ""+zLayerData.get( x+(y*width) ) );
                            String entityType = mapTileSet.getTileProperty( entityID, "entity" );

                            if( entityType != null )
                            {
                                System.out.println( "Type: " +entityType );
                                if( entityType.equalsIgnoreCase( "start" ) )
                                {
                                    startX = x;
                                    startY = y;
                                    System.out.println( "Set start to " +x+ "," +y );
                                }
                                else if( entityType.equalsIgnoreCase( "end" ) )
                                {
                                    endX = x;
                                    endY = y;
                                    System.out.println( "Set end to " +x+ "," +y );
                                }
                            }
                        }
                    }
                }
            }

            // ToDo: Use map data for entrance/exit!
            FancyMaze newMaze = new FancyMaze( mapName, mapData, tileData, mapTileSet, startX, startY, endX, endY );

            return newMaze;
        }
        catch( ParseException parseErr )
        {
            System.err.println( parseErr );
        }

        in.close();

        return null;
    }

}