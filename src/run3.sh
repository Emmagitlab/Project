#!/bin/bash
# this is run.sh to execute the *.jar file

packname="org.apache.hadoop.examples.Query3"
jarfile="query3.jar"
input1="./p1_dataset/Customers.csv"
input2="./p1_dataset/Transactions.csv"
output="./query3_out"

hadoop jar ./$jarfile $packname $input1 $input2 $output
