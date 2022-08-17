#!/bin/bash

echo "Kompilowanie programu triangle-app"

##Enter the folder triangle-lib:

cd triangle-lib || exit

rm -d -r out

mkdir -p out/class

##Create .class files by compiling the project with the command:

javac -cp ./src/main/java ./src/main/java/pl/com/rbinternational/*.java -d ./out/class


##Create .jar file

jar cvf out/triangle-lib.jar -C ./out/class .

cd ..

cd triangle-app || exit

rm -d -r out

mkdir -p out/class
mkdir -p out/bin
mkdir -p out/libs

cp -r ../triangle-lib/out/*.jar out/libs

##Compile the files to ".class" format, which are located in the folder "/src/main/java/pl/com/rbinternational/*.java"

javac -cp ./src/main/java ./src/main/java/pl/com/rbinternational/*.java ./src/main/java/pl/com/rbinternational/*/*.java -d ./out/class -classpath ./out/libs/*.jar


##The contents of the file, if we want to compile the FAT Jar, we need the contents of the MANIFEST file, it must look as follows:

##Manifest-Version: 1.0
##Class-Path: .
##Main-Class: pl.com.rbinternational.Main

##For Jar in Jar, the MANIFEST.MF file should look like this:

##Manifest-Version: 1.0
##Class-Path: libs/triangle-lib.jar
##Main-Class: pl.com.rbinternational.Main
	
	cd ../triangle-lib || exit
	
	cp -r out/class/* ../triangle-app/out/class
	
	cd ../triangle-app || exit

##Then we compile everything to a file in ".jar" format

jar cvfm out/bin/triangle-app.jar ./src/main/resources/META-INF/MANIFEST.MF -C out/class . 