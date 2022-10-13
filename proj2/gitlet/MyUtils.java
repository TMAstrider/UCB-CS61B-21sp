package gitlet;
import static java.lang.System.*;
public class MyUtils {
    /**
     * this is my Utils for proj2
     */
    public static void exit(String warning) {
        message(warning);
        System.exit(0);
    }

    /**
     * print out a message
     */
    public static void message(String msg) {
        System.out.println(msg);
    }
}
