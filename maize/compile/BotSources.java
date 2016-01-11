package maize.compile;

import maize.Bot;
import maize.log.Log;
import maize.log.LogOutputStream;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by John Vidler on 10/01/16.
 */
public class BotSources
{
    private final ArrayList<File> mSources;
    private final File            mRoot;
    private final String          mMainClass;
    private final File            mMainFile;
    private final boolean         mIsSingleSource;

    public static List<BotSources> scanDirectory( File root ) throws IOException {
        if( !root.isDirectory() || root.listFiles() == null )
            return new ArrayList<>();

        ArrayList<BotSources> detectedBots = new ArrayList<>();

        for( File bot : root.listFiles() ) {
            if( bot.isDirectory() || bot.getName().endsWith(".java") )
                detectedBots.add( new BotSources(bot) );
        }

        return detectedBots;
    }

    public File   getRoot()      { return mRoot;      }
    public File   getMainFile()  { return mMainFile;  }
    public String getMainClass() { return mMainClass; }

    public BotSources( File root ) throws IOException
    {
        mRoot = root;
        mSources = new ArrayList<>();

        if( mRoot.isDirectory() ) {
            mMainClass = "bots." +mRoot.getName()+ "." + mRoot.getName();
            mMainFile = new File( mRoot.getAbsolutePath().replaceAll( "\\.java$", ".class" ) );
            mIsSingleSource = false;
            walkDirectory( mRoot );
        } else {
            mMainClass = "bots." +mRoot.getName().replaceAll( "\\.java$", "" );
            mMainFile = new File( mRoot.getAbsolutePath().replaceAll( "\\.java$", ".class" ) );
            mSources.add( mRoot );
            mIsSingleSource = true;
        }
    }

    protected void walkDirectory( File localRoot ) throws IOException
    {
        if( localRoot.isDirectory() ) {
            File children[] = localRoot.listFiles();
            if( children != null ) {
                for (File child : children) {
                    walkDirectory( child );
                }
            }
        } else {
            if( localRoot.getName().toLowerCase().endsWith( ".java" ) )
                mSources.add( localRoot );
        }
    }

    public boolean compile() throws IOException {
        return compile( false );
    }

    public boolean compile( boolean verbose ) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles( mSources );

        ArrayList<String> compilerOptions = new ArrayList<>();

        if( verbose )
            compilerOptions.add( "-verbose" );

        compilerOptions.add( "-cp" );
        compilerOptions.add( "." );

        JavaCompiler.CompilationTask task = compiler.getTask(new PrintWriter(System.out), fileManager, diagnostics, compilerOptions, null, compilationUnits);
        boolean success = task.call();
        fileManager.close();

        if( !success ) {
            for( Diagnostic<? extends JavaFileObject> jfod : diagnostics.getDiagnostics() ) {
                Log.log ( "Compiler", "Error: " +jfod.getMessage(Locale.getDefault()) + " in file " +jfod.getSource().getName()+ " at line " +jfod.getLineNumber() );
                Log.log ( "Compiler", "\t> " + jfod.getSource().getCharContent(true) );
            }

        }

        return success;
    }

    public Bot instantiate() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        ClassLoader parentClassLoader   = ClassReloader.class.getClassLoader();
        final ClassReloader classLoader;

        if( mIsSingleSource ) {
            classLoader = new ClassReloader(parentClassLoader, mMainClass);

            Log.log( "Compiler", "Creating new " +mMainClass );
        } else {
            classLoader = new ClassReloader(parentClassLoader, mMainClass);

            Log.log( "Compiler", "Creating new " +mMainClass );
        }

        // Then load the desired bot with it
        Class botClass = classLoader.loadClass( mMainClass );
        return (Bot)botClass.newInstance();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append( "BotSources(" ).append( mMainClass ).append(", ").append( mRoot.getPath() ).append( ") -> " );

        for( File source : mSources )
            buffer.append( source.getName() ).append( " " );

        return buffer.toString();
    }
}
