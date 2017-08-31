package slyx.communication;

import slyx.jsonsimple.JSONObject;
import slyx.utils.Message;
import slyx.utils.User;

import java.util.Date;

/**
 * Created by Antoine Janvier
 * on 16/08/17.
 */
class SocketSender {
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
                .put("request", RequestTypes.MESSAGE_REQUEST)
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
    static String SocketSender_sendGetNewMessagesOfContactRequest(int myID, int contactID, int idOfLastMessage) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.GET_NEW_MESSAGES_OF_CONTACT_REQUEST);
        j.put("me", myID);
        j.put("contact", contactID);
        j.put("idOfLastMessage", idOfLastMessage);
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
    static String SocketSender_sendUpdateMySettings(int myID, boolean sounds, int volume, boolean notifications,
                                                    boolean calls, boolean messages, boolean connections) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.UPDATE_SETTINGS_REQUEST);
        j.put("me", myID);
        j.put("sounds", String.valueOf(sounds));
        j.put("volume", volume);
        j.put("notifications", String.valueOf(notifications));
        j.put("calls", String.valueOf(calls));
        j.put("messages", String.valueOf(messages));
        j.put("connections", String.valueOf(connections));
        return j.toString();
    }
    static String SocketSender_sendDisconnectionEvent(int myID) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.DISCONNECTION_REQUEST);
        j.put("me", myID);
        return j.toString();
    }
    static String SocketSender_sendRemoveContactOfContactList(int myID, int userID) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.REMOVE_CONTACT_REQUEST);
        j.put("me", myID);
        j.put("userToRemove", userID);
        return j.toString();
    }
    static String SocketSender_sendCallContactRequest(int from, int to) {
        JSONObject j = new JSONObject();
        j.put("request", RequestTypes.CALL_REQUEST);
        j.put("from", from);
        j.put("to", to);
        return j.toString();
    }
}
