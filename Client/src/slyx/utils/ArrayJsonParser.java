package slyx.utils;

import slyx.jsonsimple.JSONObject;
import slyx.jsonsimple.parser.JSONParser;
import slyx.jsonsimple.parser.ParseException;

import java.util.HashMap;

/**
 * Created by Antoine Janvier
 * on 08/08/17.
 */
public class ArrayJsonParser {
    String base;
    int nbUsers;
    HashMap<Integer, User> json;

    public ArrayJsonParser(String base) {
        this.base = base;
    }

    public User[] getUsers() {
        User[] users = new User[nbUsers];
        for (int i = 0; i < users.length; i++) {
            users[i] = json.get(i);
        }
        return users;
    }

    public void process() {

        int key = 0;

        if (base.charAt(0) == '[' && base.charAt(base.length() - 1) == ']') {
            String s1 = base.split("\\[")[1];
            String s2 = s1.split("]")[0];
            System.out.println(s2);

            String[] toParse = s2.split("}");
            for (int i = 0; i < toParse.length; i++) {
                if (i == 0) {
                    JSONParser jsonParser = new JSONParser();
                    Object o = null;

                    try {
                        o = jsonParser.parse(toParse[i] + "}");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    JSONObject jsonMe = (JSONObject) o;
                    if (jsonMe != null) {
                        User u = new User(
                                Math.toIntExact((long) jsonMe.get("id")),
                                jsonMe.get("firstname").toString(),
                                jsonMe.get("lastname").toString(),
                                Math.toIntExact((long) jsonMe.get("age")),
                                jsonMe.get("email").toString()
                        );
                        System.out.println(u.toString());
                        json.put(++key, u);
                    }
                } else {
                    JSONParser jsonParser = new JSONParser();
                    Object o = null;

                    try {
                        o = jsonParser.parse(toParse[i].substring(1) + "}");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    JSONObject jsonMe = (JSONObject) o;
                    if (jsonMe != null) {
                        json.put(++key, new User(
                                Math.toIntExact((long) jsonMe.get("id")),
                                jsonMe.get("firstname").toString(),
                                jsonMe.get("lastname").toString(),
                                Math.toIntExact((long) jsonMe.get("age")),
                                jsonMe.get("email").toString()
                        ));
                    }
                }
                nbUsers = key;
            }
        }
    }
}
