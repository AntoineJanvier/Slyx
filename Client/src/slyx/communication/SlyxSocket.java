package slyx.communication;

import jdk.nashorn.internal.parser.JSONParser;
import slyx.exceptions.SocketConnectionException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

/**
 * Created by Antoine Janvier
 * on 31/07/17.
 */
public class SlyxSocket extends Thread {
    private static Socket socket;

    public static void init() throws IOException {
        socket = new Socket(getIpAddress(), getPort());
    }

    public static void write(String message) throws IOException {
        PrintWriter printWriter = new PrintWriter(getSocket().getOutputStream(), true);
        printWriter.print(message);
    }
    public static String read() {
        return "Bonjour";
    }
    public static void close() throws IOException {
        socket.close();
    }

    private static Socket getSocket() { return socket; }
    private static void setSocket(Socket socket) { socket = socket; }
    private static String getIpAddress() { return "localhost"; }
    private static int getPort() { return 3895; }
}
