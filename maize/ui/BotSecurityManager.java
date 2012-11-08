package maize.ui;

import java.security.*;
import java.io.FileDescriptor;
import java.io.File;
import java.io.FilePermission;
import java.awt.AWTPermission;
import java.util.PropertyPermission;
import java.util.ArrayList;
import java.lang.RuntimePermission;
import java.net.SocketPermission;
import java.net.NetPermission;
import java.util.Hashtable;
import java.net.InetAddress;
import java.lang.reflect.Member;
import java.lang.reflect.*;
import java.net.URL;

import maize.ui.BotSecurityConstants;

/**
 * A SecurityManager extension to only allow bots to do nice things in the simulation.
 * 
 * Very messy, and could be minimised, but works for now.
 * 
 * @author John Vidler
 */
public class BotSecurityManager extends SecurityManager {

    protected String[] blacklist  = { "sun.reflect.", "setSecurityManager", "setAccessible" };
    protected String[] dangerlist = { "bots." };
    protected long     checkID    = 0;

    public BotSecurityManager() {
        super();
        debugWrite( "Logging at level: " +MazeUISettingsManager.smLogLevel );

        // Check for overridden blacklist
        if( MazeUISettingsManager.smBlackList != null )
        {
            debugWrite( 2, "Default blacklist replaced by: " + MazeUISettingsManager.smBlackList.toString() );
            ArrayList<String> tmp = new ArrayList<String>();
            for( Object o : MazeUISettingsManager.smBlackList )
                tmp.add( o.toString() );
            blacklist = tmp.toArray( new String[1] );
        }
        
        // Check for overridden dangerlist
        if( MazeUISettingsManager.smDangerList != null )
        {
            debugWrite( 2, "Default dangerlist replaced by: " + MazeUISettingsManager.smDangerList.toString() );
            ArrayList<String> tmp = new ArrayList<String>();
            for( Object o : MazeUISettingsManager.smDangerList )
                tmp.add( o.toString() );
            dangerlist = tmp.toArray( new String[1] );
        }

    }

    public void checkPermission( Permission perm ) throws SecurityException {

        // Only check if we're actually enabled!
        if( !MazeUISettingsManager.smEnabled )
            return;

        checkID++;

        Object o        = getSecurityContext();
        Class[] context = getClassContext();

        for( String black : blacklist )
        {
            if( black.equals( perm.getName() ) )
            {
                debugWrite( 1, "[DENY]  #" +checkID+ "   Violation: blacklist on '" +black+ "'" );
                if( MazeUISettingsManager.smLogLevel >= 3 )
                    dumpCallStack( context, "" );
                throw new SecurityException( "The call '" +black+ "' is a blacklisted method" );
            }
        }

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
                    if( perm.getName().equals(BotSecurityConstants.MODIFY_THREADGROUP_PERMISSION.getName()) ||
                            perm.getName().equals(BotSecurityConstants.MODIFY_THREAD_PERMISSION.getName()) )
                    {
                        debugWrite( 1, "[DENY]  #" +checkID+ "   Danger: " +context[dangerIndex]+ " [" +dangerIndex+ "]" );
                        if( MazeUISettingsManager.smLogLevel >= 3 )
                            dumpCallStack( context, "" );
                        throw new SecurityException( "Threading is disabled!" );
                    }
                }

                for( String black : blacklist )
                {
                    int violationIdx = searchCallStack( context, black );

                    if( violationIdx > 0 && violationIdx < dangerIndex ) {
                        debugWrite( 1, "[DENY]  #" +checkID+ "   Violation: " +context[violationIdx].getName()+ " [" +violationIdx+ "], Danger: " +context[dangerIndex]+ " [" +dangerIndex+ "]" );
                        if( MazeUISettingsManager.smLogLevel >= 3 )
                            dumpCallStack( context, black );
                        throw new SecurityException( "RuntimePermissions are disabled for '" +black+ "'!" );
                    } else if( violationIdx > 0 ) {
                        debugWrite( 2, "[ALLOW] #" +checkID+ "   Violation: " +context[violationIdx].getName()+ " [" +violationIdx+ "], Danger: " +context[dangerIndex]+ " [" +dangerIndex+ "]" );
                        if( MazeUISettingsManager.smLogLevel >= 3 )
                            dumpCallStack( context, black );
                    }

                }
            }
        }
    }

    protected void debugWrite( String s )
    {
        if( MazeUISettingsManager.smLogLevel > 0 )
            Log.log( s );
    }

    protected void debugWrite( int level, String s )
    {
        if( MazeUISettingsManager.smLogLevel >= level )
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
        checkPermission(BotSecurityConstants.CREATE_CLASSLOADER_PERMISSION);
    }

    public void checkExit(int status) {
        checkPermission(new RuntimePermission("exitVM."+status));
    }

    public void checkExec(String cmd) {
        File f = new File(cmd);
        if (f.isAbsolute()) {
            checkPermission( new FilePermission(cmd, BotSecurityConstants.FILE_EXECUTE_ACTION) );
        } else {
            checkPermission( new FilePermission("<<ALL FILES>>", BotSecurityConstants.FILE_EXECUTE_ACTION) );
        }
    }

    public void checkPropertiesAccess() {
        checkPermission(new PropertyPermission( "*", BotSecurityConstants.PROPERTY_RW_ACTION) );
    }

    public void checkPropertyAccess(String key) {
        checkPermission(new PropertyPermission( key, BotSecurityConstants.PROPERTY_READ_ACTION) );
    }

    public boolean checkTopLevelWindow(Object window) {
        return super.checkTopLevelWindow( window );
    }

    public void checkPrintJobAccess() {
        checkPermission(new RuntimePermission("queuePrintJob"));
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
