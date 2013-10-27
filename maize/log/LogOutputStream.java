package maize.log;
import java.io.*;

/** OutputStream that can be used to send messages through the log.
 */
public  class LogOutputStream extends OutputStream{
    // Writer interface
    // 'The only methods that a subclass must implement are write(char[], int, int), flush(), and close().' from the API
    private String writeBuffer  = "";
    private String prefix       = "";

    /** Create a LogOutputStream with a given prefix.
     *
     * @param prefix The string to add to all messages.
     */
    public LogOutputStream(String prefix){
        this.prefix = prefix;
    }

    /** Create a LogOutputStream without a prefix.*/
    public LogOutputStream(){
    }

    @Override
    public void write(byte[] cbuf, int off, int len) throws IOException{
        byte[] dest = new byte[len];
        System.arraycopy(cbuf, off, dest, 0, len);

        writeBuffer += new String( dest, "UTF-8");
        
        // Loop and output on \n
        /* System.out.println("Splitting: '" + writeBuffer + "'"); */
        String[] lines = writeBuffer.split("\\n");
        for(String l:lines){
            if(l.trim().length() > 0){
                /* System.out.println("Outputting: '" + l + "'"); */
                Log.log(prefix + l);
            }
        }

        writeBuffer = "";
    }

    @Override
    public void write(int b){
        char c = (char)b;
        appendChar(c);
    }

    @Override
    public void flush(){
        if(writeBuffer.length() > 0){
            Log.log(prefix + writeBuffer);
            writeBuffer = "";
        }
    }

    @Override
    public void close(){
        // Do nothing!
    }

    /** Append a character to the buffer. */
    private void appendChar(char c){
        if(c == '\n'){
            Log.log(prefix + writeBuffer);
            writeBuffer = "";
        }else{
            writeBuffer += c;
        }
    }
}
