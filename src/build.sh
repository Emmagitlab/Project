#!/bin/bash
# this is build.bash to create the .jar file 

dirname="query3"
class_path="/usr/share/hadoop/hadoop-core-1.2.1.jar"
filename="Query3.java"
jarname="query3.jar"

hadoop fs -rmr query3_out
rm -r $dirname
rm $jarname

mkdir $dirname
javac -classpath $class_path -d $dirname ./$filename
jar -cvf ./$jarname -C $dirname/ 
