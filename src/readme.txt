Team_10
CS561_hw4
Mar/27/2016

Keynote:

1. Spartial-Join:

2. Customers Input Formate:

3.K-Means:
To run the k-mean, use the bash command line:
> bash build3.sh
> bash run3.sh
there will be at most 6 iteration, so there are 6 output files in HDFS named KMeans_out0 to KMeans_out5
And I use the combiner, so that's why the reducer runs so fast as you can see!
The mapper has calculate the closest points to the seed points, which takes heavy job in the MapReduce. 

