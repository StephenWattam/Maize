CC=javac
CFLAGS=-cp ./lib/json-simple-1.1.1.jar
#-Xlint:unchecked

all:
	$(CC) $(CFLAGS) maize/ui/*.java maize/*.java *.java

docs:
	javadoc -d Docs -classpath ./lib/json-simple-1.1.1.jar maize/ui/*.java maize/*.java *.java

run:
	java -cp .:lib/json-simple-1.1.1.jar RunMazeUI

clean:
	rm -rfv Docs
	rm -rfv *.class maize/*.class maize/ui/*.class bots/*.class
