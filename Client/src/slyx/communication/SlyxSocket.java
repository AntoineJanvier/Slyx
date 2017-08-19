package slyx.communication;

import slyx.controllers.SlyxController;
import slyx.exceptions.SocketClosedException;
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
    private int idx = -1;

    private User me;
    private static HashMap<Integer, User> contacts = new HashMap<>();
    private static HashMap<Integer, User> otherUsers = new HashMap<>();
    private static HashMap<Integer, User> userRequests = new HashMap<>();
    private static HashMap<Integer, Message> messagesOfCurrentContact = new HashMap<>();
    private static String version = null;

    private static SlyxSocket instance = null;

    private SlyxSocket() throws IOException {
        this.socket = new Socket(getIpAddress(), getPort());
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.printWriter = new PrintWriter(socket.getOutputStream(), true);
        this.start();
    }

    public static synchronized SlyxSocket getInstance() throws IOException {
        if (instance == null)
            instance = new SlyxSocket();
        return instance;
    }

    public void run() {
        while (!this.isInterrupted()) {
            String serverResponse = null;
            try {
                serverResponse = listenInSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (serverResponse == null) break;
            JSONParser jsonParser = new JSONParser();
            Object o = null;
            try {
                o = jsonParser.parse(serverResponse);
            } catch (ParseException e) {
                System.out.println(e.getMessage());
            }
            boolean isArray = false;
            if (serverResponse.charAt(0) == '[')
                isArray = true;
            if (!isArray) {
                try {
                    JSONObject j = (JSONObject) o;
                    ArrayJsonParser arrayJsonParser;
                    if (j != null && j.containsKey("ACTION")) {
                        switch (j.get("ACTION").toString()) {
                            case "GET_VERSION_OF_SLYX":
                                version = j.get("version").toString();
                                break;
                            case "MESSAGE_INCOMING":
                                SlyxSound.playSound("NOTIFICATION");
                                User uFrom = contacts.get(Integer.parseInt(j.get("FROM").toString()));
                                Date d = new Date();
                                contacts.get(uFrom.getId()).messages.put(
                                        Integer.parseInt(j.get("MESSAGE_ID").toString()),
                                        new Message(
                                                Integer.parseInt(j.get("MESSAGE_ID").toString()),
                                                uFrom,
                                                this.me.getId(),
                                                d,
                                                j.get("CONTENT").toString(),
                                                "IN"
                                        )
                                );
                                break;
                            case "CALL_INCOMING":
                                System.out.println("CALL_INCOMING");
                                User caller = contacts.get(Integer.parseInt(j.get("FROM").toString()));
                                contacts.get(caller.getId()).addCall(
                                        Integer.parseInt(j.get("CALL_ID").toString()),
                                        caller,
                                        this.me
                                );
                                break;
                            case "ACCEPT_CONNECTION":
                                this.me = new User(
                                        Math.toIntExact((long) j.get("id")),
                                        j.get("firstname").toString(),
                                        j.get("lastname").toString(),
                                        Math.toIntExact((long) j.get("age")),
                                        j.get("email").toString(),
                                        j.get("picture").toString()
                                );
                                this.me.setConnected(true);
                                break;
                            case "GET_CONTACTS":
                                arrayJsonParser = new ArrayJsonParser(j.get("CONTACTS").toString());
                                arrayJsonParser.processUser();
                                User[] users = arrayJsonParser.getUsers();
                                for (User u : users) {
                                    contacts.put(u.getId(), u);
                                }
                                break;
                            case "CONTACT_REQUEST":
                                userRequests.put(Math.toIntExact((long) j.get("id")), new User(
                                        Math.toIntExact((long) j.get("id")),
                                        j.get("firstname").toString(),
                                        j.get("lastname").toString(),
                                        Math.toIntExact((long) j.get("age")),
                                        j.get("email").toString(),
                                        j.get("picture").toString()
                                ));
                                break;
                            case "GET_USERS_NOT_IN_CONTACT_LIST":
                                arrayJsonParser = new ArrayJsonParser(serverResponse);
                                arrayJsonParser.processUser();
                                User[] usersNotInContactList = arrayJsonParser.getUsers();
                                for (User u : usersNotInContactList) {
                                    otherUsers.put(u.getId(), u);
                                }
                                break;
                            case "GET_PENDING_CONTACT_REQUEST":
                                arrayJsonParser = new ArrayJsonParser(serverResponse);
                                arrayJsonParser.processUser();
                                User[] pendingRequests = arrayJsonParser.getUsers();
                                for (User u : pendingRequests) {
                                    userRequests.put(u.getId(), u);
                                }
                                break;
                            case "GET_MESSAGES_OF_CONTACT":
                                arrayJsonParser = new ArrayJsonParser(j.get("MESSAGES").toString());
                                arrayJsonParser.processMessage();
                                Message[] messages = arrayJsonParser.getMessages();
                                messagesOfCurrentContact.clear();
                                if (messages != null && messages.length > 0) {
                                    for (Message m : messages) {
                                        messagesOfCurrentContact.put(m.getId(), m);
                                    }
                                }
                                break;
                            case "CONTACT_REQUEST_ACCEPTED":
                                contacts.put(Math.toIntExact((long) j.get("id")), new User(
                                        Math.toIntExact((long) j.get("id")),
                                        j.get("firstname").toString(),
                                        j.get("lastname").toString(),
                                        Math.toIntExact((long) j.get("age")),
                                        j.get("email").toString(),
                                        j.get("picture").toString()
                                ));
                                break;
                                /*
                                TODO : Add ping answer to ping request to know status of a contact
                                 */
                            default:
                                System.out.println("Unknown ACTION...");
                        }
                    } else if (j != null && j.containsKey("ERROR")) {
                        System.out.println(j.get("ERROR").toString());
                        close();
                        System.exit(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void sendMessage(String content, User to) {
        Message m = new Message(0, this.me, to.getId(), new Date(), content, "OUT");
        writeInSocket(m.toObject().put("request", "SEND_MESSAGE").toString());
        contacts.get(to.getId()).getMessages().put(idx, new Message(
                idx,
                me,
                to.getId(),
                new Date(),
                content,
                "OUT"
        ));
    }
    public void sendGetContactsRequest(User user) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.GET_CONTACTS_REQUEST);
        j.put("userid", user.getId());
        writeInSocket(j.toString());
    }
    public void sendAddContactRequest(int userID) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.ADD_CONTACT_REQUEST);
        j.put("me", this.me.getId());
        j.put("userid", userID);
        writeInSocket(j.toString());
    }
    // TODO : Test sendRejectContactRequest
    public void sendRejectContactRequest(int userID) {
        System.out.println("sendRejectContactRequest");
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.REJECT_CONTACT_REQUEST);
        j.put("u1userid", this.me.getId());
        j.put("u2userid", userID);
        writeInSocket(j.toString());
    }
    // TODO : Test sendAcceptContactRequest
    public void sendAcceptContactRequest(int userID) {
        System.out.println("sendAcceptContactRequest");
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.ACCEPT_CONTACT_REQUEST);
        j.put("u1userid", this.me.getId());
        j.put("u2userid", userID);
        writeInSocket(j.toString());
    }
    public void sendGetUsersNotInContactList(User user) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.GET_USERS_NOT_IN_CONTACT_LIST_REQUEST);
        j.put("userid", user.getId());
        writeInSocket(j.toString());
    }
    public void sendGetPendingContactRequests(User user) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.GET_PENDING_CONTACT_REQUESTS_REQUEST);
        j.put("userid", user.getId());
        writeInSocket(j.toString());
    }
    public void sendGetMessagesOfContactRequest(User user, User to) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.GET_MESSAGES_OF_CONTACT_REQUEST);
        j.put("u1userid", user.getId());
        j.put("u2userid", to.getId());
        writeInSocket(j.toString());
    }
    public void sendAskVersion() {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.GET_UPDATE_REQUEST);
        writeInSocket(j.toString());
    }
    public void sendAskConnection(String email, String password) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.CONNECTION_REQUEST);
        j.put("email", email);
        j.put("password", password);
        writeInSocket(j.toString());
    }
    private void writeInSocket(String message) {
        // System.out.println("\nSending : " + message);
        printWriter.println(message);
    }
    private String listenInSocket() throws IOException {
        try {
            String s = bufferedReader.readLine();
            // System.out.println("Answer : " + s);
            return s;
        } catch (IOException e) {
            System.out.println(new SocketClosedException().getMessage());
            this.socket = new Socket(getIpAddress(), getPort());
        }
        return null;
    }

    public void close() throws IOException {
        this.interrupt();
        socket.close();
        bufferedReader.close();
        printWriter.close();
        instance = new SlyxSocket();
    }
    private String getIpAddress() {
        return "127.0.0.1";
    }
    private int getPort() {
        return 3895;
    }

    public User getMe() { return this.me; }
    public void setMe(User me) { this.me = me; }
    public static void setContacts(HashMap<Integer, User> contacts) { SlyxSocket.contacts = contacts; }
    public static String getVersion() { return version != null ? version : "0.0.0"; }
    public HashMap<Integer, User> getHashmapContacts() { return contacts; }

    public User[] getContacts() {
        User[] users = new User[contacts.size()];
        int counter = 0;
        for (User u : contacts.values()) {
            users[counter++] = u;
        }
        return users;
    }
    public User[] getOtherUsers() {
        User[] users = new User[otherUsers.size()];
        int counter = 0;
        for (User u : otherUsers.values()) {
            users[counter++] = u;
        }
        return users;
    }
    public User[] getUserRequests() {
        User[] users = new User[userRequests.size()];
        int counter = 0;
        for (User u : userRequests.values()) {
            users[counter++] = u;
        }
        return users;
    }
    public Message[] getMessagesOfContact(User contact) {
        Message[] messages = new Message[messagesOfCurrentContact.size()];
        int counter = 0;
        for (Message m : messagesOfCurrentContact.values()) {
            messages[counter++] = m;
        }
        return messages;
    }
}
