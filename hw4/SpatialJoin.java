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
import static javax.management.Query.value;

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
            int area1;
            int area2;
          
  
            while (itr.hasMoreTokens()) {
                token = itr.nextToken();
                tuple = token.split(",");


                
//                k.set(Integer.toString(areaX) + "," + Integer.toString(areaY));
                if(tuple.length== 2 ) //check if it is a cust name?
                {
 
                    x = Integer.parseInt(tuple[0]);
                    y = Integer.parseInt(tuple[1]); 
                    areaX = x%20==0?x/20:x/20+1;
                    areaY = y%20==0?y/20:y/20+1;
                    area1 = areaX+5*(areaY-1);
                    k.set(area1 + "");
                    output.set("1" + "," + tuple[0] + "," + tuple[1]);
                    
                }
                else{ 
                    x = Integer.parseInt(tuple[1]);
                    y = Integer.parseInt(tuple[2]); 
                    areaX = x%20==0?x/20:x/20+1;
                    areaY = y%20==0?y/20:y/20+1;
                    area2 = areaX+5*(areaY-1);
                    k.set(area2+"");
                    output.set("2" + "," + tuple[0] + "," + tuple[1] + "," + tuple[2] + "," + tuple[3] + "," + tuple[4]);
                }

                
            }  
            context.write(k, output);
            
        }
    }

    public static class TransTotalReducer
            extends Reducer<Text, Text, Text, Text> {

        private final Text result = new Text();
        private final Text outkey = new Text();


        @SuppressWarnings("empty-statement")
        public void reduce(Text key, Iterable<Text> values,
                Context context
        ) throws IOException, InterruptedException {
          ArrayList<String> pointList = new ArrayList();
          ArrayList<String> rectagleList = new ArrayList();

           String tuplev[];
           int length1;
           int length2;
           String pointTuple[];
           String recTuple[];
           int pointx;
           int pointy;
           int recx;
           int recy;
           int height;
           int width;
           int recl;
           int pointl;
            
            
           for(Text val : values){
                
                tuplev= val.toString().split(",");

                if (tuplev.length == 3){
                    String point;
                    point = val.toString();
                    pointList.add(point);         
//                    result.set(val.toString()+"point");
//                    context.write(key,result);
                }else if(tuplev.length == 6) {
                
                    String rec;
                    rec = val.toString();
                    rectagleList.add(rec);
//                    result.set(val.toString()+"rec");
//                    context.write(key,result);
                }
           }
            length1 = pointList.size();
            length2 = rectagleList.size();
            
            for(String pvalue: pointList){
                pointTuple = pvalue.split(",");
                pointx = Integer.parseInt(pointTuple[1]);
                pointy = Integer.parseInt(pointTuple[2]);
                
                for(String rvalue: rectagleList){
                    recTuple = rvalue.split(",");
                    recx = Integer.parseInt(recTuple[2]);
                    recy = Integer.parseInt(recTuple[3]);
                    height = Integer.parseInt(recTuple[4]);
                    width = Integer.parseInt(recTuple[5]);
                    
                    if (pointx >= recx && pointx <= recx + width && pointy <= recy && pointy >= recy - height) {

                        result.set(recTuple[1] + "," + pointx + "," + pointy);
                        context.write(key,result);

                    } else {

                       
                    }
                }
                
            }
            
            
//            for (int i = 0; i < length1-1; i++) {
//                pointTuple = pointList.get(i).toString().split(",");
//                pointl = pointTuple.length;
//                if(pointl!=3){result.set(pointList.get(i).toString()+";point"); 
//                context.write(key, result);
//                continue;}
//                pointx = Integer.parseInt(pointTuple[1]);
//                pointy = Integer.parseInt(pointTuple[2]);
//
//                for (int j = 0; j < length2-1; j++) {
//                    recTuple = rectagleList.get(j).toString().split(",");
//                    recl = recTuple.length;
//                    if(recl!=6){result.set(recl+"rec"); continue;}
//                    recx = Integer.parseInt(recTuple[2]);
//                    recy = Integer.parseInt(recTuple[3]);
//                    height = Integer.parseInt(recTuple[4]);
//                    width = Integer.parseInt(recTuple[5]);
//
//                    if (pointx >= recx && pointx <= recx + width && pointy <= recy && pointy >= recy - height) {
//
//                        result.set(recTuple[2] + "," + pointx + "," + pointy);
//
//                    } else {
//
//                        result.set("1");
//                    }
//
//                }
//
//            }
            
            
            outkey.set("");  //set key as country code output to result
            
//           result.set(length1 + "," +length2);
          //  context.write(key, result);
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
        job.setNumReduceTasks(1);       
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setInputFormatClass(TextInputFormat.class);
       // FileInputFormat.setInputPaths(job, new Path(args[0]),new Path(args[1]));
        MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, TokenizerMapper.class);
        MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, TokenizerMapper.class);
        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

