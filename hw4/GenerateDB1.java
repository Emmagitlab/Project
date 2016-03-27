import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * CS561 Project 4
 * Team 10
 * Date Mar/25/2016.
 */

public class GenerateDB1 {

	private int px = 0;
	private int py = 0;
    private int rectangle_x1 = 0;
    private int rectangle_y1 = 0;
    private int rectangle_x2 = 0;
    private int rectangle_y2 = 0;
    private int height = 0;
    private int width = 0;
    private String p_record = null;
    private String r_record = null;

   // private String transactionFilePath = null;

    public static void main (String arg[]) throws IOException {
    	System.out.println("start creating P datasets and R datasets. . . . .");
        GenerateDB1 db = new GenerateDB1();
        db.createFile();
        System.out.println("Complete task!!!");
    }

    public void createFile() throws IOException {
        String FilePathP = "PDataset.csv";
        String FilePathR = "RDataset.csv";

        File cfile = new File(FilePathP);
        File tfile = new File(FilePathR);
        FileWriter cWriter = new FileWriter(FilePathP);
        FileWriter tWriter = new FileWriter(FilePathR);

        for(int i = 1; i <= 100; i++ ){   //total 9,000,000 lines to generate 100MB
            px = randomInt(1, 100001);  //x and y range from 1 to 100000
            py = randomInt(1, 100001);
            p_record = px + "," + py;

            cWriter.append(p_record);
            cWriter.append('\n');
        }
        
        cWriter.flush();
        cWriter.close();
        
        for(int j = 1; j <= 40; j++ ) {   //total 4,600,000 lines to generate 100MB
            rectangle_x1 = randomInt(1, 100001);
            rectangle_y1 = randomInt(1, 100001);
            rectangle_x2 = rectangle_x1 + randomInt(1, 56); //rectange height and width range
            rectangle_y2 = rectangle_y1 + randomInt(1, 56);
            
            if(rectangle_x2 > 100000){  //check x value for exceeding outside range
            	rectangle_x2 = 100000;
            }
            
            if(rectangle_y2 > 100000){  //check y value for exceeding outside range
            	rectangle_y2 = 100000;
            }
            
            r_record = rectangle_x1 + "," + rectangle_y1 + "," +  rectangle_x2 + "," + rectangle_y2;

            tWriter.append(r_record);
            tWriter.append('\n');
        }
        tWriter.flush();
        tWriter.close();

    }

    public static int randomInt(int min, int max){
    	int num = (int) (Math.random() * (max - min)) + min;
        return num;
    }
}





