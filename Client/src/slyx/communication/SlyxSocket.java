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

    private static User me;
    private static HashMap<Integer, User> contacts;

    private static SlyxSocket instance = null;

    private SlyxSocket() throws IOException {
        this.socket = new Socket(getIpAddress(), getPort());
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.printWriter = new PrintWriter(socket.getOutputStream(), true);
        contacts = null;
        this.start();
    }

    public static synchronized SlyxSocket getInstance() throws IOException {
        if (instance == null)
            instance = new SlyxSocket();
        return instance;
    }

    /**
     * Send a message from a User to another User
     * @param content: Message to send
     * @param from: User who is sending the message
     * @param to: User who will receive the message
     */
    public void sendMessage(String content, User from, User to) {
        Date d = new Date();
        Message m = new Message(from, to, content);
        printWriter.write(m.toObject().toString());
    }

    /**
     * Ask a contact list for a defined User
     * @param user: User who asking his contact list
     * @return A list of User who are contacts of the current User
     */
    public User[] sendGetContactsRequest(User user) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.GET_CONTACTS_REQUEST);
        j.put("userid", user.getId());

        String returned = echo(j.toString());
        JSONParser jsonParser = new JSONParser();
        Object o = null;


        ArrayJsonParser arrayJsonParser = new ArrayJsonParser(returned);
//        arrayJsonParser.process();
//        return arrayJsonParser.getUsers();

//        try {
//            o = jsonParser.parse(returned);
//        } catch (ParseException e) {
//            System.out.println("JSON PARSE EXCEPTION IN GET CONTACTS REQUEST");
//            e.printStackTrace();
//        }
//        JSONObject jsonMe = (JSONObject) o;
//
//        if (jsonMe != null) {
//            System.out.println(jsonMe);
//        }

        /*
        TODO : Remove this part (Tests)
         */
        User[] contacts = new User[2];
        contacts[0] = new User(1, "Antoine", "Janvier", 21, "antoine.jan95@gmail.com");
        contacts[1] = new User(2, "Titi", "Tata", 20, "titi@tata.com");
        return contacts;
    }

    /**
     * Request a connection to the server for a User
     * @param email: Email
     * @param password: Password
     * @return User found in database
     */
    public void sendConnectionRequest(String email, String password) {
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
            if ("ACCEPT_CONNECTION".equals(jsonMe.get("request").toString())) {
                me = new User(
                        Math.toIntExact((long) jsonMe.get("id")),
                        jsonMe.get("firstname").toString(),
                        jsonMe.get("lastname").toString(),
                        Math.toIntExact((long) jsonMe.get("age")),
                        jsonMe.get("email").toString()
                );
                me.setConnected(true);
//                return me;
            }
        }
        return;
    }

    /**
     * Ask if there is updates available to check the current version of the application
     * @return Latest version number of the application
     */
    public String sendGetUpdateRequest() {
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

    /**
     * Send a message to the socket and receive the answer from it
     * @param message: Message to send to the server
     * @return Response from the server after have processed the message sent
     */
    private String echo(String message) {
        try {
            System.out.println("\nSending : " + message);
            printWriter.println(message);
            String s = bufferedReader.readLine();
            System.out.println("Receiving : " + s);
            return s;
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
    public void close() throws IOException {
        socket.close();
    }
    private String getIpAddress() {
        return "127.0.0.1";
    }
    private int getPort() {
        return 3895;
    }

    public static User getMe() { return me; }
    public void setMe(User me) { SlyxSocket.me = me; }
    public HashMap<Integer, User> getContacts() { return contacts; }
    public void setContacts(HashMap<Integer, User> contacts) { this.contacts = contacts; }
}
