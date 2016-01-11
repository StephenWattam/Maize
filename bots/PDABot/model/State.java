package bots.PDABot.model;

import java.util.ArrayList;
import java.util.List;

public class State {
    ArrayList<Transition> mArcs = new ArrayList<>();
    int mID = -1;
    String mName = null;

    public String getName() { return mName; }
    public int getID() { return mID; }
    public List<Transition> getTransitions() { return mArcs; }
}