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

/**
 * CS561 hw1
 * Team_10 Fangyu Lin, Mei Yang
 * Feb/01/2015
 */
package org.apache.hadoop.examples;

import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Query1{

  public static class TokenizerMapper 
       extends Mapper<Object, Text, Text, Text>{
    
    private final static Text one = new Text();
    private Text word = new Text();

	/**
	* key:
	* value:
	* context: 
	*/
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString());
      String tuple[];
      String token;
      while (itr.hasMoreTokens()) {
        token = itr.nextToken();
        tuple = token.split(",");
        if(tuple.length==5 && (tuple[3].equals("2") || tuple[3].equals("3") ||
           tuple[3].equals("4") || tuple[3].equals("5") || 
           tuple[3].equals("6"))){
            word.set(token);
            context.write(word, one);
        }
	// System.out.println(itr.toString());
      }
    }
  }
  

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    if (args.length != 2) {
      System.err.println("Usage: query1 <HDFS input file> <HDFS output file>");
      System.exit(2);
    }
    Job job = new Job(conf, "Query1");
    job.setJarByClass(Query1.class);
    job.setMapperClass(TokenizerMapper.class);
    // job.setCombinerClass(IntSumReducer.class);
    // job.setReducerClass(IntSumReducer.class);
    // job.setOutputKeyClass(Text.class);
    job.setNumReduceTasks(0);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
