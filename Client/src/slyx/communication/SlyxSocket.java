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
     * @param to: User who will receive the message
     */
    public void sendMessage(String content, User to) {
        Date d = new Date();
        Message m = new Message(to, new Date(), content);
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
        arrayJsonParser.processUser();
        return arrayJsonParser.getUsers();
    }

    public void sendAddContactRequest(int userID) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.ADD_CONTACT_REQUEST);
        j.put("me", SlyxSocket.getMe().getId());
        j.put("userid", userID);

        String returned = echo(j.toString());
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
        }
    }

    public void sendRejectContactRequest(int userID) {
        System.out.println("sendRejectContactRequest");
    }
    public void sendAcceptContactRequest(int userID) {
        System.out.println("sendAcceptContactRequest");
    }

    public User[] sendGetUsersNotInContactList(User user) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.GET_USERS_NOT_IN_CONTACT_LIST_REQUEST);
        j.put("userid", user.getId());

        String returned = echo(j.toString());
        JSONParser jsonParser = new JSONParser();
        Object o = null;

        ArrayJsonParser arrayJsonParser = new ArrayJsonParser(returned);
        arrayJsonParser.processUser();
        return arrayJsonParser.getUsers();
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
                        jsonMe.get("email").toString()
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
}
