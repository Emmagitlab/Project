#!/bin/bash
# this is build.bash to create the .jar file 

dirname="SpatialJoin"
class_path="/usr/share/hadoop/hadoop-core-1.2.1.jar"
filename="SpatialJoin.java"
jarname="SpatialJoin.jar"

rm -r $dirname
rm $jarname

mkdir $dirname
javac -classpath $class_path -d $dirname ./$filename
jar -cvf ./$jarname -C $dirname/ .

