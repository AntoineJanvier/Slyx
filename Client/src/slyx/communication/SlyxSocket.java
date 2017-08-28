package slyx.communication;

import slyx.exceptions.SocketClosedException;
import slyx.jsonsimple.JSONObject;
import slyx.jsonsimple.parser.JSONParser;
import slyx.jsonsimple.parser.ParseException;
import slyx.utils.*;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;

import static slyx.communication.SocketSender.*;

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
    private HashMap<Integer, User> contacts = new HashMap<>();
    public boolean hasNewConnection = false;

    private static HashMap<Integer, User> otherUsers = new HashMap<>();

    private static HashMap<Integer, User> userRequests = new HashMap<>();
    public boolean hasNewPendingRequest = false;

    private static String version = null;

    private static SlyxSocket instance = null;

    private SlyxSocket() {
        try {
            this.socket = new Socket(getIpAddress(), getPort());
        } catch (IOException e) {
            System.out.println("Connection refused");
        }
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.printWriter = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Socket not open");
        }
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
                                contacts.get(uFrom.getId()).addNewMessage(
                                        Integer.parseInt(j.get("MESSAGE_ID").toString()), uFrom, this.me.getId(),
                                        j.get("CONTENT").toString(), d, "IN"
                                );
                                break;
                            case "CALL_INCOMING":
                                System.out.println("CALL_INCOMING");
                                User caller = contacts.get(Integer.parseInt(j.get("FROM").toString()));
                                contacts.get(caller.getId()).addCall(
                                        Integer.parseInt(j.get("CALL_ID").toString()), caller, this.me
                                );
                                break;
                            case "ACCEPT_CONNECTION":
                                this.me = new User(
                                        Math.toIntExact((long) j.get("id")), j.get("firstname").toString(),
                                        j.get("lastname").toString(), Math.toIntExact((long) j.get("age")),
                                        j.get("email").toString(), j.get("picture").toString()
                                );
                                this.me.setConnected(true);
                                if (j.get("sounds") != null) {
                                    this.me.setSetting_sounds(Boolean.valueOf(j.get("sounds").toString()));
                                    this.me.setSetting_volume(Integer.parseInt(j.get("volume").toString()));
                                    this.me.setSetting_notifications(Boolean.valueOf(j.get("notifications").toString()));
                                    this.me.setSetting_calls(Boolean.valueOf(j.get("calls").toString()));
                                    this.me.setSetting_messages(Boolean.valueOf(j.get("messages").toString()));
                                    this.me.setSetting_connections(Boolean.valueOf(j.get("connections").toString()));
                                }
                                break;
                            case "GET_CONTACTS":
                                arrayJsonParser = new ArrayJsonParser(j.get("CONTACTS").toString());
                                arrayJsonParser.processUser();
                                User[] users = arrayJsonParser.getUsers();
                                contacts.clear();
                                for (User u : users)
                                    addNewContact(u);
                                break;
                            case "CONTACT_REQUEST":
                                userRequests.put(Math.toIntExact((long) j.get("id")), new User(
                                        Math.toIntExact((long) j.get("id")), j.get("firstname").toString(),
                                        j.get("lastname").toString(), Math.toIntExact((long) j.get("age")),
                                        j.get("email").toString(), j.get("picture").toString()
                                ));
                                hasNewPendingRequest = true;
                                break;
                            case "GET_USERS_NOT_IN_CONTACT_LIST":
                                arrayJsonParser = new ArrayJsonParser(serverResponse);
                                arrayJsonParser.processUser();
                                User[] usersNotInContactList = arrayJsonParser.getUsers();
                                for (User u : usersNotInContactList)
                                    otherUsers.put(u.getId(), u);
                                break;
                            case "GET_PENDING_CONTACT_REQUEST":
                                arrayJsonParser = new ArrayJsonParser(serverResponse);
                                arrayJsonParser.processUser();
                                User[] pendingRequests = arrayJsonParser.getUsers();
                                for (User u : pendingRequests)
                                    userRequests.put(u.getId(), u);
                                hasNewPendingRequest = true;
                                break;
                            case "GET_MESSAGES_OF_CONTACT":
                                arrayJsonParser = new ArrayJsonParser(j.get("MESSAGES").toString());
                                arrayJsonParser.processMessage();
                                Message[] messages = arrayJsonParser.getMessages();
                                if (messages != null && messages.length > 0) {
                                    for (Message m : messages) {
                                        contacts.get(Integer.parseInt(j.get("CONTACT_ID").toString())).addNewMessage(
                                                m.getId(), m.getFrom(), m.getTo(), m.getContent(), m.getSent(),
                                                m.getInOrOut()
                                        );
                                    }
                                }
                                break;
                            case "CONTACT_REQUEST_ACCEPTED":
                                User user = new User(
                                        Math.toIntExact((long) j.get("id")), j.get("firstname").toString(),
                                        j.get("lastname").toString(), Math.toIntExact((long) j.get("age")),
                                        j.get("email").toString(), j.get("picture").toString()
                                );
                                user.setConnected(Boolean.valueOf(j.get("connected").toString()));
                                addNewContact(user);
                                break;
                            case "CONTACT_CONNECTION":
                                User toConnect = contacts.get(Math.toIntExact((long) j.get("CONTACT_ID")));
                                toConnect.setConnected(true);
                                hasNewConnection = true;
                                break;
                            case "CONTACT_DISCONNECTION":
                                User toDisconnect = contacts.get(Math.toIntExact((long) j.get("CONTACT_ID")));
                                toDisconnect.setConnected(false);
                                hasNewConnection = true;
                                break;
                            case "CONTACT_REMOVE":
                                hasNewConnection = true;
                                break;
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
        if (content.length() > 0) {
            writeInSocket(SocketSender_sendMessage(this.me, to.getId(), content, "OUT"));
            contacts.get(to.getId()).addNewMessage(
                    idx--, this.me, to.getId(), content, new Date(), "OUT"
            );
        }
    }
    public void sendGetContactsRequest(User user) {
        writeInSocket(SocketSender_sendGetContactsRequest(user.getId()));
    }
    public void sendAddContactRequest(int userID) {
        writeInSocket(SocketSender_sendAddContactRequest(this.me.getId(), userID));
    }
    // TODO : Test sendRejectContactRequest
    public void sendRejectContactRequest(int userID) {
        writeInSocket(SocketSender_sendRejectContactRequest(this.me.getId(), userID));
    }
    public void sendAcceptContactRequest(int userID) {
        writeInSocket(SocketSender_sendAcceptContactRequest(this.me.getId(), userID));
    }
    public void sendGetUsersNotInContactList(User user) {
        writeInSocket(SocketSender_sendGetUsersNotInContactList(user.getId()));
    }
    public void sendGetPendingContactRequests(User user) { writeInSocket(SocketSender_sendGetPendingContactRequests(user.getId())); }
    public void sendGetMessagesOfContactRequest(User user, User to) {
        writeInSocket(SocketSender_sendGetMessagesOfContactRequest(user.getId(), to.getId()));
    }
    public void sendAskVersion() {
        writeInSocket(SocketSender_sendAskVersion());
    }
    public void sendAskConnection(String email, String password) {
        writeInSocket(SocketSender_sendAskConnection(email, password));
    }
    public void sendUpdateMySettings(boolean sounds, int volume, boolean notifications,
                                     boolean calls, boolean messages, boolean connections) {
        writeInSocket(
                SocketSender_sendUpdateMySettings(
                        this.me.getId(), sounds, volume, notifications, calls, messages, connections
                )
        );
        this.me.setSetting_sounds(sounds);
        this.me.setSetting_volume(volume);
        this.me.setSetting_notifications(notifications);
        this.me.setSetting_calls(calls);
        this.me.setSetting_messages(messages);
        this.me.setSetting_connections(connections);
    }
    public void sendRemoveContactOfContactList(int userID) {
        writeInSocket(SocketSender_sendRemoveContactOfContactList(this.me.getId(), userID));
    }
    public void sendCallContactRequest(int from, int to) {
        writeInSocket(SocketSender_sendCallContactRequest(from, to));
    }

    private void writeInSocket(String message) {
         System.out.println("\nSending : " + message);
        printWriter.println(message);
    }
    private String listenInSocket() throws IOException {
        try {
            String s = bufferedReader.readLine();
             System.out.println("Answer : " + s);
            return s;
        } catch (IOException e) {
            System.out.println(new SocketClosedException().getMessage());
            this.socket = new Socket(getIpAddress(), getPort());
        }
        return null;
    }
    public void sendDisconnectionEvent() {
        writeInSocket(SocketSender_sendDisconnectionEvent(this.me.getId()));
    }

    public void close() throws IOException {
        this.interrupt();
        socket.close();
        bufferedReader.close();
        printWriter.close();
        instance = new SlyxSocket();
        this.me = null;
    }
    private String getIpAddress() {
        return "127.0.0.1";
    }
    private int getPort() {
        return 3895;
    }
    public User getMe() { return this.me; }
    public void setMe(User me) { this.me = me; }
    public static String getVersion() { return version != null ? version : "0.0.0"; }
    public HashMap<Integer, User> getHashmapContacts() { return contacts; }
    private void addNewContact(User u) {
        contacts.put(u.getId(), u);
    }
    public User[] getContacts() {
        User[] users = new User[contacts.size()];
        int counter = 0;
        for (User u : contacts.values())
            users[counter++] = u;
        return users;
    }
    public User[] getOtherUsers() {
        User[] users = new User[otherUsers.size()];
        int counter = 0;
        for (User u : otherUsers.values())
            users[counter++] = u;
        return users;
    }
    public User[] getUserRequests() {
        User[] users = new User[userRequests.size()];
        int counter = 0;
        for (User u : userRequests.values())
            users[counter++] = u;
        return users;
    }
    public Message[] getMessagesOfContact(User contact) {
        Message[] messages = new Message[contacts.get(contact.getId()).getMessages().size()];
        int counter = 0;
        for (Message m : contacts.get(contact.getId()).getMessages().values())
            messages[counter++] = m;
        return messages;
    }
}
