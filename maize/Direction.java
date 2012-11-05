package maize;

/** Helper class for directions */
public abstract class Direction{

    /** FORWARD move */
    public static final int FORWARD = 0;

    /** BACKWARD move */
    public static final int BACK = 1;

    /** RIGHT move */
    public static final int RIGHT = 2;

    /** LEFT move */
    public static final int LEFT = 3;


    public static String getName(int d){
        switch(d){
            case 0: return "FORWARD";
            case 1: return "BACK"; 
            case 2: return "RIGHT";
            case 3: return "LEFT"; 
        }
        return null;
    }

}

