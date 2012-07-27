package maize;

import java.io.*;

/** Class for loading and saving Maze objects into files */
public abstract class ClassSerializer{

  /** Loads Maze instance from file 
    *
    * @param  mazeFile                  File which contains serialized maze
    *
    * @return                           Maze instance, null if failed.
    *
    * @throws IOException               Thrown on an IO fuckup
    * @throws ClassNotFoundException    Thrown on an casting fuckup
    */
  public static Object load(File mazeFile) throws IOException, ClassNotFoundException {

    /* IO stuff */
    ObjectInputStream oi = new ObjectInputStream( new FileInputStream( mazeFile ) );

    Object instance = oi.readObject();

    oi.close();

    return instance;
  }

  /** Saves Maze instance to a file 
    *
    * @param  mazeFile                  File to save to.
    * @param  maze                      Maze instance
    *
    * @throws IOException               Thrown on an IO fuckup
    */
  public static void save(File mazeFile, Object maze) throws IOException {

    /* IO stuff */
    ObjectOutputStream oo = new ObjectOutputStream( new FileOutputStream( mazeFile ) );

    oo.writeObject(maze);

    oo.close();
  }
    
}
