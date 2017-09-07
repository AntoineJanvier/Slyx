package slyx.utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import static slyx.utils.SlyxAnnotation.Type.EXCEPTION;

@SlyxAnnotation(todo = "Use this class to redirect all errors", type = EXCEPTION)
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
    }
}
