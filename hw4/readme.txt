Team_10
CS561_hw4
Mar/27/2016

Keynote:

0. Generate all dataset:
> bash generate_upload.sh 
So this command line will generate all five datasets for the following MR jobs using. 

1. Spartial-Join:
To run the Spartial-Join, use the bash command line:
> bash build1.sh
> bash run1.sh
Then, there will be an window pop out so you can choose to enter a rectangle with W(x1, y1, x2, y2) int values.
If you choose not to enter the rectangel, the MR job will join the entire points set with all other rectangles.

2. Customers Input Formate:
To run this MR jobs, use the bash command line:
> bash build2.sh
> bash run2.sh

3.K-Means:
To run the k-mean, use the bash command line:
> bash build3.sh
> bash run3.sh
there will be at most 6 iteration, so there are 6 output files in HDFS named KMeans_out0 to KMeans_out5
And I use the combiner, so that's why the reducer runs so fast as you can see!
The mapper has calculate the closest points to the seed points, which takes heavy job in the MapReduce. 

