package maize;

/** This bot is abstract and should be extended to create your own bots.  
 * Using the calls of Advanced bot, Stack bot allows you to maintain a simple state.
 * @see AdvancedBot  
 */
public abstract class StateBot extends AdvancedBot{

  /** Variable which can be used to keep simple state */
  protected int state = 0;

  /** Default Constructor */
  public StateBot(){
    super();
  }

  /** Sets the state of the bot.
    * Can be any arbitrary number mapped to any constant that is user defined.
    *
    * @param  state       New state
    */
  protected void setState(int state){
    this.state = state;
  }

  /** Gets the current state of the bot.
    *
    * @return             Current state.
    */
  protected int getState(){
    return this.state;
  }

  /** This function should contain control logic for the bot.
    * Abstract function to be overloaded by bots.
    *
    * @param    view    View matric from the perspective of the bot, orientated so
    *                   the top of the matrix is facing the same direction as the 
    *                   bot.
    * @param    x       X coord of the bot.
    * @param    y       Y coord of the bot.
    * @param    o       Orientation of the bot @see Orientation
    * @param    fx      X coord of the finish.
    * @param    fy      Y coord of the finish.
   */
  public abstract void makeMove(boolean[][] view, int x, int y, int o, int fx, int fy);

  /** A nice fluffy name for the bot, like 'Bob' or 'John'. 
    *
    * @return           Bot name.
    */
  public abstract String getName();

  /** Implementation of the Bot interface.
    *
    * @return           Bot Description.
    */
  public abstract String getDescription();
}
