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
import org.apache.hadoop.util.GenericOptionsParser;

public class Query2{

  public static class TokenizerMapper 
       extends Mapper<Object, Text, Text, FloatWritable>{
    
    private final static FloatWritable transTotal = new FloatWritable();
    private Text custID = new Text();
   
	/**
	* key:
	* value:
	* context: 
	*/
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString());
      String token;
      String tuple[];
      
      while (itr.hasMoreTokens()) {
        token = itr.nextToken();
        tuple = token.split(",");
        custID.set(tuple[1]);
        transTotal.set(Float.parseFloat(tuple[2]));
        context.write(custID, transTotal);
	// System.out.println(itr.toString());
      }
    }
  }
  
  public static class FloatSumReducer 
       extends Reducer<Text, FloatWritable , Text , FloatWritable>{
    private FloatWritable result = new FloatWritable();

    public void reduce(Text key, Iterable<FloatWritable> values, 
                       Context context
                       ) throws IOException, InterruptedException {
      float sum = 0;
      int count = 0;
      String outkey;
      for (FloatWritable val : values) {
         sum += val.get();
         count++;
      }
      outkey = key.toString() + ", " + count;
      key.set(outkey);
      result.set(sum);
      context.write(key, result);
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    if (args.length != 2) {
      System.err.println("Usage: query2 <HDFS input file> <HDFS output file>");
      System.exit(2);
    }
    Job job = new Job(conf, "query2");
    job.setJarByClass(Query2.class);
    job.setMapperClass(TokenizerMapper.class);
    //job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(FloatSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setNumReduceTasks(6);
    job.setOutputValueClass(FloatWritable.class);
    
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
