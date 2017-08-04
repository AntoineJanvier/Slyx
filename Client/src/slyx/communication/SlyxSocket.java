package slyx.communication;

import slyx.jsonsimple.JSONObject;
import slyx.jsonsimple.parser.JSONParser;
import slyx.jsonsimple.parser.ParseException;
import slyx.utils.*;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;

import static slyx.utils.Gender.FEMALE;
import static slyx.utils.Gender.MALE;

/**
 * Created by Antoine Janvier
 * on 31/07/17.
 */
public class SlyxSocket extends Thread {
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    public JSONObject jsonObject;

    private HashMap<Integer, User> contacts;

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

    public void sendMessage(String content, User from, User to) {
        Date d = new Date();
        Message m = new Message(from, to, content);
        printWriter.write(m.toObject().toString());
    }

    public User[] sendGetContactRequest(User u) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.CONNECTION_REQUEST);
        j.put("userid", u.getId());

        String returned = echo(j.toString());
        /*
        TODO : Parsing of the returned string to get a list of contacts for the connected User
         */

        /*
        TODO : Remove this part (Tests)
         */
        User[] contacts = new User[2];
        contacts[0] = new User(1, "Antoine", "Janvier", 21, "antoine.jan95@gmail.com");
        contacts[1] = new User(2, "Titi", "Tata", 20, "titi@tata.com");
        return contacts;
    }

    public void sendConnectionRequest(String email, String password) throws IOException {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.CONNECTION_REQUEST);
        j.put("email", email);
        j.put("password", password);

        System.out.println("A");
        String returned = echo(j.toString());
        System.out.println("RETURN => " + returned);

        System.out.println("B");
        JSONParser jsonParser = new JSONParser();
        Object o = null;

        System.out.println("C");
        try {
            o = jsonParser.parse(returned);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("D");
        JSONObject jsonMe = (JSONObject) o;

        System.out.println("E");
        if (jsonMe != null) {
            System.out.println("F");
            Me me = Me.getInstance();
            me.setNULL();
            System.out.println("G");
            me = new Me(
                    Math.toIntExact((long) jsonMe.get("id")),
                    jsonMe.get("firstname").toString(),
                    jsonMe.get("lastname").toString(),
                    Math.toIntExact((long) jsonMe.get("age")),
                    jsonMe.get("email").toString()
            );
            System.out.println("H");
            me.setConnected(true);
        }

    }


    public String echo(String message) {
        try {
//            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
//            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            printWriter.println(message);
            String r = bufferedReader.readLine();
            System.out.println("==========");
            System.out.println(r);
            System.out.println("==========");
            return r;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void run() {
        super.run();

//        while (!this.isInterrupted()) {
//            try {
//                String s = read();
//                if (s != null)
//                    System.out.println(s);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                jsonObject = new JSONObject(bufferedReader.readLine());
//                if (jsonObject.has("type")) {
//                    switch (jsonObject.get("type").toString()) {
//                        case "USER":
//                            User new_user = new User(
//                                    jsonObject.getInt("id"),
//                                    jsonObject.getString("firstname"),
//                                    jsonObject.getString("lastname"),
//                                    jsonObject.getInt("age"),
//                                    jsonObject.getString("email"),
//                                    Gender.MALE
//                            );
//                            contacts.put(contacts.size() + 1, new_user);
//                            break;
//                        case "CALL":
//                            Call new_call = new Call(
//                                    new User(
//                                            jsonObject.getInt("id"),
//                                            jsonObject.getString("firstname"),
//                                            jsonObject.getString("lastname"),
//                                            jsonObject.getInt("age"),
//                                            jsonObject.getString("email"),
//                                            Gender.MALE
//                                    ),
//                                    new User(
//                                            jsonObject.getInt("id"),
//                                            jsonObject.getString("firstname"),
//                                            jsonObject.getString("lastname"),
//                                            jsonObject.getInt("age"),
//                                            jsonObject.getString("email"),
//                                            Gender.MALE
//                                    )
//                            );
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
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

    public Socket getSocket() {
        return this.socket;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }

    public String getIpAddress() {
        return "127.0.0.1";
    }

    public int getPort() {
        return 3895;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public void setPrintWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }
}
