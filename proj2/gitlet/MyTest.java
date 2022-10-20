package gitlet;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class MyTest {
    public static final File CWD = new File(System.getProperty("user.dir"));


    public static void main(String[] args) {
//        LocalDateTime myDateObj = LocalDateTime.now();
//        System.out.println("Before formatting: " + myDateObj);
//        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("HH:mm:ss, E, dd MMM yyyy");
////        /**  00:00:00 UTC, Thursday, 1 January 1970 */
////        System.out.println("After formatting: 00:00:00 UTC, Thursday, 1 January 1970");
//        String formattedDate = myDateObj.format(myFormatObj);
//        System.out.println("After formatting: " + formattedDate)
//        Date newDate = new Date();
//        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("HH:mm:ss, E, dd MMM yyyy");
////        String formattedDate = myFormatObj.format(newDate);
        Date newD = new Date(0);
//        System.out.println(newD.toString());
//        String pattern = "MM-dd-yyyy";
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//        Date date = simpleDateFormat.parse(newD);
//        System.out.println(date);
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        System.out.println(dateFormat.format(newD).toString());

        Date day = new Date();
        System.out.println(getTimestamp(day));
        System.out.println(CWD.getPath());
        System.out.println(CWD.getAbsolutePath().toString());
        System.out.println("Hello World");


        Blob newBlob = new Blob(Repository.head);
        System.out.println(newBlob.getBlobId());
        boolean a = Repository.head.isDirectory();
        System.out.println(a);
        boolean b = CWD.isDirectory();
        System.out.println(b);
    }

    public static String getTimestamp(Date referredTimestamp) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        return dateFormat.format(referredTimestamp);
    }
}
