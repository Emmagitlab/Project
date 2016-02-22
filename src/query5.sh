#!/bin/bash
# this is query5.sh file to run the hadoop streaming

mapfile="mapper.py"
redfile="reducer.py"
infile1="hw2/Customers.csv"
infile2="hw2/Transactions.csv"
outfile="hw2_q5_out"
jarfile="/usr/share/hadoop/contrib/streaming/hadoop-streaming-1.2.1.jar"

hadoop fs -rmr $outfile

hadoop jar $jarfile \
-mapper mapper.py \
-reducer reducer.py \
-input $infile1 \
-input $infile2 \
-output $outfile \
-file mapper.py \
-file reducer.py
#hadoop jar $jarfile -D mapred.reduce.tasks=6 -file $mapfile -mapper $mapfile -file $redfile -reducer $redfile -input $infile1 -input $infile2 -output $outfile -partitioner org.apache.hadoop.mapred.lib.KeyFieldBasedPartitioner -jobconf stream.map.output.field.separator=^ -jobconf stream.num.map.output.key.fields=4 -jobconf map.output.key.field.separator=^ -jobconf num.key.fields.for.partition=1

