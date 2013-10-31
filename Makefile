
# 
CC=javac
# Mac users should, use this instead (see http://docs.oracle.com/javase/7/docs/webnotes/install/mac/mac-jdk.html )
#CC=/usr/libexec/java_home -v 1.7.0_09 --exec javac

# Use 1.6 language features only.
CFLAGS=-cp ./lib/json-simple-1.1.1.jar:./lib/javacsv.jar
	#-source 1.6 
#-Xlint:unchecked

JAVA=java
# Mac users should use this instead (see http://docs.oracle.com/javase/7/docs/webnotes/install/mac/mac-jdk.html )
#JAVA=/usr/libexec/java_home -v 1.7.0_09 --exec java 
RUNFLAGS=-cp .:lib/json-simple-1.1.1.jar:./lib/javacsv.jar

JAVADOC=javadoc
# Mac users should use this instead (see http://docs.oracle.com/javase/7/docs/webnotes/install/mac/mac-jdk.html )
#JAVADOC=/usr/libexec/java_home -v 1.7.0_09 --exec javadoc 
DOCFLAGS=-d Docs -classpath ./lib/json-simple-1.1.1.jar:./lib/javacsv.jar

all:
	$(CC) $(CFLAGS) maize/*/*.java maize/*.java *.java
	jar cfm Maize.jar Maize.mf  maize/*/*.class maize/*.class *.class

docs:
	$(JAVADOC) $(DOCFLAGS) maize/*/*.java maize/*.java *.java

run:
	$(JAVA) $(RUNFLAGS)  RunMazeUI

clean:
	rm -rfv Docs
	rm -rfv *.class maize/*.class maize/ui/*.class bots/*.class
	rm -fv Maize.jar
