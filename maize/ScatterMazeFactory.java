package maize;

/** Creates an maze made of scattered bricks */
public class ScatterMazeFactory implements MazeFactory {

  /** Maze width */
  private int width = 0;

  /** Maze height */
  private int height = 0;

  /** Maze data */
  private boolean[][] data = null;

  /** Default Constructor */
  public ScatterMazeFactory() { }

  /** Public method for getting the maze.
   *
   * @param    width       Width of the maze.
   * @param    height      Width of the maze.
   *
   * @return         Maze object.
   */
  public Maze getMaze(int width, int height){

    /* Check for sane values, otherwise use defaults. */
    if(width < 7 || height < 7){

      this.width = 7;
      this.height = 7;
    } 
    if ( (width >> 1 << 1) == width ){

      this.width = width+1;
    } 
    if ( (height >> 1 << 1) == height ){

      this.height = height+1;
    }

    this.width = (this.width == 0)? width : this.width;
    this.height = (this.height == 0)? height : this.height;

    this.buildBlankMaze();

    return new Maze(this.data, 1, 1, width-3, height-3, this.getName());
  } 



    public String getName(){
        return "Regular Scatter";
    }

  /** Creates a maze with scattered walls.
   */
  protected void buildBlankMaze(){

    this.data = new boolean[this.width][this.height];
    boolean flip = false;

    /**Walls go on even squares, rooms on odd.*/
    for(int x = 0; x < this.width; x++){

      for(int y = 0; y < this.height; y++){

        /* Wall */
        if ( x == 0 || y == 0 || x == this.width-1 || y == this.height-1){

          this.data[x][y] = true;
          /* Column */
        } else if( (x>>1<<1) == x && (y>>1<<1) == y){

            this.data[x][y] = true;
        }else {

          this.data[x][y] = false;
        }

        if(x != 0 && y != 0 && x != this.width-1 && y != this.height-1 && ((x-1)/2) != (x/2) && x > 1 && (x/2) >> 1 << 1 == (x/2)){
          this.data[x][y] = !(this.data[x][y]);
        }
      }
    }
  }

}


