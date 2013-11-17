javac -cp ./lib/json-simple-1.1.1.jar;./lib/javacsv.jar maize/compile/*.java maize/log/*.java maize/trial/*.java maize/ui/*.java maize/*.java *.java

jar cfm Maize.jar Maize.mf maize/compile/*.java maize/log/*.java maize/trial/*.java maize/ui/*.java maize/*.java *.java
