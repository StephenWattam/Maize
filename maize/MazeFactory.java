package maize;

/** Interface for Maze generators */
public interface MazeFactory {

  /** This method is implemented to create the maze data and return it encapsulated in a Maze instance.
    *
    * @param    width       Width of the maze.
    * @param    height      Width of the maze.
    *
    * @return               Maze instance
    */
  public Maze getMaze(int width, int height);

}
