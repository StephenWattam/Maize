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

    /** Return a string representing the direction.
     *
     * @param d The direction, as a value from one of the direction enumerations in this class.
     * @return A string corresponding to a direction, named after the enumeration value variables, or null
     * if the direction is not in the class.
     */
    public static String getName(int d){
        switch(d){
            case 0: return "FORWARD";
            case 1: return "BACK"; 
            case 2: return "RIGHT";
            case 3: return "LEFT"; 
        }
        return "STOPPED";
    }

}

