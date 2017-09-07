package slyx.communication;

/*
 * Created by Antoine Janvier
 * on 04/08/17.
 */

import slyx.utils.SlyxAnnotation;

import static slyx.utils.SlyxAnnotation.Type.COMMUNICATION;

/**
 * This class serves as a formatter of request that will be transmitted in the socket link to the server
 */

@SlyxAnnotation(todo = "Add more requests", type = COMMUNICATION)
class RequestTypes {
    // Login
    final static String CONNECTION_REQUEST = "CONNECTION";
    final static String DISCONNECTION_REQUEST = "DISCONNECTION";
    final static String GET_UPDATE_REQUEST = "GET_UPDATE";

    // Basic instructions
    final static String GET_SETTINGS_REQUEST = "GET_SETTINGS";
    final static String UPDATE_SETTINGS_REQUEST = "UPDATE_SETTINGS";
    final static String CALL_REQUEST = "CALL";
    final static String MESSAGE_REQUEST = "SEND_MESSAGE";

    // Contacts
    final static String GET_CONTACTS_REQUEST = "GET_CONTACTS";
    final static String GET_USERS_NOT_IN_CONTACT_LIST_REQUEST = "GET_USERS_NOT_IN_CONTACT_LIST";
    final static String GET_PENDING_CONTACT_REQUESTS_REQUEST = "GET_PENDING_CONTACT_REQUESTS";
    final static String ADD_CONTACT_REQUEST = "ADD_CONTACT";
    final static String REMOVE_CONTACT_REQUEST = "REMOVE_CONTACT";
    final static String ACCEPT_CONTACT_REQUEST = "ACCEPT_CONTACT";
    final static String REJECT_CONTACT_REQUEST = "REJECT_CONTACT";

    // On contact
    final static String GET_MESSAGES_OF_CONTACT_REQUEST = "GET_MESSAGES_OF_CONTACT";
    final static String GET_NEW_MESSAGES_OF_CONTACT_REQUEST = "GET_NEW_MESSAGES_OF_CONTACT";
    final static String GET_CALLS_OF_CONTACT_REQUEST = "GET_CALLS_OF_CONTACT";
}
