#!/bin/bash
# this is run.sh to execute the *.jar file

packname="org.apache.hadoop.examples.SpatialJoin"
jarfile="SpatialJoin.jar"
input1="./p4_dataset/DB_R.csv"
input1="./p4_dataset/DB_P.csv"
output="./project1"

hadoop fs -rmr $output
hadoop jar ./$jarfile $packname $input1 $input2 $output
