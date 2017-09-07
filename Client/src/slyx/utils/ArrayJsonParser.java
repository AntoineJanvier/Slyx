package slyx.utils;

import slyx.communication.SlyxSocket;
import slyx.jsonsimple.JSONObject;
import slyx.jsonsimple.parser.JSONParser;
import slyx.jsonsimple.parser.ParseException;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static slyx.utils.SlyxAnnotation.Type.CLASS;

/**
 * Created by Antoine Janvier
 * on 08/08/17.
 */

@SlyxAnnotation(todo = "Upgrade processes", type = CLASS)
public class ArrayJsonParser {
    private String base;
    private int nbKeys;
    public HashMap<Integer, User> userHashMap = new HashMap<>();
    private HashMap<Integer, Message> messageHashMap = new HashMap<>();

    public ArrayJsonParser(String base) {
        this.base = base;
    }

    public User[] getUsers() {
        User[] values = new User[userHashMap.size()];
        int index = 0;
        for (Map.Entry<Integer, User> mapEntry : userHashMap.entrySet()) {
            values[index] = mapEntry.getValue();
            index++;
        }
        return values;
    }
    public Message[] getMessages() {
        Message[] values = new Message[messageHashMap.size()];
        int index = 0;
        for (Map.Entry<Integer, Message> mapEntry : messageHashMap.entrySet()) {
            values[index] = mapEntry.getValue();
            index++;
        }
        return values;
    }

    private Object tryParse(int i, JSONParser jsonParser, String[] toParse) {
        Object o = null;
        if (i == 0) {
            try {
                o = jsonParser.parse(toParse[i] + "}");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                // Need to remove the comma at the beginning of the String
                o = jsonParser.parse(toParse[i].substring(1) + "}");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return o;
    }

    public void processUser() {
        base = '[' + base.split("\\[")[1];
        base = base.split("]")[0] + ']';
        int key = 0;
        if (base.charAt(0) == '[' && base.charAt(base.length() - 1) == ']' && base.length() > 2) {
            // Remove '[' and ']'
            String s1 = base.split("\\[")[1];

            if (s1.length() > 1) {
                String s2 = s1.split("]")[0];

                // Split to have all objects (need to re-add brackets at the end to parse correctly objects)
                String[] toParse = s2.split("}");

                for (int i = 0; i < toParse.length; i++) {
                    JSONParser jsonParser = new JSONParser();
                    Object o;

                    o = tryParse(i, jsonParser, toParse);

                    // Create a User and add it to HashMap
                    JSONObject jsonMe = (JSONObject) o;
                    if (jsonMe != null) {
                        User u = new User(
                                Math.toIntExact((long) jsonMe.get("id")),
                                jsonMe.get("firstname").toString(),
                                jsonMe.get("lastname").toString(),
                                Math.toIntExact((long) jsonMe.get("age")),
                                jsonMe.get("email").toString(),
                                jsonMe.get("picture").toString()
                        );
                        u.setConnected(Boolean.valueOf(jsonMe.get("connected").toString()));
                        userHashMap.put(++key, u);
                    }
                    nbKeys = key;
                }
            } else {
                System.out.println("Empty JSON detected");
            }
        }
    }
    public void processMessage() {
        int key = 0;
        if (base.charAt(0) == '[' && base.charAt(base.length() - 1) == ']' && base.length() > 2) {
            // Remove '[' and ']'
            String s1 = base.split("\\[")[1];
            String s2 = s1.split("]")[0];

            // Split to have all objects (need to re-add brackets at the end to parse correctly objects)
            String[] toParse = s2.split("}");

            for (int i = 0; i < toParse.length; i++) {
                JSONParser jsonParser = new JSONParser();
                Object o;

                o = tryParse(i, jsonParser, toParse);

                // Create a User and add it to HashMap
                JSONObject jsonMe = (JSONObject) o;
                if (jsonMe != null) {
                    Date dateSent = new Date();
                    dateSent.setTime(Long.parseLong(jsonMe.get("sent").toString()));
                    try {
                        SlyxSocket slyxSocket = SlyxSocket.getInstance();
                        Message u = new Message(
                                Math.toIntExact((long) jsonMe.get("id")),
                                slyxSocket.getMe(),
                                Math.toIntExact((long) jsonMe.get("contact")),
                                dateSent,
                                jsonMe.get("content").toString(),
                                jsonMe.get("inOrOut").toString()
                        );
                        messageHashMap.put(++key, u);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                nbKeys = key;
            }
        }
    }
}
