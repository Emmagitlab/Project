/**
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.hadoop.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class KMeansClustering{
  public static Map<Integer, Integer[]> seedpoints = new HashMap<Integer, Integer[]>();
  public static int itrcount = 0;
  public static boolean endflag = false;
  
  public static class KMeansMapper 
       extends Mapper<Object, Text, IntWritable, Text>{
    
    private IntWritable seedID = new IntWritable();
    private Text inpoint = new Text();
//    private Map<Integer, Integer[]> seedpoints = new HashMap<Integer, Integer[]>();
//    private final ArrayList<Integer[]> seedlist= new ArrayList<Integer[]>();
//    private Map<Integer, ArrayList<Integer[]>> pointclust = new HashMap<Integer, ArrayList<Integer[]>>();
    
    public void setup(Context context) throws IOException, InterruptedException{
        Path pt = new Path("./p4_dataset/KSeed.csv");     //Location of file in HDFS
        String thepath;
        Path pt1;
        
        FileSystem fs = FileSystem.get(new Configuration());
        
        for(int n=0; n<6; n++){
            thepath = "./KMeans_out" + n + "/part-r-00000";  //switch to next output file
            pt1 = new Path(thepath);
            if(fs.exists(pt1)){
                pt = new Path(thepath);
            }else{
                break;
            }
        }
        
        BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(pt)));
        String line;
        String tuple1[];
        int kid = 0;
        
        while ((line=br.readLine())!=null){
            line = line.replaceAll("\t", ","); //here is the tricky part, the output file will use tab to seperate key and values
            tuple1 = line.split(",");
            if(tuple1.length==3){
                Integer list[] = new Integer[2];
                kid = Integer.parseInt(tuple1[0]);
                list[0] = Integer.parseInt(tuple1[1]);
                list[1] = Integer.parseInt(tuple1[2]);
                seedpoints.put(kid, list);  //need to open here
            }
        }
    }
    
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      String prevalue = value.toString().replaceAll("\t", ","); //the output file has tab to separate key and values
      StringTokenizer itr = new StringTokenizer(prevalue);
      String token;
      int kmeansID = 1;
      String tuple[];
      float cmin1;
      float min_dis1 = Float.MAX_VALUE;
      
      while (itr.hasMoreTokens()) {
        token = itr.nextToken();
        tuple = token.split(",");
        
        if(tuple.length == 3){
            if(tuple[0]==""||tuple[1]==""||tuple[2]==""){
                continue;
            }
            
            seedID.set(Integer.parseInt(tuple[0]));     //centroid seed ID
            inpoint.set(token);                  //centroid x y position 
            context.write(seedID, inpoint);      //write centroid points to reducer
        }else if(tuple.length == 2){
            if(seedpoints.isEmpty()||tuple[0]==""||tuple[1]==""){
                //do nothing
            }else{
                int x1 = Integer.parseInt(tuple[0]);  //x1
                int y1 = Integer.parseInt(tuple[1]);  //y1
                Integer oneseed[];

                for(int n=1; n<=seedpoints.size(); n++){
                    oneseed = seedpoints.get(n);
                    if(oneseed == null){
                        // do nothing
                    }else{
                        cmin1 = distance(oneseed, x1, y1);
                        if(cmin1 < min_dis1){
                            min_dis1 = cmin1;
                            kmeansID = n;
                        }
                    }
                }
                
                min_dis1 = Float.MAX_VALUE;
                seedID.set(kmeansID); 
                inpoint.set(token);
                context.write(seedID, inpoint); //write the test points to reducer
            }
        }
      }

    }
    
    public float distance(Integer[] seed, int x, int y){
        float r;
        int kx = seed[0];
        int ky = seed[1];
        r = (float)Math.sqrt(Math.pow(Math.abs(kx-x), 2.0)+Math.pow(Math.abs(ky-y), 2.0));
        return r;
    }
  }
  
  public static class KMeansReducer 
       extends Reducer<IntWritable, Text , IntWritable , Text>{
    private Text result = new Text();
    private IntWritable outseed = new IntWritable();
    private Map<Integer, Integer[]> pointclust = new HashMap<Integer, Integer[]>();
    
    public void reduce(IntWritable key, Iterable<Text> values, Context context
                       ) throws IOException, InterruptedException {
      String info;
      String tuples[];
      double sumofX = 0;  //prevent from buffer overflow, must use double
      double sumofY = 0;
      int countp = 0;
      int x;
      int y;
      int avgX;
      int avgY;
      int clustID = Integer.parseInt(key.toString());
      Integer savedTuple[] = new Integer[2];
      savedTuple[0] = 1;
      savedTuple[1] = 1;
      
      /**
       * Here is to calculate smallest distant and group the points to k-means
       */
      for (Text val : values) {
         info = val.toString();
         //info = info.replaceAll("\\s", "");
         tuples = info.split(","); 
      
         if(tuples.length == 3){   //this is from seed points, only done once
             clustID = Integer.parseInt(tuples[0]);
             savedTuple[0] = Integer.parseInt(tuples[1]);  //save the original x
             savedTuple[1] = Integer.parseInt(tuples[2]);  //save the original y
             Integer newSeed[] = new Integer[2];
             newSeed[0] = savedTuple[0];
             newSeed[1] = savedTuple[1];
             pointclust.put(clustID, newSeed);

         }else if(tuples.length == 2){   //this is from test points
            x = Integer.parseInt(tuples[0]);  //x1
            y = Integer.parseInt(tuples[1]);  //y1
            
            sumofX += x;
            sumofY += y;
            countp++;
         }
      }
      
      if(countp!=0){
        avgX = (int)(sumofX/countp);
        avgY = (int)(sumofY/countp);
        clustID = Integer.parseInt(key.toString());
        outseed.set(clustID);
        result.set(avgX + "," + avgY);   //change here
        context.write(outseed, result);  //write out to next MapReduce 
      }
    } //end of reducer function
  }
  
  public static void main(String[] args) throws Exception {
     Configuration conf = new Configuration(false);
     int n = 1;
     
    if (args.length != 3) {
      System.err.println("Usage: KMeansClustering <HDFS input file1> <HDFS input file2> <HDFS output file>");
      System.exit(2);
    }
    
    Job job = new Job(conf, "K_Means_"+0);
    job.setJarByClass(KMeansClustering.class);
    job.setMapperClass(KMeansMapper.class);
    job.setMapOutputKeyClass(IntWritable.class);
    job.setMapOutputValueClass(Text.class);
    job.setCombinerClass(KMeansReducer.class);
    job.setReducerClass(KMeansReducer.class);
    job.setNumReduceTasks(1);
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(Text.class);
    
    MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, KMeansMapper.class);
    MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, KMeansMapper.class);
    FileOutputFormat.setOutputPath(job, new Path(args[2]));
    
    if(job.waitForCompletion(true)){
        while(job.waitForCompletion(true) && n<6){
            Path input2 = new Path("./KMeans_out"+(n-1)+"/part-r-00000");
            FileSystem fs1 = FileSystem.get(new Configuration());
            if(!fs1.exists(input2)){  //if there is no output file, means all seed points keep no change, so exit savely
                System.exit(job.waitForCompletion(true) ? 0 : 1);
            }
            
            Configuration conf1 = new Configuration(false);
            job = new Job(conf1, "K_Means_"+n);
            job.setJarByClass(KMeansClustering.class);
            job.setMapperClass(KMeansMapper.class);
            job.setMapOutputKeyClass(IntWritable.class);
            job.setMapOutputValueClass(Text.class);
            job.setCombinerClass(KMeansReducer.class);
            job.setReducerClass(KMeansReducer.class);
            job.setNumReduceTasks(1);
            job.setOutputKeyClass(IntWritable.class);
            job.setOutputValueClass(Text.class);

            MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, KMeansMapper.class);
            MultipleInputs.addInputPath(job, input2, TextInputFormat.class, KMeansMapper.class);
            FileOutputFormat.setOutputPath(job, new Path("./KMeans_out"+n));
            n++;  //increment the iteration
        }
    }
    
    System.exit(job.waitForCompletion(true) ? 0 : 1);
    
  }
  
}
