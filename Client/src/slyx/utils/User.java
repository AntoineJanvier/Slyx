package slyx.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by Antoine Janvier
 * on 31/07/17.
 */
public class User {
    // To all contacts
    private int id;
    private String firstname;
    private String lastname;
    private int age;
    private String email;
    private String password;
    private boolean isConnected;
    private String picture;

    // To "Me" instance
    private boolean setting_sounds;
    private int setting_volume;
    private boolean setting_notifications;
    private boolean setting_calls;
    private boolean setting_messages;
    private boolean setting_connections;
    public boolean hasNewMessages = false;

    public TreeMap<Date, Message> messages = new TreeMap<>();
    private HashMap<Integer, Call> calls = new HashMap<>();

    public User() {
    }
    public User(int id, String firstname, String lastname, int age, String email, String picture) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.email = email;
        this.setConnected(false);
        this.picture = picture;
    }

    public void addMessage(int messageID, User from, int to, String content, Date sent, String inOrOut) {
        Message m = new Message(messageID, from, to, sent, content, inOrOut);
        messages.put(sent, m);
    }
    public void addCall(int callID, User from, User to) {
        Call c = new Call(from, to);
        calls.put(callID, c);
    }

    public int getId() { return id; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public String getEmail() { return email; }
    public boolean isConnected() { return isConnected; }
    public String getPicture() { return picture; }

    public void setId(int id) { this.id = id; }
    public void setConnected(boolean connected) { isConnected = connected; }

    // Settings
    public boolean isSetting_sounds() { return setting_sounds; }
    public void setSetting_sounds(boolean setting_sounds) { this.setting_sounds = setting_sounds; }
    public int getSetting_volume() { return setting_volume; }
    public void setSetting_volume(int setting_volume) { this.setting_volume = setting_volume; }
    public boolean isSetting_notifications() { return setting_notifications; }
    public void setSetting_notifications(boolean setting_notifications) { this.setting_notifications = setting_notifications; }
    public boolean isSetting_calls() { return setting_calls; }
    public void setSetting_calls(boolean setting_calls) { this.setting_calls = setting_calls; }
    public boolean isSetting_messages() { return setting_messages; }
    public void setSetting_messages(boolean setting_messages) { this.setting_messages = setting_messages; }
    public boolean isSetting_connections() { return setting_connections; }
    public void setSetting_connections(boolean setting_connections) { this.setting_connections = setting_connections; }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isConnected=" + isConnected +
                ", picture=" + picture +
                ", nbOfMessages=" + messages.size() +
                '}';
    }
}
