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
import javax.swing.JOptionPane;

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
    public static int x1, x2, y1, y2;
    
    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, Text> {

        private int areaX;
        private int areaY;
        private int x;
        private int y;
        private final Text output = new Text();
        private final Text k = new Text();
        public int mx;
        public int my;
        
//        public void setup(Context context) throws IOException, InterruptedException {
//            int flag = JOptionPane.showConfirmDialog(null, "Do you want to enter rectangel?");
//            if(flag == JOptionPane.YES_OPTION){
//                x1 = Integer.parseInt(JOptionPane.showInputDialog("Enter a positive int value for 'X1':"));
//                y1 = Integer.parseInt(JOptionPane.showInputDialog("Enter a positive int value for 'Y1':"));
//                x2 = Integer.parseInt(JOptionPane.showInputDialog("Enter a positive int value for 'X2':"));
//                y2 = Integer.parseInt(JOptionPane.showInputDialog("Enter a positive int value for 'Y2':"));
//            }else{
//                x1 = x2 = y1 = y2 = -1;
//            }
//        }

        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());
            Configuration mapconf = context.getConfiguration();  //here is the key, how parameter pass into mapper or reducer
            String token;
            String tuple[];
            int area1;
            int area2;
            boolean flag = false;
            int rx1;
            int rx2;
            int ry1;
            int ry2;
            int mx1 = Integer.parseInt(mapconf.get("mx1"));
            int my1 = Integer.parseInt(mapconf.get("my1"));
            int mx2 = Integer.parseInt(mapconf.get("mx2"));
            int my2 = Integer.parseInt(mapconf.get("my2"));
            
            if(mx1>0 && my1>0 && mx2>0 && my2>0){
                flag = true;
            }
            
            while (itr.hasMoreTokens()) {
                token = itr.nextToken();
                tuple = token.split(",");

                if(tuple.length== 2 ){  //it is from the point sets
                    x = Integer.parseInt(tuple[0]);
                    y = Integer.parseInt(tuple[1]); 
                    if(flag && (x<mx1 || x>mx2 || y<my1 || y>my2)){
                        continue;
                    }
                    
                    areaX = x%2000==0?x/2000:x/2000+1;
                    areaY = y%2000==0?y/2000:y/2000+1;
                    area1 = areaX+5*(areaY-1);
                    k.set(area1+"");
                    output.set("1" + "," + tuple[0] + "," + tuple[1]);
                    context.write(k, output);
                }else{    //it is from the rectange sets                    
                    rx1 = Integer.parseInt(tuple[1]);
                    ry1 = Integer.parseInt(tuple[2]);
                    rx2 = Integer.parseInt(tuple[3]);
                    ry2 = Integer.parseInt(tuple[4]);
                    if(flag && (rx2<mx1||rx1>mx2||ry2<my1||ry1>my2)){
                        continue;
                    }
                    
                    x = Integer.parseInt(tuple[1]);
                    y = Integer.parseInt(tuple[2]); 
                    areaX = x%2000==0?x/2000:x/2000+1;
                    areaY = y%2000==0?y/2000:y/2000+1;
                    area2 = areaX+5*(areaY-1);
                    k.set(area2+"");
                    output.set("2" + "," + tuple[0] + "," + tuple[1] + "," + tuple[2] + "," + tuple[3] + "," + tuple[4]);
                    context.write(k, output);
                }
            }
            
        }
    }

    public static class TransTotalReducer
            extends Reducer<Text, Text, Text, Text> {

        private final Text result = new Text();
        private final Text outkey = new Text();
//        @SuppressWarnings("empty-statement")
        public void reduce(Text key, Iterable<Text> values,
                Context context
        ) throws IOException, InterruptedException {
          ArrayList<String> pointList = new ArrayList<>();
          ArrayList<String> rectagleList = new ArrayList<>();

           String tuplev[];
           String pointTuple[];
           String recTuple[];
           int pointx;
           int pointy;
           int recx1;
           int recy1;
           int recx2;
           int recy2;
            
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
//            length1 = pointList.size();
//            length2 = rectagleList.size();
            for(String pvalue: pointList){
                pointTuple = pvalue.split(",");
                pointx = Integer.parseInt(pointTuple[1]);
                pointy = Integer.parseInt(pointTuple[2]);
                
                for(String rvalue: rectagleList){
                    recTuple = rvalue.split(",");
                    recx1 = Integer.parseInt(recTuple[2]);
                    recy1 = Integer.parseInt(recTuple[3]);
                    recx2 = Integer.parseInt(recTuple[4]);
                    recy2 = Integer.parseInt(recTuple[5]);
                    
//                  result.set(pvalue + " |;| " + rvalue);
//                  context.write(key, result);
                    if (pointx >= recx1 && pointy >= recy1 && pointx <= recx2 && pointy <= recy2) {
                        result.set(recTuple[1] + "," + pointx + "," + pointy);
                        context.write(key,result);
                    }
                }
                
            }
            
            pointList.clear();
            rectagleList.clear();
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
            
            
//            outkey.set("");  //set key as country code output to result
            
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
        
        int flag = JOptionPane.showConfirmDialog(null, "Do you want to enter a rectangel?");
        if(flag == JOptionPane.YES_OPTION){
            x1 = Integer.parseInt(JOptionPane.showInputDialog("Enter a positive int value for 'X1':"));
            y1 = Integer.parseInt(JOptionPane.showInputDialog("Enter a positive int value for 'Y1':"));
            x2 = Integer.parseInt(JOptionPane.showInputDialog("Enter a positive int value for 'X2':"));
            y2 = Integer.parseInt(JOptionPane.showInputDialog("Enter a positive int value for 'Y2':"));
            if(x1>x2){   //make sure x1 < x2
                int temp = x1;
                x1 = x2;
                x2 = temp;
            }
            if(y1>y2){   //make sure y1 < y2
                int ttp = y1;
                y1 = y2;
                y2 = ttp;
            }
        }else{
            x1 = x2 = y1 = y2 = -1;
        }
        
        conf.setInt("mx1", x1);
        conf.setInt("mx2", x2);
        conf.setInt("my1", y1);
        conf.setInt("my2", y2);
        
        Job job = new Job(conf, "spatialjoin");
        job.setJarByClass(SpatialJoin.class);
        job.setMapperClass(TokenizerMapper.class);
        //job.setCombinerClass(FloatSumReducer.class);
        job.setReducerClass(TransTotalReducer.class);
        job.setNumReduceTasks(1);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        
        job.setInputFormatClass(TextInputFormat.class);
        MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, TokenizerMapper.class);
        MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, TokenizerMapper.class);
        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        
        if(job.waitForCompletion(true)){
            System.out.println("=============Here is Your Input Rectangle=========================");
            System.out.println(x1 + "," + y1 + "," + x2 + "," + y2);
        }
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

