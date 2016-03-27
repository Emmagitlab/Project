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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.lib.ChainMapper;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class KMeansClustering{
    
  public static class TokenizerMapper 
       extends Mapper<Object, Text, IntWritable, Text>{
    
    private IntWritable seedID = new IntWritable();
    private Text inpoint = new Text();
    private final ArrayList<Integer[]> seedlist= new ArrayList<Integer[]>();
    
    public void setup(Context context) throws IOException{
        Path pt = new Path("p4_dataset/KSeed.csv");     //Location of file in HDFS
        FileSystem fs = FileSystem.get(new Configuration());
        BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(pt)));
        String line;
        String tuple[];
        Integer list[] = new Integer[3];
        int count = 1;
        
        while ((line=br.readLine())!=null){
            tuple = line.split(",");
            if(tuple.length==3){
                list[0] = Integer.parseInt(tuple[1]);
                list[1] = Integer.parseInt(tuple[1]);
                list[2] = Integer.parseInt(tuple[2]);
                seedlist.add(list);  //custID + countryCode
                count++;
            }
        }
        
    }
    
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString());
      String token;
      String tuple[];
      int point[] = new int[2];
      float cmin = 0;
      float min_dis = Float.MAX_VALUE;
      int kmeansID = 1;
      
      while (itr.hasMoreTokens()) {
        token = itr.nextToken();
//        tuple = token.split(",");
        seedID.set(kmeansID);
        inpoint.set(token);
        context.write(seedID, inpoint);
        
      }
    }
  }
  
  public static class TransSumReducer 
       extends Reducer<IntWritable, Text , IntWritable , Text>{
    private Text result = new Text();
    private IntWritable outseed = new IntWritable();
    private int itrcount = 0;
    private Map<Integer, Integer[]> seedpoints = new HashMap<Integer, Integer[]>();
    Map<Integer, ArrayList<Integer[]>> pointclust = new HashMap<Integer, ArrayList<Integer[]>>();
    private Iterable<Text> savedVal;
    private Context savedCont;
    private IntWritable savedKey;
    
    public void reduce(IntWritable key, Iterable<Text> values, 
                       Context context
                       ) throws IOException, InterruptedException {
      savedKey = key;
      savedVal = values;
      savedCont = context;
      String info;
      String tuples[];
      int x = 0;
      int y = 0;
      int sumofX = 0;
      int sumofY = 0;
      int countp = 0;
      int avgX = 0;
      int avgY = 0;
      boolean flag = false;
      float cmin;
      float min_dis;
      int clustID = 1;
      boolean endflag = false; //to terminate iteration of reducer
      
      if(seedpoints.isEmpty()){
          flag = true; //check if it is the first time to enter reducer
      }
      
      /**
       * Here is to calculate smallest distant and group the points to k-means
       */
      for (Text val : values) {
         info = val.toString();
         tuples = info.split(",");
         cmin = Float.MAX_VALUE;  
         min_dis = Float.MAX_VALUE; //saved current min distance
         
         if(tuples.length == 3){   //this is from seed points
             if(flag){   //this only execute once when first time run reducer
                Integer tempoint[] = new Integer[2];
                ArrayList<Integer[]> thepoint = new ArrayList<Integer[]>();
                clustID = Integer.parseInt(tuples[0]);
                tempoint[0] = Integer.parseInt(tuples[1]);  //seed x point
                tempoint[1] = Integer.parseInt(tuples[2]);  //seed y point
                seedpoints.put(clustID, tempoint); //has saved to local memory now
                pointclust.put(clustID, thepoint); //has saved to local memory now
             }
             // else case just ignore original seed points
             // here is the test for output
             outseed.set(clustID);
             result.set(info);
             context.write(outseed, result);
         }else if(tuples.length == 2){   //this is from test points
            x = Integer.parseInt(tuples[0]);  //x1
            y = Integer.parseInt(tuples[1]);  //y1
            Integer savepoint[] = new Integer[2];
            savepoint[0] = x;
            savepoint[1] = y;
            Integer oneseed[];
            
            for(int i=1; i<=seedpoints.size(); i++){
                oneseed = seedpoints.get(i);
                cmin = distance(oneseed, x, y);
                if(cmin < min_dis){
                    min_dis = cmin;
                    clustID = i;
                }
            }
            
            ArrayList<Integer[]> temp = pointclust.get(clustID);
            if(temp==null){
                //nothing to do
            } else{
                temp.add(savepoint);
                pointclust.put(clustID, temp);  //save group of points into the map
                // here is to test output reslut
//                outseed.set(clustID);
//                result.set(info);
//                context.write(outseed, result);
             }
         }
      }
      
      /**
       * here is to recompute the k-means position
       **/
      
      ArrayList<Integer[]> somepoints = new ArrayList<Integer[]>();
      Integer orig_seed[];
      for(int i=1; i<=pointclust.size(); i++){  //recomput the k-means position
          somepoints = pointclust.get(i);
          sumofX = 0;
          sumofY = 0;
          countp = 0;
          
          if(somepoints==null){
              continue;
          }
          
          for(Integer[] onepoint: somepoints){
              sumofX += onepoint[0];
              sumofY += onepoint[1];
              countp++;
          }
          
          if(countp!=0){
            avgX = sumofX/countp;
            avgY = sumofY/countp;
          }else{
              continue;
          }
          
          orig_seed = seedpoints.get(i);
          if(orig_seed == null){
              continue;
          }
          
          if(itrcount>5){  //do no more than 6 iteration
              endflag = true;
          }else if(Math.abs(orig_seed[0]-avgX) > 10 || Math.abs(orig_seed[1]-avgY) > 10){ //threshold=3
              orig_seed[0] = avgX;
              orig_seed[1] = avgY;
              seedpoints.put(i, orig_seed);  //update the new k-means position
              endflag = false;
          }else{
              endflag = true;
          }
      }
      
      Integer outp[] = new Integer[2];
      
      if(endflag){  //iteration will be terminate here
        for(int m=1; m<=seedpoints.size(); m++){
            outseed.set(m);
            outp = seedpoints.get(m);
            result.set("Centroid: " + outp[0] + "," + outp[1]);
            context.write(outseed, result);  //output the last k-means centroid
        }
      }else{    
        //tail iteration call reducer here, increment counter
        itrcount++;
        reduce(savedKey, savedVal, savedCont);  //there is problem here.
      }
     
    }

    public float distance(Integer[] seed, int x, int y){
        float r;
        int kx = seed[0];
        int ky = seed[1];
        r = (float)Math.sqrt(Math.pow(kx-x, 2.0)+Math.pow(ky-y, 2.0));
        return r;
    }
  }
  
  public static void main(String[] args) throws Exception {
     Configuration conf = new Configuration(false);
     
     if (args.length != 3) {
      System.err.println("Usage: KMeansClustering <HDFS input file1> <HDFS input file2> <HDFS output file>");
      System.exit(2);
    }
    
    Job job = new Job(conf, "K_Means");
    job.setJarByClass(KMeansClustering.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setMapOutputKeyClass(IntWritable.class);
    job.setMapOutputValueClass(Text.class);
    //job.setCombinerClass(TransSumReducer.class);
    job.setReducerClass(TransSumReducer.class);
    job.setNumReduceTasks(1);
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(Text.class);
    
    MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, TokenizerMapper.class);
    MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, TokenizerMapper.class);
    FileOutputFormat.setOutputPath(job, new Path(args[2]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
  
}
