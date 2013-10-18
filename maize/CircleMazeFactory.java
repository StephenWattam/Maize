package maize;

/** Creates an maze made of concentric cirlces */
public class CircleMazeFactory implements MazeFactory {

  /** Maze width */
  private int width = 0;

  /** Maze height */
  private int height = 0;

  /** Maze data */
  private boolean[][] data = null;

  /** Default Constructor */
  public CircleMazeFactory() { }

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

    this.createBlankCircles();
    this.createCircleExits();

    return new Maze(this.data, 1, 1, width/2, height/2);
  } 

  /** Creates a blank maze with concentric circles, no joins.
    */
  public void createBlankCircles(){

    this.data = new boolean[this.width][this.height];

    int noCircles = (int)Math.floor(Math.min(this.data.length, this.data[0].length)/2);

    /* Create the circles */
    for(int i=0;i<noCircles;i++){
      for(int w=0; w<=1; w++){  /* Wall or room layer */
        for(int x=0+(i*2)+w; x<this.data.length-((i*2)+w); x++){
          for(int y=0+(i*2)+w; y<this.data[0].length-((i*2)+w); y++){
            this.data[x][y] = (w == 0) ? true : false;
          }
        }
      }
    }
  }
    
  /** Creates exits between the circles.
    */
  public void createCircleExits(){

    int noCircles = (int)Math.ceil(Math.min(this.data.length, this.data[0].length)/4);

    /* Create the circles */
    for(int i=1;i<=noCircles;i++){

      /* Create a circumference and choose from that */
      int w = this.data.length - (i*4) - 1;
      int h = this.data[0].length- (i*4) - 1;
      int c = (w*2 + h*2);

      /* Choose position based upon a random number in the circumference. */
      int num = (int)(Math.random() * c);

      /* The most intriciate bit of evil ever. 
       * Translate from circumference number to x,y
       */

      int x = 0;
      if(num < w || (num < (w*2 +h)&& num >= (w+h)) ){
        if(num < w){
          x = num;
        } else {
          x = num - (w + h);
        }
      } else {
        if(num < (w+h)){
          x = w;
        } else {
          x = 0;
        }
      }


      int y = 0;
      if(num < w || (num < (w*2 +h)&& num >= (w+h)) ){
        if(num < w){
          y = 0;
        } else {
          y = h;
        }
      } else {
        if(num < (w+h)){
          y = num - w;
        } else {
          y = num - (2*w + h);
        }
      }

      /* If (x,y) is a corner fix it */
      if(x == 0 && y ==0) {x++;}
      if(x == 0 && y ==h) {x++;}
      if(x == w && y ==0) {x--;}
      if(x == w && y ==h) {x--;}
      
      this.data[x + ((i*2))][y + ((i*2))] = false;
    }
    this.data[this.width/2][this.height/2] = false;
  }
}

