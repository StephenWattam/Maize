package maize.log;

/** LogListener objects can be added to a Log object to be notified of log events.
 *
 * As the log does not differentiate between level or type of message, this simply passes strings.
 *
 */
public interface LogListener{

    /** Log a string event. */
    public void logEvent(String line);
}
