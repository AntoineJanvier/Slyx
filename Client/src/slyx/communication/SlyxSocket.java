package slyx.communication;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import slyx.controllers.MessageOutController;
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

    private User me;

    public HashMap<Integer, Integer> listOfContactWhoHasNewMessages = new HashMap<>();
    public HashMap<Integer, User> contacts = new HashMap<>();
    private static HashMap<Integer, User> otherUsers = new HashMap<>();
    private static HashMap<Integer, User> userRequests = new HashMap<>();

    private int idx = -1;

    private static String version = null;

    private static SlyxSocket instance = null;

    public int idOfCurrentContactPrinted = 0;

    public int messagesPrinted = 0;
    public int contactsPrinted = 0;

    public boolean hasNewPendingRequest = false;
    public boolean needToRefreshContacts = false;
    public boolean needToClearCurrent = false;
    public boolean needToEmptyVBoxMessages = false;
    public boolean contactChange = false;

    public int refreshNumber = 0;

    /**
     * Private constructor, called only if the instance of this object is null
     */
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

    /**
     * Instance getter of SlyxSocket, if instance is null, create a new one
     * @return An instance of SlyxSocket (socket connected to server)
     * @throws IOException In case of connection is not possible
     */
    public static synchronized SlyxSocket getInstance() throws IOException {
        if (instance == null)
            instance = new SlyxSocket();
        return instance;
    }

    /**
     * The threaded comportment of the threaded socket
     */
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
                                contacts.clear();
                                for (User u : contacts.values()) {
                                    System.out.println(u.toString());
                                }
                                break;
                            case "GET_CONTACTS":
                                arrayJsonParser = new ArrayJsonParser(j.get("CONTACTS").toString());
                                arrayJsonParser.processUser();
                                contacts.clear();
                                for (User u : arrayJsonParser.userHashMap.values())
                                    contacts.put(u.getId(), u);
                                needToRefreshContacts = true;
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
                                if (pendingRequests.length > 0)
                                    hasNewPendingRequest = true;
                                break;
                            case "CALL_INCOMING":
                                System.out.println("CALL_INCOMING");
                                User caller = contacts.get(Integer.parseInt(j.get("FROM").toString()));
                                contacts.get(caller.getId()).addCall(
                                        Integer.parseInt(j.get("CALL_ID").toString()), caller, this.me
                                );
                                break;
                            case "MESSAGE_INCOMING":
                                User uFrom = contacts.get(Integer.parseInt(j.get("FROM").toString()));
                                Date d = new Date();
                                contacts.get(uFrom.getId()).addMessage(
                                        Integer.parseInt(j.get("MESSAGE_ID").toString()), uFrom, this.me.getId(),
                                        j.get("CONTENT").toString(), d, "IN"
                                );
                                if (uFrom.getId() != idOfCurrentContactPrinted) {
                                    SlyxSound.playSound("NOTIFICATION");
                                    needToRefreshContacts = true;
                                    listOfContactWhoHasNewMessages.put(uFrom.getId(), uFrom.getId());
                                } else {
                                    refreshNumber = 3;
                                }
                                break;
                            case "GET_MESSAGES_OF_CONTACT":
                                arrayJsonParser = new ArrayJsonParser(j.get("MESSAGES").toString());
                                arrayJsonParser.processMessage();
                                Message[] messages = arrayJsonParser.getMessages();
                                if (messages != null && messages.length > 0) {
                                    contacts.get(Integer.parseInt(j.get("CONTACT_ID").toString())).messages.clear();
                                    for (Message m : messages) {
                                        contacts.get(Integer.parseInt(j.get("CONTACT_ID").toString())).addMessage(
                                                m.getId(), m.getFrom(), m.getTo(), m.getContent(), m.getSent(),
                                                m.getInOrOut()
                                        );
                                    }
                                    needToEmptyVBoxMessages = true;
                                }
                                refreshNumber = 3;
                                break;
                            case "GET_NEW_MESSAGES_OF_CONTACT":
                                arrayJsonParser = new ArrayJsonParser(j.get("MESSAGES").toString());
                                arrayJsonParser.processMessage();
                                Message[] messages1 = arrayJsonParser.getMessages();
                                if (messages1 != null && messages1.length > 0) {
                                    for (Message m : messages1) {
                                        contacts.get(Integer.parseInt(j.get("CONTACT_ID").toString())).addMessage(
                                                m.getId(), m.getFrom(), m.getTo(), m.getContent(), m.getSent(),
                                                m.getInOrOut()
                                        );
                                    }
                                }
                                refreshNumber = 3;
                                break;
                            case "CONTACT_REQUEST_ACCEPTED":
                                User user = new User(
                                        Math.toIntExact((long) j.get("id")), j.get("firstname").toString(),
                                        j.get("lastname").toString(), Math.toIntExact((long) j.get("age")),
                                        j.get("email").toString(), j.get("picture").toString()
                                );
                                user.setConnected(Boolean.valueOf(j.get("connected").toString()));
                                addNewContact(user);
                                needToRefreshContacts = true;
                                break;
                            case "CONTACT_CONNECTION":
                                User toConnect = contacts.get(Math.toIntExact((long) j.get("CONTACT_ID")));
                                toConnect.setConnected(true);
                                needToRefreshContacts = true;
                                break;
                            case "CONTACT_DISCONNECTION":
                                User toDisconnect = contacts.get(Math.toIntExact((long) j.get("CONTACT_ID")));
                                toDisconnect.setConnected(false);
                                needToRefreshContacts = true;
                                break;
                            case "CONTACT_REMOVE":
                                needToRefreshContacts = true;
                                if (Integer.parseInt(j.get("USER_A").toString()) == idOfCurrentContactPrinted
                                        || Integer.parseInt(j.get("USER_B").toString()) == idOfCurrentContactPrinted) {
                                    needToClearCurrent = true;
                                }
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

    /**
     * Send a message to a specific user
     * @param content Message content
     * @param to Recipient
     */
    public void sendMessage(String content, User to, VBox vBox) {
        if (content.length() > 0) {
            writeInSocket(SocketSender_sendMessage(this.me, to.getId(), content, "OUT"));
            contacts.get(to.getId()).addMessage(
                    idx--, this.me, to.getId(), content, new Date(), "OUT"
            );
        }
        refreshNumber = 3;
    }

    public void resetMessagePrinted(int exceptThisUser, VBox vBox) {
        for (User u : contacts.values())
            if (u.getId() != exceptThisUser)
                for (Message m : u.messages.values())
                    m.printed = false;
        messagesPrinted = 0;
        vBox.getChildren().clear();
        for (Node observable : vBox.getChildren()) {
            vBox.getChildren().remove(observable);
        }
    }

    /**
     * Send a get contact request to server, it will answer later
     */
    public void sendGetContactsRequest() {
        writeInSocket(SocketSender_sendGetContactsRequest(this.me.getId()));
    }

    /**
     * Send an add contact request to server, it will answer later
     * @param userID User to add as a new contact
     */
    public void sendAddContactRequest(int userID) {
        writeInSocket(SocketSender_sendAddContactRequest(this.me.getId(), userID));
    }

    /**
     * Send a reject contact request to server, it will answer later
     * @param userID User to reject from requests
     */
    public void sendRejectContactRequest(int userID) {
        writeInSocket(SocketSender_sendRejectContactRequest(this.me.getId(), userID));
    }

    /**
     * Send an accept contact request to server, it will answer later
     * @param userID User to accept as new contact
     */
    public void sendAcceptContactRequest(int userID) {
        writeInSocket(SocketSender_sendAcceptContactRequest(this.me.getId(), userID));
    }

    /**
     * Send a get user request to server, it will answer later
     * @param user Me
     */
    public void sendGetUsersNotInContactList(User user) {
        writeInSocket(SocketSender_sendGetUsersNotInContactList(user.getId()));
    }

    /**
     * Send a get pending contact request to server, it will answer later
     */
    public void sendGetPendingContactRequests() { writeInSocket(SocketSender_sendGetPendingContactRequests(this.me.getId())); }

    /**
     * Send a get message of contact request to server, it will answer later
     * @param contact We want messages of this contact
     */
    public void sendGetMessagesOfContactRequest(User contact) {
        writeInSocket(SocketSender_sendGetMessagesOfContactRequest(this.me.getId(), contact.getId()));
    }

    /**
     * Send a get message of contact request to server, it will answer later
     * @param contact We want messages of this contact
     */
    public void sendGetNewMessagesOfContactRequest(User contact, int last) {
        writeInSocket(SocketSender_sendGetNewMessagesOfContactRequest(this.me.getId(), contact.getId(), last));
    }

    /**
     * Send an ask version request to server, it will answer later
     */
    public void sendAskVersion() {
        writeInSocket(SocketSender_sendAskVersion());
    }

    /**
     * Send an ask connection request to server, it will answer later
     * @param email Email of user
     * @param password Password of user
     */
    public void sendAskConnection(String email, String password) {
        writeInSocket(SocketSender_sendAskConnection(email, password));
    }

    /**
     * Send an update settings request to server, it will answer later
     * @param sounds CheckBox to mute/unmute general sounds
     * @param volume Slider (0 to 100 percent) for general sounds
     * @param notifications CheckBox to have sounds in case of notification
     * @param calls CheckBox to have sounds in case of incoming call
     * @param messages CheckBox to have sounds in case of incoming message
     * @param connections CheckBox to have sounds in case of contact connections
     */
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

    /**
     * Send a remove contact request to server, it will answer later
     * @param userID User to remove of contact list
     */
    public void sendRemoveContactOfContactList(int userID) {
        writeInSocket(SocketSender_sendRemoveContactOfContactList(this.me.getId(), userID));
    }

    /**
     * Send a call contact request to server, it will answer later
     * @param from User who want to launch a call
     * @param to User who will be called
     */
    public void sendCallContactRequest(int from, int to) {
        writeInSocket(SocketSender_sendCallContactRequest(from, to));
    }

    /**
     * Send an message to the server by the printWriter
     * @param message Message content
     */
    private void writeInSocket(String message) {
         System.out.println("\nSending : " + message);
        printWriter.println(message);
    }

    /**
     * Listen incoming messages of the server by the bufferedReader
     * @return The message sent by the server
     * @throws IOException In case of the reader has a problem (closed for example)
     */
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

    /**
     * Send a disconnection request to the server
     */
    public void sendDisconnectionEvent() {
        writeInSocket(SocketSender_sendDisconnectionEvent(this.me.getId()));
    }

    /**
     * Close all processes in this instance and set the instance and the connected user to null
     * @throws IOException In case of problem while closing or interrupting socket
     */
    public void close() throws IOException {
        this.interrupt();
        socket.close();
        bufferedReader.close();
        printWriter.close();
        instance = new SlyxSocket();
        this.me = null;

        contacts.clear();
        listOfContactWhoHasNewMessages.clear();
        userRequests.clear();
        otherUsers.clear();
        needToClearCurrent = true;
        needToEmptyVBoxMessages = true;
    }

    /**
     * Get the IP address of the socket server
     * @return IP address of the socket server
     */
    private String getIpAddress() {
        return "127.0.0.1";
    }

    /**
     * Get the port of the socket server
     * @return The port of the socket server
     */
    private int getPort() {
        return 3895;
    }

    /**
     * Get the connected user
     * @return The connected user
     */
    public User getMe() { return this.me; }

    /**
     * Set the connected user
     * @param me User object to set as connected
     */
    public void setMe(User me) { this.me = me; }

    /**
     * Get the version of the application
     * @return The version of the application or 0.0.0 in case of the server hasn't told it to the app
     */
    public static String getVersion() { return version != null ? version : "0.0.0"; }

    /**
     * Get contacts
     * @return The hashMap of contacts
     */
    public HashMap<Integer, User> getHashmapContacts() { return contacts; }

    /**
     * Add a contact to contact list
     * @param u User to add in contact list
     */
    private void addNewContact(User u) {
        contacts.put(u.getId(), u);
    }

    /**
     * Remove a user of contact requests list
     * @param user User to remove of contact requests
     */
    public void removeContactRequest(User user) {
        if (userRequests.containsKey(user.getId())) {
            userRequests.remove(user.getId());
        }
    }

    /**
     * Get an array of contacts who are not in contact list
     * @return The hashmap of users as an array
     */
    public User[] getOtherUsers() {
        User[] users = new User[otherUsers.size()];
        int counter = 0;
        for (User u : otherUsers.values())
            users[counter++] = u;
        return users;
    }

    /**
     * Get an array of contact requests
     * @return The hashmap of requests
     */
    public User[] getUserRequests() {
        User[] users = new User[userRequests.size()];
        int counter = 0;
        for (User u : userRequests.values())
            users[counter++] = u;
        return users;
    }

    /**
     * Get messages of a specific contact
     * @param contact We want to get messages of this contact
     * @return An array of messages
     */
    public Message[] getMessagesOfContact(User contact) {
        Message[] messages = new Message[contacts.get(contact.getId()).messages.size()];
        int counter = 0;
        for (Message m : contacts.get(contact.getId()).messages.values())
            messages[counter++] = m;
        return messages;
    }

    public void clearVBox(VBox vBox) {
        vBox.getChildren().clear();
        for (Node observable : vBox.getChildren()) {
            vBox.getChildren().remove(observable);
        }
    }

}
