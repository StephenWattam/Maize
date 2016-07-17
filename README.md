# Maize README
Maize is a tool for running virtual micro-mice around virtual mazes.  It was
designed to illustrate different approaches to AI, and to let people have a go
at writing some simple agent-based bots.

For more information see:

 * The project page: [https://stephenwattam.com/projects/maize/](https://stephenwattam.com/projects/maize/)
 * The page with teaching materials: [https://stephenwattam.com/teaching/maize/](https://stephenwattam.com/teaching/maize/)


## Running Maize
There are two ways of running Maize, a normal UI for running bots interactively
and a demo mode for showing off a sample of bots randomly.

## Documentation
To compile documentation, which will be in ./Docs, run

    $ make docs

If you don't have GNU Make, run the full command:

    $ javadoc -d Docs maize/ui/*.java maize/*.java *.java

## Compilation
To compile Maize on a system with GNU Make simply run 

    $ make

If you don't have Make, you'll have to run the full command:

	javac maize/ui/*.java maize/*.java *.java

Don't worry about compiling the bots, the maze program does that.

## Running
To launch the normal interactive UI simply run:
 
    $ java RunMazeUI

and to launch the demo, run:

    $ java RunMazeDemo

The program will compile everything in the ./bots/ folder, then launch.



## Writing bots
Bots must subclass the Bot class, and contain a method that is called at each
time tick by the simulation.  This method has a number of useful inputs passed 
to it, and is expected to output a command to control the bot in the maze.

Aside from subclassing Bot, bots should implement Serializable so that they can
be saved and loaded, and should be in package 'bot' so they can be compiled on-
the-fly.  Have a look at existing bots, as well as the documentation, for more
detailed documentation, or check the source of maize/Bot.java.
