package slyx.communication;

/**
 * Created by Antoine Janvier
 * on 04/08/17.
 */
public class RequestTypes {
    // Login
    public final static String CONNECTION_REQUEST = "CONNECTION";
    public final static String GET_UPDATE_REQUEST = "GET_UPDATE";

    // Basic instructions
    public final static String GET_SETTINGS_REQUEST = "GET_SETTINGS";
    public final static String UPDATE_SETTINGS_REQUEST = "UPDATE_SETTINGS";
    public final static String CALL_REQUEST = "CALL";
    public final static String MESSAGE_REQUEST = "MESSAGE";

    // Contacts
    public final static String GET_CONTACTS_REQUEST = "GET_CONTACTS";
    public final static String GET_USERS_NOT_IN_CONTACT_LIST_REQUEST = "GET_USERS_NOT_IN_CONTACT_LIST";
    public final static String GET_PENDING_CONTACT_REQUESTS_REQUEST = "GET_PENDING_CONTACT_REQUESTS";
    public final static String ADD_CONTACT_REQUEST = "ADD_CONTACT";
    public final static String REMOVE_CONTACT_REQUEST = "REMOVE_CONTACT";
    public final static String ACCEPT_CONTACT_REQUEST = "ACCEPT_CONTACT";
    public final static String REJECT_CONTACT_REQUEST = "REJECT_CONTACT";

    // On contact
    public final static String GET_MESSAGES_OF_CONTACT_REQUEST = "GET_MESSAGES_OF_CONTACT";
    public final static String GET_CALLS_OF_CONTACT_REQUEST = "GET_CALLS_OF_CONTACT";
}
