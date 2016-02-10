Team_10
CS561 hw1
Create by: Fangyu Lin, Mei Yang
Date: Feb/10/2016

1.Create Datasets: DB.java
   > Javac DB.java  //compile the java file
   > Java DB   //run class file
   
2.Upload Data into Hadoop:please use generate_upload.sh to generate data files with DB.java and upload it to specific folder ./p1_dataset/
  Note:command line > bash generate_upload.sh

3.1 Please excute > bash build.sh to compile Query1.java 
    and > bash run.sh to get result.
  Note:command line > bash *.sh

3.2 Please excute > bash build2.sh to compile Query2.java 
    and > bash run2.sh to get result for result without combiner.
    Also build2_cb.sh and run2_cb.sh to get result with combiner.

3.3 Please excute build3.sh to compile Query3.java 
    and run3.sh to get result as required.
    
3.4 This task is finished in a single Map-Reduce job, please excute bash build4.sh and bash run4.sh to check.

3.5 Two Map-Reduce is created by Query5.java with same excute method.
    > bash build5.sh and > bash run5.sh
