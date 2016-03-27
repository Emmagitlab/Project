#!/bin/bash
# this is run.sh to execute the *.jar file

packname="org.apache.hadoop.examples.CustomerInputFormat"
jarfile="CustomerInputFormat.jar"
input1="./p4_dataset/Customers.json"
output="./custFormat_out"

hadoop fs -rmr $output
hadoop jar ./$jarfile $packname $input1 $output
