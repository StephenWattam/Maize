package maize.ui;

import java.security.*;
import java.io.FileDescriptor;
import java.io.File;
import java.io.FilePermission;
import java.awt.AWTPermission;
import java.util.PropertyPermission;
import java.lang.RuntimePermission;
import java.net.SocketPermission;
import java.net.NetPermission;
import java.util.Hashtable;
import java.net.InetAddress;
import java.lang.reflect.Member;
import java.lang.reflect.*;
import java.net.URL;

import maize.ui.SecurityConstants;

/**
 * A SecurityManager extension to only allow bots to do nice things in the simulation.
 * 
 * Very messy, and could be minimised, but works for now.
 * 
 * @author John Vidler
 */
public class BotSecurityManager extends SecurityManager {

    protected final String[] blacklist  = { "sun.reflect.", "setSecurityManager", "setAccessible" };
    protected final String[] dangerlist = { "bots." };

    public void checkPermission( Permission perm ) throws SecurityException {

        // Only check if we're actually enabled!
        if( !MazeUISettingsManager.smEnabled )
            return;

        for( String black : blacklist )
        {
            if( black.equals( perm.getName() ) )
                throw new SecurityException( "The call '" +black+ "' is a blacklisted method" );
        }

        Object o        = getSecurityContext();
        Class[] context = getClassContext();

        // Uncommenting this causes much spam, you are fore-warned!
        //Log.log( perm.getClass().getName() + " --> " + perm.getName() );

        // Runtime/Reflection permission check (?)
        if( perm instanceof RuntimePermission || perm instanceof ReflectPermission ) {
            int dangerIndex = -1;
            for( String cls : dangerlist )
            {
                int idx = searchCallStack( context, cls );
                dangerIndex = Math.max( dangerIndex, idx );
            }

            // Scan the blacklist!
            if( dangerIndex != -1 )
            {
                // Disallow threads, if the configuration mandates it.
                if( !MazeUISettingsManager.smAllowThreading )
                {
                    if( perm.getName().equals(SecurityConstants.MODIFY_THREADGROUP_PERMISSION.getName()) ||
                        perm.getName().equals(SecurityConstants.MODIFY_THREAD_PERMISSION.getName()) )
                        throw new SecurityException( "Threading is disabled!" );
                }

                for( String black : blacklist )
                {
                    int violationIdx = searchCallStack( context, black );
                    /* if( violationIdx > 0 ) */
                    debugWrite( "Violation: " +context[violationIdx].getName()+ " [" +violationIdx+ "], Danger: " +context[dangerIndex]+ " [" +dangerIndex+ "]" );

                    if( violationIdx > 0 && violationIdx < dangerIndex ) {
                        debugWrite( "RuntimePermission denied on '" +black+ "'!" );
                        if( MazeUISettingsManager.smDebug )
                            dumpCallStack( context, black );
                        throw new SecurityException( "RuntimePermissions are disabled for '" +black+ "'!" );
                    }
                }
            }
        }
    }

    protected void debugWrite( String s )
    {
        if( MazeUISettingsManager.smDebug )
            Log.log( s );
    }

    public void dumpCallStack( Class[] stack, String classSpec ) {
        boolean found = false;
        debugWrite( "StackDump:" );
        
        for( int d = stack.length-1; d>0; d-- )
        {
            Class c = stack[d];
            if( classSpec.endsWith(".") ) {
                if( c.getName().startsWith( classSpec ) )
                    found = true;
            }
            else
                if( c.getName().equals( classSpec ) )
                    found = true;
            debugWrite( "\t#" +d+ " - " +(found?"* ":"- ") +c.getName() );
        }
    }

    public int searchCallStack( Class[] stack, String classSpec ) {
        for( int d = stack.length-1; d>0; d-- )
        {
            Class c = stack[d];
            if( c != BotSecurityManager.class )
            {
                if( classSpec.endsWith(".") )
                {
                    if( c.getName().startsWith( classSpec ) )
                        return d;
                }
                else
                    if( c.getName().equals( classSpec ) )
                        return d;
            }
        }
        return -1;
    }

    public void checkPermission( Permission perm, Object context ) {
        checkPermission( perm );
    }

    public void checkCreateClassLoader() {
        checkPermission(SecurityConstants.CREATE_CLASSLOADER_PERMISSION);
    }

    public void checkExit(int status) {
        checkPermission(new RuntimePermission("exitVM."+status));
    }

    public void checkExec(String cmd) {
        File f = new File(cmd);
        if (f.isAbsolute()) {
            checkPermission( new FilePermission(cmd, SecurityConstants.FILE_EXECUTE_ACTION) );
        } else {
            checkPermission( new FilePermission("<<ALL FILES>>", SecurityConstants.FILE_EXECUTE_ACTION) );
        }
    }

    public void checkPropertiesAccess() {
        checkPermission(new PropertyPermission( "*", SecurityConstants.PROPERTY_RW_ACTION) );
    }

    public void checkPropertyAccess(String key) {
        checkPermission(new PropertyPermission( key, SecurityConstants.PROPERTY_READ_ACTION) );
    }

    public boolean checkTopLevelWindow(Object window) {
        return super.checkTopLevelWindow( window );
    }

    public void checkPrintJobAccess() {
        checkPermission(new RuntimePermission("queuePrintJob"));
    }

    public void checkSystemClipboardAccess() {
        //checkPermission(SecurityConstants.ACCESS_CLIPBOARD_PERMISSION);
    }

    public void checkAwtEventQueueAccess() {
        //checkPermission(SecurityConstants.CHECK_AWT_EVENTQUEUE_PERMISSION);
    }

    private static boolean packageAccessValid = false;

    private static String[] packageAccess;

    private static final Object packageAccessLock = new Object();

    private static boolean packageDefinitionValid = false;

    private static String[] packageDefinition;

    private static final Object packageDefinitionLock = new Object();

    private static String[] getPackages(String p) {
        String packages[] = null;
        if (p != null && !p.equals("")) {
            java.util.StringTokenizer tok = new java.util.StringTokenizer(p, ",");
            int n = tok.countTokens();
            if (n > 0) {
                packages = new String[n];
                int i = 0;
                while (tok.hasMoreElements()) {
                    String s = tok.nextToken().trim();
                    packages[i++] = s;
                }
            }
        }
        if (packages == null)
            packages = new String[0];
        return packages;
    }

    public void checkPackageAccess(String pkg) {
        if (pkg == null) {
            throw new NullPointerException("package name can't be null");
        }
        String[] pkgs;
        synchronized (packageAccessLock) {
            if (!packageAccessValid) {
                String tmpPropertyStr =
                    AccessController.doPrivileged(
                            new PrivilegedAction<String>() {

                            public String run() {
                            return java.security.Security.getProperty(
                                "package.access");
                            }
                            }
                            );
                packageAccess = getPackages(tmpPropertyStr);
                packageAccessValid = true;
            }
            // Using a snapshot of packageAccess -- don't care if static field
            // changes afterwards; array contents won't change.
            pkgs = packageAccess;
        }
        for (int i = 0; i < pkgs.length; i++) {
            if (pkg.startsWith(pkgs[i]) || pkgs[i].equals(pkg + ".")) {
                checkPermission(
                        new RuntimePermission("accessClassInPackage."+pkg));
                break;  // No need to continue; only need to check this once
            }
        }
    }

    public void checkPackageDefinition(String pkg) {
        if (pkg == null) {
            throw new NullPointerException("package name can't be null");
        }
        String[] pkgs;
        synchronized (packageDefinitionLock) {
            if (!packageDefinitionValid) {
                String tmpPropertyStr =
                    AccessController.doPrivileged(
                            new PrivilegedAction<String>() {

                            public String run() {
                            return java.security.Security.getProperty(
                                "package.definition");
                            }
                            }
                            );
                packageDefinition = getPackages(tmpPropertyStr);
                packageDefinitionValid = true;
            }
            // Using a snapshot of packageDefinition -- don't care if static
            // field changes afterwards; array contents won't change.
            pkgs = packageDefinition;
        }
        for (int i = 0; i < pkgs.length; i++) {
            if (pkg.startsWith(pkgs[i]) || pkgs[i].equals(pkg + ".")) {
                checkPermission(
                        new RuntimePermission("defineClassInPackage."+pkg));
                break; // No need to continue; only need to check this once
            }
        }
    }

    public void checkSetFactory() {
        checkPermission(new RuntimePermission("setFactory"));
    }

    public void checkMemberAccess(Class<?> clazz, int which) {
        super.checkMemberAccess( clazz, which );
    }

    public void checkSecurityAccess(String target) {
        checkPermission(new SecurityPermission(target));
    }

    private native Class currentLoadedClass0();

    public ThreadGroup getThreadGroup() {
        return Thread.currentThread().getThreadGroup();
    }
}
