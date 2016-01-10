package bots.FSMBot;

import java.util.ArrayList;

public class Transition
{
    State mFrom;
    State mTo;
    ArrayList<Character> mRead = new ArrayList<>();
    ArrayList<Character> mPush = new ArrayList<>();
    ArrayList<Character> mPop = new ArrayList<>();

    @Override
    public String toString()
    {
        return "{" +mFrom.mName+ "}\t"
                +(mRead.size() == 0?"\u03BB":Utility.listJoin(mRead, ""))+ "\t/\t"
                +(mPop.size() == 0?"\u03BB":Utility.listJoin(mPop, ""))+ "\t/\t"
                +(mPush.size() == 0?"\u03BB":Utility.listJoin(mPush, ""))+ "\t{" +mTo.mName+ "}";
    }
}