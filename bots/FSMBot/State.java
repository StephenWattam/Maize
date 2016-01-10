package bots.FSMBot;

import java.util.ArrayList;

public class State {
    ArrayList<Transition> mArcs = new ArrayList<>();
    int mID = -1;
    String mName = null;
}