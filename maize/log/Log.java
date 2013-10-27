package maize.log;

import java.text.*;
import java.util.*;

/** Singleton logging class that supports multiple listeners through the LogListener interface.
 * Logs to stdout by default, and prepends a simple date format and component label.
 *
 * @author Stephen Wattam
 */
public class Log{
    
    /** Singleton instance. */
	private static Log instance = null;

    /** Keep track of all log listeners. */
    private static Vector<LogListener> listeners = new Vector<LogListener>(); 

    /** Add a log listener, which will be notified of all log events. 
     *
     * @param ll The LogListener object that will be notified hence.
     */
    public static void addLogListener(LogListener ll){
        if(listeners.size() == 0)
            log("log", "Logging has been assumed by a dedicated log subscriber, and will no longer be placed on STDOUT.");
        listeners.add(ll);
    }

    /** Remove a log listener.  Said listener will no longer be notified of events.
     *
     * @param ll The LogListener to stop notifying of new events.
     */
    public static void removeLogListener(LogListener ll){
        listeners.remove(ll);
        if(listeners.size() == 0)
            log("log", "The final subscribers left me, defaulting to STDOUT.");
    }

    /** Log a string without a component name.
     *
     * @param str The string to print to the listeners (or stdout)
     */
    public static void log(String str){
        str = formatMessage(str);

        // Send to stdout if we don't have listeners
        if(listeners.size() > 0)
            for(LogListener ll:listeners)
                ll.logEvent(str);
        else
            System.out.println(str);
    }

    /** Log an exception, outputting its stack trace.
     *
     * @param e The exception to throw.
     */
    public static void logException(Throwable e){
        StackTraceElement[] trace = e.getStackTrace();
        log("Exception -> " + e.toString());
        for(StackTraceElement ste : e.getStackTrace()){
            log("             " + ste.toString());
        }
    }


    /** Add something to the log, from a given component.  Prints the component
     * name prior to the message for easy filtering.
     *
     * @param who The component name, prepended to the message
     * @param str The line to log.
     */
    public static void log(String who, String str){
        log("[" + who + "] " + str);
    }

    /** Format the message to contain the date.
     *
     * @param str The message to be formatted.
     */
    private static String formatMessage(String str){
        String datetime = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss]").format(new Date());
        return (datetime + " " + str);
    }


    /** Ensures this remains as a singleton */
	public static Log getInstance(){
		if(instance == null)
			instance = new Log();
		return instance;
	}

   
}
