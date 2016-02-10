Team_10
CS561 hw1
Creaters: Fangyu Lin, Mei Yang
Date: Feb/10/2016

1. Create Datasets: DB.java
> Javac DB.java   //compile the java file
> Java DB         //run class file

2. Upload Data into Hadoop:please use generate_upload.sh to generate data files with DB.java and upload it to specific folder ./p1_dataset/
> hadoop fs -put Customers.csv Transactions.csv p1_dataset
Note:command line > bash generate_upload.sh

3.1 Please excute > bash build.sh to compile Query1.java
and > bash run.sh to get result.(All the records end with 1 as value, 1 has no specific meaning, for us to check result.)
Note:command line > bash *.sh

3.2 Please excute > bash build2.sh to compile Query2.java and > bash run2.sh to get result for result without combiner. 
Also build2_cb.sh and run2_cb.sh to get result with combiner.

Differences(reference to performance.):

 Items with considerable reduce of excution with combiner and without combiner:
  1) FILE_BYTES_READ is less of excution with combiner than without combiner
  2) FILE_BYTES_WRITTEN is less of excution with combiner than without combiner

 Conclusion:File read/write has significant reduce, which is beneficail to performance.

  3) Map output materialized bytes
  4) Reduce shuffle bytes
  5) Physical memory (bytes) snapshot
  6) Spilled Records
  7) Reduce input records

 Conclusion:There are less records written in disk which reduces the time of reducetask. With less reduce input records based on the combiner function, which also help enhance perfomance of mapreduce.


3.3 Please excute build3.sh to compile Query3.java and run3.sh to get result as required.

3.4 This task is finished in a single Map-Reduce job, please excute bash build4.sh and bash run4.sh to check.

3.5 Two Map-Reduce is created by Query5.java with same excute method.
> bash build5.sh and > bash run5.sh
    The first job is named "Query5" and the second job is named "Query5_1".
