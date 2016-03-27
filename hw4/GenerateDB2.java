import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * CS561 Project 4
 * Team 10
 * Date Mar/25/2016.
 */

public class GenerateDB2 {

	private int cid = 0;
    private String p_record = null;
    private String cName = null;
    private String addr = null;
    private int salary = 0;
    private String gender = "male";

    public static void main (String arg[]) throws IOException {
    	System.out.println("start creating Customers.json . . . . .");
        GenerateDB2 db = new GenerateDB2();
        db.createFile();
        System.out.println("Complete task!!!");
    }

    public void createFile() throws IOException {
        String FilePathP = "Customers.json";

        File cfile = new File(FilePathP);
        FileWriter cWriter = new FileWriter(FilePathP);

        for(int i = 1; i <= 410000; i++ ){ //total 410000 lines to generate 100MB
        	cid = i;
            cName = randomString(100);  //string length is 100 for name and addr
            addr = randomString(100);
            salary = randomInt(100, 1001); //salary range from 100 to 1000
            gender = (Math.random()>0.5) ? "male" : "female";
            p_record = "{Customer_ID:" + cid +",Name:" + cName + ",Address:" + addr + ",Salary:" + salary + ",Gender:" + gender + "},";

            cWriter.append(p_record);
            cWriter.append('\n');
        }
        
        cWriter.flush();
        cWriter.close();

    }

    public static int randomInt(int min, int max){
    	int num = (int) (Math.random() * (max - min)) + min;
        return num;
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
}





