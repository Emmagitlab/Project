/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.hadoop.examples;

/**
 *
 * @author hadoop
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

public class SpatialJoin {

    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, Text> {
 
      
        private int areaX;
        private int areaY;
        private int x;
        private int y;
        private final Text output = new Text();
        private final Text k = new Text();
        private final Map<Integer, Integer> pointInfo = new HashMap<Integer, Integer>();
        


        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());
            String token;
            String tuple[];
          
  
            while (itr.hasMoreTokens()) {
                token = itr.nextToken();
                tuple = token.split(",");
                x = Integer.parseInt(tuple[0]);
                y = Integer.parseInt(tuple[1]);  
                areaX = x%2000==0?x/2000:x/2000+1;
                areaY = y%2000==0?y/2000:y/2000+1;
                k.set(areaX + "," + areaY);
                if(tuple.length== 2 ) //check if it is a cust name?
                {
 
                    output.set("1" + "," + tuple[0] + "," + tuple[1]);
                    
                }
                else{ 
                    
                    output.set("2" + "," + tuple[0] + "," + tuple[1] + "," + tuple[2] + "," + tuple[3]);
                }
            }  
            context.write(k, output);
            
        }
    }

    public static class TransTotalReducer
            extends Reducer<Text, Text, Text, Text> {

        private final Text result = new Text();
        private final Text outkey = new Text();
        private final ArrayList<int[]> pointList = new ArrayList();
        private final ArrayList<int[]> rectagleList = new ArrayList();

        public void reduce(IntWritable key, Iterable<FloatWritable> values,
                Context context
        ) throws IOException, InterruptedException {
            Float transTotal;
            int custCount = 0;
            float max = Float.MIN_VALUE;
            float min = Float.MAX_VALUE;
            
//            for(FloatWritable val : values){
//                custCount++;
//                transTotal = val.get();
//                if(transTotal < min){
//                    min = transTotal;
//                }
//                if(transTotal > max){
//                    max = transTotal;
//                }
//            }
//            
//            outkey.set(key.get());  //set key as country code output to result
//            result.set("; "+custCount + ", " + min+", "+max);
            context.write(outkey, result);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        if (args.length != 3) {
            System.err.println("Usage: query1  <HDFS input file1> <HDFS input file2> <HDFS output file>");
            System.exit(2);
        }
        Job job = new Job(conf, "spatialjoin");
        job.setJarByClass(SpatialJoin.class);
        job.setMapperClass(TokenizerMapper.class);
        //job.setMapperClass(TokenizerMapper.class);
        //job.setCombinerClass(FloatSumReducer.class);
        job.setReducerClass(TransTotalReducer.class);
        job.setNumReduceTasks(0);       
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.setInputPaths(job, new Path(args[0]),new Path(args[1]));
        MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, TokenizerMapper.class);
        MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, TokenizerMapper.class);
        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
