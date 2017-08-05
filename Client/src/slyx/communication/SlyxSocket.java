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
        JSONParser jsonParser = new JSONParser();
        Object o = null;

        try {
            o = jsonParser.parse(returned);
        } catch (ParseException e) {
            System.out.println("JSON PARSE EXCEPTION IN SEND CONNECTION REQUEST");
            e.printStackTrace();
        }
        JSONObject jsonMe = (JSONObject) o;

        if (jsonMe != null) {
            System.out.println(jsonMe);
        }

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

        String returned = echo(j.toString());

        JSONParser jsonParser = new JSONParser();
        Object o = null;

        try {
            o = jsonParser.parse(returned);
        } catch (ParseException e) {
            System.out.println("JSON PARSE EXCEPTION IN SEND CONNECTION REQUEST");
            e.printStackTrace();
        }
        JSONObject jsonMe = (JSONObject) o;

        if (jsonMe != null) {
            Me me = Me.getInstance();
            me.setNULL();
            me = new Me(
                    Math.toIntExact((long) jsonMe.get("id")),
                    jsonMe.get("firstname").toString(),
                    jsonMe.get("lastname").toString(),
                    Math.toIntExact((long) jsonMe.get("age")),
                    jsonMe.get("email").toString()
            );
            me.setConnected(true);
        }
    }

    public String sendGetUpdateRequest() throws IOException {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.GET_UPDATE_REQUEST);

        String returned = echo(j.toString());

        JSONParser jsonParser = new JSONParser();
        Object o = null;

        try {
            o = jsonParser.parse(returned);
        } catch (ParseException e) {
            System.out.println("JSON PARSE EXCEPTION IN SEND GET UPDATE REQUEST");
            e.printStackTrace();
        }
        JSONObject jsonUpdate = (JSONObject) o;

        if (jsonUpdate != null) {
            return jsonUpdate.get("version").toString();
        }
        return "0.0.0";
    }


    private String echo(String message) {
        try {
            printWriter.println(message);
            return bufferedReader.readLine();
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
