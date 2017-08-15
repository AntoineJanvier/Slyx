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
    private static HashMap<Integer, User> otherUsers;
    private static String version = null;

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

    public void run() {
        while (!this.isInterrupted()) {
            try {
                while (true) {
                    String serverResponse = listenInSocket();
                    JSONParser jsonParser = new JSONParser();
                    Object o = null;
                    try {
                        o = jsonParser.parse(serverResponse);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONObject j = (JSONObject) o;
                        if (j != null && j.containsKey("ACTION")) {
                            switch (j.get("ACTION").toString()) {
                                case "GET_VERSION_OF_SLYX":
                                    System.out.println("GET_VERSION_OF_SLYX");
                                    version = j.get("version").toString();
                                    break;
                                case "MESSAGE_INCOMING":
                                    System.out.println("MESSAGE_INCOMING");
                                    User uFrom = contacts.get(Integer.parseInt(j.get("FROM").toString()));
                                    Date d = new Date();
                                    contacts.get(uFrom.getId()).addMessage(
                                            Integer.parseInt(j.get("MESSAGE_ID").toString()),
                                            uFrom,
                                            me,
                                            j.get("CONTENT").toString(),
                                            d
                                    );
                                    break;
                                case "CALL_INCOMING":
                                    System.out.println("CALL_INCOMING");
                                    User caller = contacts.get(Integer.parseInt(j.get("FROM").toString()));
                                    contacts.get(caller.getId()).addCall(
                                            Integer.parseInt(j.get("CALL_ID").toString()),
                                            caller,
                                            me
                                    );
                                    break;
                                case "ACCEPT_CONNECTION":
                                    me = new User(
                                            Math.toIntExact((long) j.get("id")),
                                            j.get("firstname").toString(),
                                            j.get("lastname").toString(),
                                            Math.toIntExact((long) j.get("age")),
                                            j.get("email").toString(),
                                            j.get("picture").toString()
                                    );
                                    me.setConnected(true);
                                case "GET_CONTACTS":
                                    ArrayJsonParser arrayJsonParser = new ArrayJsonParser(serverResponse);
                                    arrayJsonParser.processUser();
                                    User[] users = arrayJsonParser.getUsers();
                                    for (User u : users) {
                                        contacts.put(u.getId(), u);
                                    }
                                default:
                                    System.out.println("Unknown ACTION...");
                            }
                        } else if (j != null && j.containsKey("ERROR")) {
                            System.out.println(j.get("ERROR").toString());
                            socket.close();
                            System.exit(0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // OK
    public void sendMessage(String content, User to) {
        Date d = new Date();
        Message m = new Message(me, to, d, content);
        printWriter.println(m.toObject().put("request", "SEND_MESSAGE").toString());
    }

    // OK
    public User[] sendGetContactsRequest(User user) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.GET_CONTACTS_REQUEST);
        j.put("userid", user.getId());

        String returned = echo(j.toString());
        JSONParser jsonParser = new JSONParser();
        Object o = null;

        ArrayJsonParser arrayJsonParser = new ArrayJsonParser(returned);
        arrayJsonParser.processUser();
        return arrayJsonParser.getUsers();
    }

    // OK
    public void sendAddContactRequest(int userID) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.ADD_CONTACT_REQUEST);
        j.put("me", SlyxSocket.getMe().getId());
        j.put("userid", userID);

        writeInSocket(j.toString());
        /*String returned = echo(j.toString());
        JSONParser jsonParser = new JSONParser();
        Object o = null;

        try {
            o = jsonParser.parse(returned);
            JSONObject jsonMe = (JSONObject) o;
            if (!"OK".equals(jsonMe.get("request"))) {
                System.out.println("CONTACT NOT ADDED !");
            } else {
                System.out.println("CONTACT ADDED");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
    }

    // OK
    public void sendRejectContactRequest(int userID) {
        System.out.println("sendRejectContactRequest");
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.REJECT_CONTACT_REQUEST);
        j.put("u1userid", me.getId());
        j.put("u2userid", userID);

        // echo(j.toString());
        writeInSocket(j.toString());
    }

    // OK
    public void sendAcceptContactRequest(int userID) {
        System.out.println("sendAcceptContactRequest");
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.ACCEPT_CONTACT_REQUEST);
        j.put("u1userid", me.getId());
        j.put("u2userid", userID);

        // echo(j.toString());
        writeInSocket(j.toString());
    }

    // OK
    public User[] sendGetUsersNotInContactList(User user) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.GET_USERS_NOT_IN_CONTACT_LIST_REQUEST);
        j.put("userid", user.getId());

        writeInSocket(j.toString());
        /*String returned = echo(j.toString());
        JSONParser jsonParser = new JSONParser();
        Object o = null;

        ArrayJsonParser arrayJsonParser = new ArrayJsonParser(returned);
        arrayJsonParser.processUser();
        return arrayJsonParser.getUsers();*/
    }

    public User[] sendGetPendingContactRequests(User user) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.GET_PENDING_CONTACT_REQUESTS_REQUEST);
        j.put("userid", user.getId());

        String returned = echo(j.toString());
        JSONParser jsonParser = new JSONParser();
        Object o = null;

        ArrayJsonParser arrayJsonParser = new ArrayJsonParser(returned);
        arrayJsonParser.processUser();
        return arrayJsonParser.getUsers();
    }

    public Message[] sendGetMessagesOfContactRequest(User user, User to) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.GET_MESSAGES_OF_CONTACT_REQUEST);
        j.put("u1userid", user.getId());
        j.put("u2userid", to.getId());

        String returned = echo(j.toString());
        JSONParser jsonParser = new JSONParser();
        Object o = null;

        ArrayJsonParser arrayJsonParser = new ArrayJsonParser(returned);
        arrayJsonParser.processMessage();
        return arrayJsonParser.getMessages();
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
                        jsonMe.get("email").toString(),
                        jsonMe.get("picture").toString()
                );
                me.setConnected(true);
            }
        }
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

    public void sendAskVersion() {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.GET_UPDATE_REQUEST);
        writeInSocket(j.toString());
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

    private void writeInSocket(String message) {
        System.out.println("\nSending to server : " + message);
        printWriter.println(message);
    }

    private String listenInSocket() {
        try {
            String s = bufferedReader.readLine();
            System.out.println("Server emits : " + s);
            return s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() throws IOException {
        socket.close();
        instance = null;
    }
    private String getIpAddress() {
        return "127.0.0.1";
    }
    private int getPort() {
        return 3895;
    }

    public static User getMe() { return me; }
    public static void setMe(User me) { SlyxSocket.me = me; }
    public static HashMap<Integer, User> getContacts() { return contacts; }
    public static void setContacts(HashMap<Integer, User> contacts) { SlyxSocket.contacts = contacts; }
    public static String getVersion() { return version != null ? version : "0.0.0"; }
}
