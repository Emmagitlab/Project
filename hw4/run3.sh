#!/bin/bash
# this is run.sh to execute the *.jar file

packname="org.apache.hadoop.examples.KMeansClustering"
jarfile="KMeansClustering.jar"
input1="./p4_dataset/2DPoints.csv"
input2="./p4_dataset/KSeed.csv"
output="./KMeans_out0"
output1="./KMeans_out1"
output2="./KMeans_out2"

hadoop fs -rmr $output
hadoop fs -rmr $output1
hadoop fs -rmr $output2
hadoop jar ./$jarfile $packname $input1 $input2 $output
