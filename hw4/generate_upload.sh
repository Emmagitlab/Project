#!/bin/bash
# this is build.bash to create the .jar file 

javac GenerateDB1.java
javac GenerateDB2.java
javac GenerateDB3.java
java GenerateDB1
java GenerateDB2
java GenerateDB3

dirname="p4_dataset/*"
input1="RDataset.csv"
input2="PDataset.csv"
input3="KSeed.csv"
input4="2DPoints.csv"
input5="Customers.json"
folder="p4_dataset"

hadoop fs -rmr $dirname
hadoop fs -mkdir $folder
hadoop fs -put $input1 $input2 $input3 $input4 $input5 $folder

hadoop fs -ls $folder