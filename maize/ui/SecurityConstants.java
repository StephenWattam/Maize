package maize.ui;

import java.lang.RuntimePermission;
import java.security.Permission;
import java.security.AllPermission;

/**
 * Security constants, to play the role of the Sun/Oracle standard constants
 * @author John Vidler
 * @version 1.0
 */
public final class SecurityConstants
{
    private SecurityConstants() { /* Supress creation */ }

    public static final String FILE_DELETE_ACTION = "delete";
    public static final String FILE_EXECUTE_ACTION = "execute";
    public static final String FILE_READ_ACTION = "read";
    public static final String FILE_WRITE_ACTION = "write";
    public static final String FILE_READLINK_ACTION = "readlink";

    public static final String SOCKET_RESOLVE_ACTION = "resolve";
    public static final String SOCKET_CONNECT_ACTION = "connect";
    public static final String SOCKET_LISTEN_ACTION = "listen";
    public static final String SOCKET_ACCEPT_ACTION = "accept";
    public static final String SOCKET_CONNECT_ACCEPT_ACTION = "connect,accept";

    public static final String PROPERTY_RW_ACTION = "read,write";
    public static final String PROPERTY_READ_ACTION = "read";
    public static final String PROPERTY_WRITE_ACTION = "write";

    // Cover all, allow-any any permission
    public static final AllPermission ALL_PERMISSION = new AllPermission();

    public static final Permission CREATE_CLASSLOADER_PERMISSION = new RuntimePermission( "createClassLoader" );
    public static final Permission MODIFY_THREAD_PERMISSION      = new RuntimePermission( "modifyThread" );
    public static final Permission MODIFY_THREADGROUP_PERMISSION = new RuntimePermission( "modifyThreadGroup" );
    public static final Permission LOCAL_LISTEN_PERMISSION       = new RuntimePermission( "localListen" );
    //public static final Permission _PERMISSION = new RuntimePermission("");
}
