package slyx.utils;

import java.util.HashMap;

import static slyx.utils.Gender.MALE;

/**
 * Created by Antoine Janvier
 * on 01/08/17.
 */
public class Jison {
    private String message;

    public Jison(String message) {
        this.message = message;
    }

    public Object getObject() {
        String key = getKey(this.message, 0);
        switch (key) {
            case "err":
                return null;
            case "User":
                User u = new User("Ahah", "Hihi", 12, "string", MALE);
                u.setConnected(true);
                return u;
            case "Call":
                Call c = new Call(new User(), new User());
                return c;
            case "Message":
                Message m = new Message(new User(), new User(), "BONJOUR MONSIEUR");
                return m;
            default:
                return null;
        }
    }

    public int getNbOf(String k) {
        int cmp = 0;
        for (int i = 0; i < this.message.length(); i++) {
            String key = getKey(this.message, i);
            if (k.equals(key)) {
                cmp++;
                if ("User".equals(key)) {

                }
            }
        }
        return cmp;
    }

//    private getUserInfos

//    public HashMap<String, History> getHistory() {
//        for (int i = 0; i < this.message.length(); i++) {
//            if ()
//        }
//    }
    private String getKey(String s, int start) {
        int cmp = 0;
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (cmp == 2)
                break;
            char c = s.charAt(i);
            if (c == '"')
                cmp++;
            if (cmp == 1) {
                res.append(c);
            }
        }
        return res.toString();
    }
//
//    private Message getMessage(String s) {
//
//    }
//    private Call getCall() {
//
//    }
}
