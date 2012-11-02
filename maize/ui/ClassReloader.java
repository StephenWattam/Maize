package maize.ui;

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
        if(!className.equals(name))
            return super.loadClass(name);

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


            // Define the class
            return defineClass(name, classData, 0, classData.length);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace(); 
        }

        return null;
    }

}
