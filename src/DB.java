import com.sun.rowset.internal.Row;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Random.*;

/**
 * Created by T440 on 2016/1/30.
 */
public class DB {

    private int ID = 0;

    private String name = null;

    private int age = 0;

    private int countryCode = 0;

    private float salary = 0;

    private  int transID = 0;

    private  int custID = 0;

    private float transTotal = 0;

    private int transNumItems = 0;

    private String transDesc = null;

    private ArrayList<Row> data = new ArrayList<Row>();

    private String file_path = null;

    private String c_record = null;

    private String t_record = null;

   // private String transactionFilePath = null;

    public static void main (String arg[]) throws IOException {

        DB db = new DB();
        db.createFile();


    }

//    public static void DB(String file_name) throws IOException {
//
//        String file_path = file_name + ".csv";
//        //this.customerFilePath = file_name + ".csv";
//        //this.transactionFilePath = file_name + ".cvs";
//
//        File File = new File(file_path);
//        //File tFile = new File(transactionFilePath);
//
//        if (!File.exists()) {
//            File.createNewFile();
//        }
//
//    }
    public void createFile() throws IOException {
        String customerFilePath = "Customers.csv";
        String transactionFilePath = "Transactions.csv";s

        File cfile = new File(customerFilePath);
        File tfile = new File(transactionFilePath);

        FileWriter cWriter = new FileWriter(customerFilePath);

        FileWriter tWriter = new FileWriter(transactionFilePath);


        String att = "";


        for(int i = 0; i < 50000; i++ ) {
            ID = i+1;
            name = randomString((int) (Math.random() * (21 - 10)) + 10);
            age = randomInt(1, 70);
            countryCode = randomInt(1, 10);
            salary = randomFloat(100.0f, 10001.0f);
            c_record = ID + "," + name + "," + age+ ","  + countryCode+ "," + salary;

            cWriter.append(c_record);
            cWriter.append('\n');

        }
        cWriter.flush();
        cWriter.close();
        for(int j = 0; j < 5000000; j++ ) {
            transID = j+1;
            transDesc = randomString((int) (Math.random() * (51 - 20)) + 20);
            transTotal = randomInt(10, 1000);
            custID =  randomInt(1,5000);
            transNumItems  = randomInt(1, 10);
            t_record = transID + "," + custID + "," + transTotal+ ","  + transNumItems+ "," + transDesc;

            tWriter.append(t_record);
            tWriter.append('\n');

        }
        tWriter.flush();
        tWriter.close();

    }

    public static String randomString(int length){
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < length; ++i){
            int number = random.nextInt(52);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static int randomInt( int max, int min){

            int num = (int) (Math.random() * (max - min)) + min;

        return num;
    }

    public static float randomFloat( float max, float min){

        Random r = new Random();
        float num =  r.nextFloat()*(max - min) + min;

        return num;
    }




    }





