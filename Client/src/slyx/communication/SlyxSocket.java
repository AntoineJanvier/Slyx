package slyx.communication;

import slyx.libs.JSONObject;
import slyx.utils.Call;
import slyx.utils.Gender;
import slyx.utils.User;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by Antoine Janvier
 * on 31/07/17.
 */
public class SlyxSocket extends Thread {
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    public JSONObject jsonObject;

    public HashMap<Integer, User> contacts;

    private static SlyxSocket instance = null;

    private SlyxSocket() throws IOException {
        this.socket = new Socket(getIpAddress(), getPort());
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.printWriter = new PrintWriter(socket.getOutputStream(), true);
        contacts = null;
        this.start();
    }

    public static SlyxSocket getInstance() throws IOException {
        if (instance == null)
            instance = new SlyxSocket();
        return instance;
    }

    @Override
    public void run() {
        super.run();

        while(!this.isInterrupted()) {
            try {
                jsonObject = new JSONObject(bufferedReader.readLine());
                if (jsonObject.has("type")) {
                    switch (jsonObject.get("type").toString()) {
                        case "user":
                            User new_user = new User(
                                    jsonObject.getInt("id"),
                                    jsonObject.getString("firstname"),
                                    jsonObject.getString("lastname"),
                                    jsonObject.getInt("age"),
                                    jsonObject.getString("email"),
                                    Gender.MALE
                            );
                            contacts.put(contacts.size() + 1, new_user);
                            break;
                        case "call":
                            Call new_call = new Call(
                                    new User(
                                            jsonObject.getInt("id"),
                                            jsonObject.getString("firstname"),
                                            jsonObject.getString("lastname"),
                                            jsonObject.getInt("age"),
                                            jsonObject.getString("email"),
                                            Gender.MALE
                                    ),
                                    new User(
                                            jsonObject.getInt("id"),
                                            jsonObject.getString("firstname"),
                                            jsonObject.getString("lastname"),
                                            jsonObject.getInt("age"),
                                            jsonObject.getString("email"),
                                            Gender.MALE
                                    )
                            )
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(String message) throws IOException {
        printWriter.print(message);
    }
    public String read() throws IOException {
        return bufferedReader.readLine();
    }
    public void close() throws IOException {
        socket.close();
    }
    public Socket getSocket() { return this.socket; }
    public BufferedReader getBufferedReader() { return bufferedReader; }
    public PrintWriter getPrintWriter() { return printWriter; }
    public String getIpAddress() { return "127.0.0.1"; }
    public int getPort() { return 3895; }
    public void setSocket(Socket socket) { this.socket = socket; }
    public void setBufferedReader(BufferedReader bufferedReader) { this.bufferedReader = bufferedReader; }
    public void setPrintWriter(PrintWriter printWriter) { this.printWriter = printWriter; }
}
