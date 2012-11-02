package maize;

/** Helper class for directions */
public abstract class Orientation{

    /** NORTH direction */
    public static final int NORTH = 0;

    /** EAST direction */
    public static final int EAST = 1;

    /** SOUTH direction */
    public static final int SOUTH = 2;

    /** WEST direction */
    public static final int WEST = 3;

    public static String getName(int o){
        switch(o){
            case 0: return "NORTH";
            case 1: return "EAST"; 
            case 2: return "SOUTH";
            case 3: return "WEST"; 
        }
        return null;
    }

    // Rotate to fit current context
    public static boolean[][] rotateToNorth(boolean[][] view, int o){
        switch(o){
            case Orientation.EAST: return rotateCW(rotateCW(rotateCW(view)));
            case Orientation.SOUTH: return rotateCW(rotateCW(view));
            case Orientation.WEST: return rotateCW(view);
        }
        return view;
    }

    // Thank to http://stackoverflow.com/questions/2799755/rotate-array-clockwise
    private static boolean[][] rotateCW(boolean[][] mat) {
        final int M = mat.length;
        final int N = mat[0].length;
        boolean[][] ret = new boolean[N][M];
        for (int r = 0; r < M; r++) {
            for (int c = 0; c < N; c++) {
                ret[c][M-1-r] = mat[r][c];
            }
        }
        return ret;
    }
}
