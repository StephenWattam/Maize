CC=javac
CFLAGS=
#-Xlint:unchecked

all:
	$(CC) $(CFLAGS) maize/ui/*.java maize/*.java *.java

docs:
	javadoc -d Docs maize/ui/*.java maize/*.java *.java

clean:
	rm -rfv Docs
	rm -rfv *.class maize/*.class maize/ui/*.class bots/*.class
