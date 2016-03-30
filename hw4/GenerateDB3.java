import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import javax.swing.JOptionPane;

/**
 * CS561 Project 4
 * Team 10
 * Date Mar/25/2016.
 */

public class GenerateDB3 {

	private int px = 0;
	private int py = 0;
	private int seedx = 0;
    private int seedy = 0;
    private String p_record = null;

    public static void main (String arg[]) throws IOException {
    	System.out.println("start creating two datasets KSeed.csv and 2DPoints. . . . .");
        GenerateDB3 db = new GenerateDB3();
        int k = 0;
        k = Integer.parseInt(JOptionPane.showInputDialog("Enter a positive value for 'K':"));
        db.createFile(k);
        System.out.println("Complete task!!!");
    }

    public void createFile(int kMean) throws IOException {
        String FilePathP = "KSeed.csv";
        String FilePathP2 = "2DPoints.csv";

        File cfile = new File(FilePathP);
        FileWriter cWriter = new FileWriter(FilePathP);
        File pfile = new File(FilePathP2);
        FileWriter pWriter = new FileWriter(FilePathP2);

        for(int i = 1; i <= kMean; i++ ){ //the number of seed points is "kMean"
            seedx = randomInt(1, 100001);
            seedy = randomInt(1, 100001);
            p_record = i + "," + seedx + "," + seedy;
            		
            cWriter.append(p_record);
            cWriter.append('\n');
        }
        
        cWriter.flush();
        cWriter.close();

        for(int j = 1; j <= 9000000; j++ ){ //total 9000000 lines to generate 100MB
            px = randomInt(0, 100001);
            py = randomInt(0, 100001);
            p_record = px + "," + py;
            		
            pWriter.append(p_record);
            pWriter.append('\n');
        }
        
        pWriter.flush();
        pWriter.close();
    }

    public static int randomInt(int min, int max){
    	int num = (int) (Math.random() * (max - min)) + min;
        return num;
    }

}





