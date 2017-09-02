package slyx.utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public class ErrorExit {
    public static void writeInFile(String error) {
        Date date = new Date();
        String toWrite = date.toString() + " : " + error;

        try {
            PrintWriter printWriter = new PrintWriter("log.txt", "UTF-8");
            printWriter.write(toWrite);
            printWriter.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //System.out.println(toWrite);
    }
}
