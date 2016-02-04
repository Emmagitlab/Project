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

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Query3{

  public static class TokenizerMapper 
       extends Mapper<Object, Text, Text, Text>{
    
    private final static Text custInfo = new Text();
    private Text custID = new Text();
  
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString());
      String token;
      String tuple[];
      
      while (itr.hasMoreTokens()) {
        token = itr.nextToken();
        tuple = token.split(",");
        custID.set(tuple[0]);
        custInfo.set(tuple[1]+","+tuple[4]);
        context.write(custID, custInfo);
	// System.out.println(itr.toString());
      }
    }
  }
  
  public static class TokenizerMapper2 
       extends Mapper<Object, Text, Text, Text>{
    
    private final static Text transInfo = new Text();
    private Text custID = new Text();
   
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString());
      String token;
      String tuple[];
      
      while (itr.hasMoreTokens()) {
        token = itr.nextToken();
        tuple = token.split(",");
        custID.set(tuple[1]);
        transInfo.set(tuple[2]+","+tuple[3]);
        context.write(custID, transInfo);
	// System.out.println(itr.toString());
      }
    }
  }
  
  public static class FloatSumReducer 
       extends Reducer<Text, FloatWritable , Text , Text>{
    private Text result = new Text();

    public void reduce(Text key, Iterable<Text> values, 
                       Context context
                       ) throws IOException, InterruptedException {
      float sum = 0;
      int count = 0;
      String outkey = "unknown";
      String totalsum = "unknown";
      String info[];
      Boolean flag = true;
      int min = 9999;
      int numItem = 0;
      
      for (Text val : values) {
         info = (val.toString()).split(",");
         if(flag && info[0].matches("^[A-Za-z]+$")){  //alpha only
             outkey = key.toString()+","+val.toString();
             flag = false;
         }else{
             count++;
             sum += Float.parseFloat(info[0]);
             numItem = Integer.parseInt(info[1]);
             if(min > numItem){
                 min = numItem;
             }
         }
      }
      
      key.set(outkey);
      result.set(count+","+sum+","+min);
      context.write(key, result);
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    if (args.length != 3) {
      System.err.println("Usage: query3 <HDFS input file1> <HDFS input file2> <HDFS output file>");
      System.exit(2);
    }
    Job job = new Job(conf, "query3");
    job.setJarByClass(Query3.class);
    job.setMapperClass(TokenizerMapper.class);
    //job.setCombinerClass(FloatSumReducer.class);
    job.setReducerClass(FloatSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setNumReduceTasks(6);
    job.setOutputValueClass(FloatWritable.class);
    
    MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, TokenizerMapper.class);
    MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, TokenizerMapper2.class);
    FileOutputFormat.setOutputPath(job, new Path(args[2]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
