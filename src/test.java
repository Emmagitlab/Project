import java.util.Random;

/**
 * Created by T440 on 2016/1/30.
 */
public class test {
    public static void main(String arg[]){

        int stringNum = 0;
        String name = null;

        stringNum = (int) (Math.random() * (21 - 10)) + 10;
        name = randomString(stringNum);

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


