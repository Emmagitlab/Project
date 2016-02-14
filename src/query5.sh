#!/bin/bash
# this is query5.sh file to run the hadoop streaming

mapfile="mapper5.py"
redfile="reducer5.py"
infile1="p1_dataset/Customers.csv"
infile2="p1_dataset/Transactions.csv"
outfile="hw2_q5_out"
jarfile="/usr/share/hadoop/contrib/streaming/hadoop-streaming-1.2.1.jar"

hadoop fs -rmr $outfile

hadoop jar $jarfile -D mapred.reduce.tasks=1 -file $mapfile -mapper $mapfile -file $redfile -reducer $redfile -input $infile1 $infile2 -output $outfile
