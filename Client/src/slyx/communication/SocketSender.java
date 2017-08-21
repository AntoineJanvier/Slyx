package slyx.communication;

import slyx.jsonsimple.JSONObject;
import slyx.utils.Message;
import slyx.utils.RequestTypes;
import slyx.utils.User;

import java.util.Date;

/**
 * Created by Antoine Janvier
 * on 16/08/17.
 */
class SocketSender {
    /*
     * TODO : Put function which are sending infos to server here
     */
    static String SocketSender_sendGetContactsRequest(int userID) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.GET_CONTACTS_REQUEST);
        j.put("userid", userID);
        return j.toString();
    }
    static String SocketSender_sendAddContactRequest(int myID, int userID) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.ADD_CONTACT_REQUEST);
        j.put("me", myID);
        j.put("userid", userID);
        return j.toString();
    }
    static String SocketSender_sendRejectContactRequest(int myID, int userID) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.REJECT_CONTACT_REQUEST);
        j.put("u1userid", myID);
        j.put("u2userid", userID);
        return j.toString();
    }
    static String SocketSender_sendAcceptContactRequest(int myID, int userID) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.ACCEPT_CONTACT_REQUEST);
        j.put("u1userid", myID);
        j.put("u2userid", userID);
        return j.toString();
    }
    static String SocketSender_sendMessage(User me, int toUserID, String content, String ior) {
        return new Message(0, me, toUserID, new Date(), content, ior)
                .toObject()
                .put("request", "SEND_MESSAGE")
                .toString();
    }
    static String SocketSender_sendGetUsersNotInContactList(int myID) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.GET_USERS_NOT_IN_CONTACT_LIST_REQUEST);
        j.put("userid", myID);
        return j.toString();
    }
    static String SocketSender_sendGetPendingContactRequests(int myID) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.GET_PENDING_CONTACT_REQUESTS_REQUEST);
        j.put("userid", myID);
        return j.toString();
    }
    static String SocketSender_sendGetMessagesOfContactRequest(int myID, int contactID) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.GET_MESSAGES_OF_CONTACT_REQUEST);
        j.put("u1userid", myID);
        j.put("u2userid", contactID);
        return j.toString();
    }
    static String SocketSender_sendAskVersion() {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.GET_UPDATE_REQUEST);
        return j.toString();
    }
    static String SocketSender_sendAskConnection(String email, String password) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.CONNECTION_REQUEST);
        j.put("email", email);
        j.put("password", password);
        return j.toString();
    }
}
