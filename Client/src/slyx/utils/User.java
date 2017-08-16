package slyx.utils;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Antoine Janvier
 * on 31/07/17.
 */
public class User {
    private int id;
    private String firstname;
    private String lastname;
    private int age;
    private String email;
    private String password;
    private boolean isConnected;
    private String picture;

    private HashMap<Integer, Message> messages = new HashMap<>();
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
//    public User(int id, String firstname, String lastname, int age, String email, String password) {
//        this.id = id;
//        this.firstname = firstname;
//        this.lastname = lastname;
//        this.age = age;
//        this.email = email;
//        this.password = password;
//        this.setConnected(false);
//    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isConnected() { return isConnected; }
    public void setConnected(boolean connected) { isConnected = connected; }
    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }

    public HashMap<Integer, Message> getMessages() {
        return messages;
    }
    public void addMessage(int messageID, User from, User to, String content, Date sent) {
        Message m = new Message(messageID, from, to, sent, content);
        messages.put(messageID, m);
    }
    public void addCall(int callID, User from, User to) {
        Call c = new Call(from, to);
        calls.put(callID, c);
    }

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
                '}';
    }
}
