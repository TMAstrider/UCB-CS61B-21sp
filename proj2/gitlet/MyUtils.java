package gitlet;
import static java.lang.System.*;
public class MyUtils {
    /** This is MyUtils for Gitlet */
    public static void exit(String warning) {
        message(warning);
        System.exit(0);
    }

    /** Print out a message */
    public static void message(String msg) {
        System.out.println(msg);
    }


}
