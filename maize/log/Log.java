package maize.log;

import java.text.*;
import java.util.*;

public class Log{
    
    // Singleton
	private static Log instance = null;

    // Keep track of all log listeners
    private static Vector<LogListener> listeners = new Vector<LogListener>(); 

    // Add a log listener, which will be notified of all log events
    public static void addLogListener(LogListener ll){
        if(listeners.size() == 0)
            log("Logging has been assumed by a dedicated log subscriber, and will no longer be placed on STDOUT.");
        listeners.add(ll);
    }

    // Remove a log listener
    public static void removeLogListener(LogListener ll){
        listeners.remove(ll);
        if(listeners.size() == 0)
            log("The final subscribers left me, defaulting to STDOUT.");
    }

    // Log a string without a component name
    public static void log(String str){
        str = formatMessage(str);

        // Send to stdout if we don't have listeners
        if(listeners.size() > 0)
            for(LogListener ll:listeners)
                ll.logEvent(str);
        else
            System.out.println(str);
    }

    // Log an exception
    public static void logException(Throwable e){
        StackTraceElement[] trace = e.getStackTrace();
        log("Exception -> " + e.toString());
        for(StackTraceElement ste : e.getStackTrace()){
            log("             " + ste.toString());
        }
    }


    // Add something to the log, from a given component
    public static void log(String who, String str){
        log("[" + who + "] " + str);
    }

    // Format the message to contain the date
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
