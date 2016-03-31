import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Created by T440 on 2016/3/26.
 */
public class Database {
    private int x=0;
    private int y=0;
    Random r = new Random();
    private String points = null;
    private String rectagle = null;


    public static void main(String arg[]) throws IOException {
        System.out.println("start creating two datasets DB_P.csv and DB_R.csv  .........");
        Database db = new Database();
        db.createFile();
        System.out.println("Complete task!!!");

    }

    public void createFile() throws IOException {
        String filename1 = "DB_P.csv";
        String filename2 = "DB_R.csv";
        FileWriter writer1 = new FileWriter (filename1);
        FileWriter writer2 = new FileWriter(filename2);

        for (int i = 0; i < 11000000; i++) {  //original 11000000 to get a 100MB size
            int x = randomInt(10001, 1);
            int y = randomInt(10001, 1);
            points = x + "," + y;
            writer1.append(points);
            writer1.append('\n');

        }
        writer1.flush();
        writer1.close();
        for (int i = 0; i < 3720000; i++) {   //original 3600000 to get a 100MB size
            String R = "r" + i;
            int top_left_x = randomInt(10001, 1);
            int top_left_y = randomInt(10001, 1);
            int height = randomInt(21,1);
            int width = randomInt(6,1);
            int bottom_right_x = top_left_x + width;
            int bottom_right_y = top_left_y + height;
            rectagle = R +","+top_left_x + "," + top_left_y + "," + bottom_right_x + "," + bottom_right_y ;
            writer2.append(rectagle);
            writer2.append('\n');

        }
        writer2.flush();
        writer2.close();
    }

    public static int randomInt( int max, int min){
        int num = (int) (Math.random() * (max - min)) + min;
        return num;
    }

}


