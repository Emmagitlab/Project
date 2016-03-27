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
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

public class CustomerInputFormat{
    
  public static class TokenizerMapper 
       extends Mapper<Object, Text, IntWritable, Text>{
    
    private Text genderInfo = new Text();
    private IntWritable salaryID = new IntWritable();
  
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString());
      String token;
      String tuple[];
      String salaryToken[];
      String salary;
      String genderToken[];
      String gender;
      
      while (itr.hasMoreTokens()) {
        token = itr.nextToken();
        tuple = token.split(",");
        if(tuple.length == 5){
            salaryToken = tuple[3].split(":");
            salary = salaryToken[1];
            genderToken = tuple[4].split(":");
            gender = genderToken[1];
            gender = gender.replace("}", "");
            salaryID.set(Integer.parseInt(salary));  //the salary as key out
            genderInfo.set(gender); //the gender as value out
        }
        context.write(salaryID, genderInfo);
      }
    }
  }
  
  public static class SalaryCountReducer 
       extends Reducer<IntWritable, Text , Text , Text>{
    private Text result = new Text();
    private Text outkey = new Text();

    public void reduce(IntWritable key, Iterable<Text> values, 
                       Context context
                       ) throws IOException, InterruptedException {
      String gender;
      int countM = 0;
      int countF = 0;
     
      for (Text val : values) {
         gender = val.toString();
         if(gender.equals("male")){   //increment the male count
             countM++;
         }else if(gender.equals("female")){
             countF++;
         }
      }
      
      outkey.set("Salary:" + key.toString()+", ");
      result.set("MaleCount:"+ countM + ",  FemaleCount:" + countF);
      context.write(outkey, result);
    }
  }
  
  public static void main(String[] args) throws Exception {
     Configuration conf = new Configuration(false);
     if (args.length != 2) {
      System.err.println("Usage: CustomerInputFormat <HDFS input file1> <HDFS output file>");
      System.exit(2);
    }
    
    Job job = new Job(conf, "CustomerInputFormat");
    job.setJarByClass(CustomerInputFormat.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setMapOutputKeyClass(IntWritable.class);
    job.setMapOutputValueClass(Text.class);
//    job.setCombinerClass(SalaryCountReducer.class);
    job.setReducerClass(SalaryCountReducer.class);
    job.setNumReduceTasks(1);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
     System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
  
}
