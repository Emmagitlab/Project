#!/bin/bash
# this is build.bash to create the .jar file 

dirname="CustomerInputFormat"
class_path="/usr/share/hadoop/hadoop-core-1.2.1.jar"
filename="CustomerInputFormat.java"
jarname="CustomerInputFormat.jar"

rm -r $dirname
rm $jarname

mkdir $dirname
javac -classpath $class_path -d $dirname ./$filename
jar -cvf ./$jarname -C $dirname/ .

