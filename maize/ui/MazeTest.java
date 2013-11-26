package maize.ui;
import maize.*;
import java.util.*;


public class MazeTest{
    
    public Vector<Maze> mazes = new Vector<Maze>();

    public Vector<Bot> bots = new Vector<Bot>();

    public Vector<MazeFactory> factories = new Vector<MazeFactory>();

    public Comparator<Maze> mazeNameComparator = new Comparator<Maze>(){
        @Override
        public int compare( Maze maze, Maze maze2 )
        {
            return maze.getName().compareTo( maze2.getName() );
        }
    };

    public Comparator<Bot> botNameComparator = new Comparator<Bot>(){
        @Override
        public int compare( Bot bot, Bot bot2 )
        {
            return bot.getName().compareTo( bot2.getName() );
        }
    };

    public Comparator<MazeFactory> factoryNameComparator = new Comparator<MazeFactory>(){
        @Override
        public int compare( MazeFactory mazeFactory, MazeFactory mazeFactory2 )
        {
            return mazeFactory.getName().compareTo( mazeFactory2.getName() );
        }
    };

    public void sortMazes()
    {
        Collections.sort( mazes, mazeNameComparator );
    }
    public void sortBots()
    {
        Collections.sort( bots, botNameComparator );
    }

    public void sortFactories()
    {
        Collections.sort( factories, factoryNameComparator );
    }

}
