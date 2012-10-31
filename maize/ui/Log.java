package maize.ui;

import java.text.*;
import java.util.*;

public class Log{
    
	private static Log instance = null;

    private static Vector<LogListener> listeners = new Vector<LogListener>(); 

    public static void addLogListener(LogListener ll){
        if(listeners.size() == 0)
            log("Logging has been assumed by a dedicated log subscriber, and will no longer be placed on STDOUT.");
        listeners.add(ll);
    }

    public static void removeLogListener(LogListener ll){
        listeners.remove(ll);
        if(listeners.size() == 0)
            log("The final subscribers left me, defaulting to STDOUT.");
    }

    public static void log(String str){
        str = formatMessage(str);

        if(listeners.size() > 0)
            for(LogListener ll:listeners)
                ll.logEvent(str);
        else
            System.out.println(str);
    }

    public static void logException(Exception e){
        StackTraceElement[] trace = e.getStackTrace();
        log("Exception -> " + e.toString());
        for(StackTraceElement ste : e.getStackTrace()){
            log("             " + ste.toString());
        }
    }


    public static void log(String who, String str){
        log("[" + who + "]" + str);
    }

    private static String formatMessage(String str){
        String datetime = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss]").format(new Date());
        return (datetime + " " + str);
    }


    // Ensures this remains as a singleton
	public static Log getInstance(){
		if(instance == null)
			instance = new Log();
		return instance;
	}
}
