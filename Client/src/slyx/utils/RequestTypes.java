package slyx.utils;

/**
 * Created by Antoine Janvier
 * on 04/08/17.
 */
public class RequestTypes {
    // Login
    public final static String CONNECTION_REQUEST = "CONNECTION";
    public final static String GET_UPDATE_REQUEST = "GET_UPDATE";

    // Basic instructions
    public final static String CALL_REQUEST = "CALL";
    public final static String MESSAGE_REQUEST = "MESSAGE";

    // Contacts
    public final static String GET_CONTACTS_REQUEST = "GET_CONTACTS";
    public final static String GET_USERS_NOT_IN_CONTACT_LIST_REQUEST = "GET_USERS_NOT_IN_CONTACT_LIST";
    public final static String ADD_CONTACT_REQUEST = "ADD_CONTACT";

    // On contact
    public final static String GET_MESSAGES_OF_CONTACT_REQUEST = "GET_MESSAGES_OF_CONTACT";
    public final static String GET_CALLS_OF_CONTACT_REQUEST = "GET_CALLS_OF_CONTACT";
}
