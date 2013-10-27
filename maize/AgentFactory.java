package maize;

/** Agent factory for building Agents*/
public class AgentFactory {

    /** Default constructor */
    public AgentFactory() {}

    /** Returns a ready built agent from the People factorys!
     *
     * @param maze         Maze agent shall be traversing.
     * @param bot          Bot to control agent, consider it the brain.
     *
     * @return             New Agent
     */
    public Agent getAgent(Maze maze, Bot bot){

        return new Agent(maze, bot, maze.getEntX(), maze.getEntY());
    }
}
