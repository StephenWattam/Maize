import bots.FSMBot.State;
import bots.FSMBot.Transition;

import java.util.ArrayList;
import java.util.TreeMap;

public class Graph
{
    public State mStart;
    public ArrayList<State> mFinish      = new ArrayList<>();
    public TreeMap<Integer,State> mStates      = new TreeMap<>();
    public ArrayList<Transition>   mTransitions = new ArrayList<>();
}