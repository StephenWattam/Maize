package maize.compile;

import java.io.*;
import java.net.*;

// Many thanks to http://tutorials.jenkov.com/java-reflection/dynamic-class-loading-reloading.html#dynamicreloading

public class ClassReloader extends ClassLoader{

    // Will only load this one class,
    // will pass all others through to the parent class loader.
    private String className = "";

    public ClassReloader(ClassLoader parent, String className) {
        super(parent);
        this.className = className;
    }

    public Class loadClass(String name) throws ClassNotFoundException {


        // Use the parent if it's not on our list of allowable classes
        // Also allow inner classes
        if(!className.equals(name) && !className.startsWith(name + "$")){
            return super.loadClass(name);
        }

        try {
            // Read file
            String url                      = "file:" + className.replace(".", "/") + ".class";
            URL myUrl                       = new URL(url);
            URLConnection connection        = myUrl.openConnection();
            InputStream input               = connection.getInputStream();
            ByteArrayOutputStream buffer    = new ByteArrayOutputStream();
            int data                        = input.read();

            // Continue reading file
            while(data != -1){
                buffer.write(data);
                data = input.read();
            }

            // Close file
            input.close();

            // Convert to a byte array
            byte[] classData = buffer.toByteArray();

            /* Log.log("Forcing reload of class " + name + " from " + url); */

            // Define the class
            return defineClass(name, classData, 0, classData.length);

        } catch (MalformedURLException e) {
            /* Log.log("Error forcing reload of bot class: " + name); */
            /* Log.logException(e); */
            e.printStackTrace();
        } catch (IOException e) {
            /* Log.log("Error forcing reload of bot class: " + name); */
            /* Log.logException(e); */
            e.printStackTrace(); 
        }

        return null;
    }

}
