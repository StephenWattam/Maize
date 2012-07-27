package maize;

/** Creates an empty maze for the purposes of testing */
public class EmptyMazeFactory implements MazeFactory {

  /** Maze width */
  private int width = 0;

  /** Maze height */
  private int height = 0;

  /** Default constructor. */
  public EmptyMazeFactory() { }

  /** Create the empty maze.
    *
    * @param    width       Width of the maze.
    * @param    height      Width of the maze.
    *
    * @return               Maze instance
    */
  public Maze getMaze(int width, int height){

    if(width < 3 || height < 3){
      this.width = 2;
      this.height = 2;
    }

    this.width = width;
    this.height = height;

    boolean[][] data = new boolean[this.width][this.height];

    for(int i =0; i < this.width; i++){
      for(int j = 0; j < this.height; j++){
        data[i][j] = (i == 0 || j == 0 || i == this.width-1 || j == this.height-1)? true : false;
      }
    }

    return new Maze(data, width, height, 1 + (int)(Math.random() * width-3), 1 + (int)(Math.random() * height-3), width-2, height-2);
  } 
}
