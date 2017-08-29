package slyx.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import slyx.communication.SlyxSocket;
import slyx.utils.Message;
import slyx.utils.SlyxSound;
import slyx.utils.User;

import java.io.IOException;

/**
 * Created by Antoine Janvier
 * on 30/07/17.
 */
public class SlyxController {
    // Containers
    @FXML
    AnchorPane anchorPane_top;
    @FXML
    AnchorPane anchorPane_right;
    @FXML
    BorderPane borderPane_general;
    @FXML
    HBox hBox_top;
    @FXML
    ScrollPane scrollPane_messages;
    @FXML
    Tab tabPaneTab_requests;
    @FXML
    TabPane tabPane_left;
    @FXML
    VBox vBox_left;
    @FXML
    VBox vBox_messages;
    @FXML
    VBox vBox_request;

    // Buttons
    @FXML
    Button btn_send_message;
    @FXML
    Button btn_disconnection;
    @FXML
    Button button_addNewContact;

    // Utils
    @FXML
    ImageView imageView_my_icon;
    @FXML
    TextField tf_message_to_send;

    // Labels
    @FXML
    Label label_my_firstname;
    @FXML
    Label label_my_lastname;
    @FXML
    Label label_my_email;


    /**
     * Launch the window where user can add a new contact
     * @throws IOException : When FXMLLoader.load(...) fail
     */
    public void launchAddNewContactWindow() throws IOException {
        // Launch Settings window
        Parent next_root = FXMLLoader.load(getClass().getResource("/slyx/scenes/addContact.fxml"));
        next_root.getStylesheets().add(getClass().getResource("/slyx/css/addContact.css").toExternalForm());
        Stage next_stage = new Stage();
        next_stage.setTitle("Add Contact");
        next_stage.setScene(new Scene(next_root));
        next_stage.show();
    }

    /**
     * Launch the window where user can change its settings (notifications, volume sound, sounds, ...)
     * @throws IOException : When FXMLLoader.load(...) fail
     */
    public void launchSettingsWindow() throws IOException {
        // Launch Settings window
        Parent next_root = FXMLLoader.load(getClass().getResource("/slyx/scenes/settings.fxml"));
        next_root.getStylesheets().add(getClass().getResource("/slyx/css/settings.css").toExternalForm());
        Stage next_stage = new Stage();
        next_stage.setTitle("Settings");
        next_stage.setScene(new Scene(next_root));
        next_stage.show();
    }

    /**
     * Close the link between client and server
     * @throws IOException : When getting the instance of the SlyxSocket (singleton)
     */
    public void disconnect() throws IOException {
        SlyxSocket slyxSocket = SlyxSocket.getInstance();
        slyxSocket.sendDisconnectionEvent();
        slyxSocket.setMe(null);

        Parent next_root = FXMLLoader.load(getClass().getResource("/slyx/scenes/login.fxml"));
        next_root.getStylesheets().add(getClass().getResource("/slyx/css/login.css").toExternalForm());
        Stage stage = (Stage) btn_disconnection.getScene().getWindow();
        stage.close();
        Stage next_stage = new Stage();
        next_stage.setTitle("Slyx");
        next_stage.setScene(new Scene(next_root));
        next_stage.show();
    }

    /**
     * Initialize all components of the Slyx app : Contacts, user requests, events etc...
     * @throws IOException : When getting the instance of the SlyxSocket (singleton)
     */
    public void initialize() throws IOException {
        SlyxSocket slyxSocket = SlyxSocket.getInstance();

        SlyxSound.playSound("LOGIN");

        button_addNewContact.setOnMouseClicked(event -> {
            try {
                launchAddNewContactWindow();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Set my informations
        User me = slyxSocket.getMe();

        Parent p = FXMLLoader.load(getClass().getResource("/slyx/scenes/myProfile.fxml"));
        p.getStylesheets().add(getClass().getResource("/slyx/css/myProfile.css").toExternalForm());
        ((ImageView) p.lookup("#imageView_myImage")).setImage(new Image(me.getPicture()));
        ((Label) p.lookup("#label_myName")).setText(me.getFirstname() + " " + me.getLastname());
        ((Label) p.lookup("#label_myEmail")).setText(me.getEmail());
        anchorPane_top.getChildren().add(p);

        tf_message_to_send.setDisable(true);
        btn_send_message.setDisable(true);

        refresh(slyxSocket);
    }

    /**
     * Call the refresh functions in the Slyx app window
     * @param slyxSocket : Instance of SlyxSocket (singleton)
     * @throws IOException : When refreshContacts() or refreshContactRequests() calls
     */
    private void refresh(SlyxSocket slyxSocket) throws IOException {
        slyxSocket.sendGetContactsRequest();
        slyxSocket.sendGetPendingContactRequests();
        slyxSocket.sendGetUsersNotInContactList(slyxSocket.getMe());

        refreshContacts();
        refreshContactRequests();

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(1000),
                ae -> {
                    if (slyxSocket.hasNewPendingRequest) {
                        vBox_request.getChildren().clear();
                        for (Node observable : vBox_request.getChildren()) {
                            vBox_request.getChildren().remove(observable);
                        }
                        slyxSocket.needToRefreshContacts = false;
                    }
                }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Load the view of a contact and add it to contact list with specific user informations
     * @param u : The user to print in contact list
     * @throws IOException : On FXMLLoader.load(...) call
     */
    private void refreshContactsInContactList(User u) throws IOException {
        SlyxSocket slyxSocket = SlyxSocket.getInstance();
        Parent parent = FXMLLoader.load(getClass().getResource("/slyx/scenes/contact.fxml"));
        parent.getStylesheets().add(getClass().getResource("/slyx/css/contact.css").toExternalForm());
        if (u.isConnected()) {
            parent.lookup("#rect_connected").setStyle(
                    "-fx-fill: green"
            );
        } else {
            parent.lookup("#rect_connected").setStyle(
                    "-fx-fill: red"
            );
        }
        ((Label) parent.lookup("#label_firstname")).setText(u.getFirstname());
        ((Label) parent.lookup("#label_lastname")).setText(u.getLastname());
        ((ImageView) parent.lookup("#imageView_contact_icon")).setImage(new Image(u.getPicture()));

        if (slyxSocket.listOfContactWhoHasNewMessages.containsKey(u.getId())) {
            parent.lookup("#circle_notifications").setVisible(true);
        } else {
            parent.lookup("#circle_notifications").setVisible(false);
        }

        parent.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                slyxSocket.idOfCurrentContactPrinted = u.getId();
                slyxSocket.sendGetMessagesOfContactRequest(slyxSocket.getMe(), u);

                if (parent.lookup("#circle_notifications").isVisible()) {
                    parent.lookup("#circle_notifications").setVisible(false);
                    slyxSocket.listOfContactWhoHasNewMessages.remove(u.getId());
                    slyxSocket.needToRefreshContacts = true;
                }

                if (btn_send_message.isDisable())
                    btn_send_message.setDisable(false);
                if (tf_message_to_send.isDisable())
                    tf_message_to_send.setDisable(false);

                try {
                    Parent p = FXMLLoader.load(getClass().getResource("/slyx/scenes/contactProfile.fxml"));
                    p.getStylesheets().add(getClass().getResource("/slyx/css/contactProfile.css").toExternalForm());

                    // Set elements
                    ((Label) p.lookup("#label_firstname")).setText(u.getFirstname());
                    ((Label) p.lookup("#label_lastname")).setText(u.getLastname());
                    ((Label) p.lookup("#label_email")).setText(u.getEmail());
                    ((ImageView) p.lookup("#imageView_contact_icon")).setImage(new Image(u.getPicture()));

                    if (!u.isConnected()) {
                        p.lookup("#button_call_contact").setDisable(true);
                    } else {
                        p.lookup("#button_call_contact").setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                slyxSocket.sendCallContactRequest(slyxSocket.getMe().getId(), u.getId());
                                try {
                                    Parent next_root = FXMLLoader.load(getClass().getResource("/slyx/scenes/callWindow.fxml"));
                                    next_root.getStylesheets().add(getClass().getResource("/slyx/css/callWindow.css").toExternalForm());
                                    ((Label) next_root.lookup("#label_from")).setText(slyxSocket.getMe().getId() + "");
                                    ((Label) next_root.lookup("#label_to")).setText(u.getId() + "");
                                    Stage next_stage = new Stage();
                                    next_stage.setTitle("Calling " + u.getFirstname() + "...");
                                    next_stage.setScene(new Scene(next_root));
                                    next_stage.show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }

                    p.lookup("#button_remove_contact").setOnMouseClicked(event13 -> {
                        slyxSocket.sendRemoveContactOfContactList(u.getId());
                        anchorPane_right.getChildren().clear();
                        vBox_messages.getChildren().clear();
                        for (Node observable : vBox_messages.getChildren()) {
                            vBox_messages.getChildren().remove(observable);
                        }
                        tf_message_to_send.setDisable(true);
                        btn_send_message.setDisable(true);
                    });

                    // Add in scene
                    anchorPane_right.getChildren().clear();
                    anchorPane_right.getChildren().add(p);

                    btn_send_message.setOnMouseClicked(event1 -> {
                        slyxSocket.sendMessage(
                                tf_message_to_send.getText(),
                                slyxSocket.getHashmapContacts().get(u.getId())
                        );
                        tf_message_to_send.setText("");
                    });
                    tf_message_to_send.setOnKeyPressed(event12 -> {
                        if (event12.getCode().equals(KeyCode.ENTER)) {
                            slyxSocket.sendMessage(
                                    tf_message_to_send.getText(),
                                    slyxSocket.getHashmapContacts().get(u.getId())
                            );
                            tf_message_to_send.setText("");
                        }
                    });

                    vBox_messages.getChildren().clear();
                    for (Node observable : vBox_messages.getChildren()) {
                        vBox_messages.getChildren().remove(observable);
                    }

                    Message[] messages = slyxSocket.getMessagesOfContact(u);
                    vBox_messages.getChildren().clear();
                    for (Node observable : vBox_messages.getChildren()) {
                        vBox_messages.getChildren().remove(observable);
                    }
                    for (Message m : messages) {
                        putInVBoxMessages(m);
                    }
                    Timeline timeline = new Timeline(new KeyFrame(
                            Duration.millis(500),
                            ae -> {
                                try {
                                    boolean toPutAtEnd = false;
                                    if (!u.hashMapNewMessages.isEmpty()) {
                                        toPutAtEnd = true;
                                        Message[] messagesOfContact = u.getNewMessages();
                                        for (Message m : messagesOfContact) {
                                            if (slyxSocket.idOfCurrentContactPrinted == u.getId())
                                                putInVBoxMessages(m);
                                            u.removeNewMessage(m);
                                        }
                                    }
                                    if (toPutAtEnd) {
                                        scrollPane_messages.setVvalue(1);
                                    }

                                } catch (IOException e) {
                                    System.out.println(e.getMessage());
                                }
                            }));
                    timeline.setCycleCount(Animation.INDEFINITE);
                    timeline.play();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        vBox_left.getChildren().add(parent);
    }

    /**
     * Refresh the contact list
     * @throws IOException : When getting the instance of the SlyxSocket (singleton)
     */
    public void refreshContacts() throws IOException {
        SlyxSocket slyxSocket = SlyxSocket.getInstance();

        // Set all contacts in the contact area
        User[] contacts = slyxSocket.getContacts();
        vBox_left.getChildren().clear();
        for (Node observable : vBox_left.getChildren()) {
            vBox_left.getChildren().remove(observable);
        }
        for (User u : contacts) {
            refreshContactsInContactList(u);
        }
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(1000),
                ae -> {
                    try {
                        if (slyxSocket.needToRefreshContacts) {
                            vBox_left.getChildren().clear();
                            for (Node observable : vBox_left.getChildren()) {
                                vBox_left.getChildren().remove(observable);
                            }
                            User[] users = slyxSocket.getContacts();
                            for (User u : users) {
                                refreshContactsInContactList(u);
                            }
                            slyxSocket.needToRefreshContacts = false;
                            slyxSocket.sendGetContactsRequest();
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                    if (slyxSocket.needToClearCurrent) {
                        slyxSocket.needToClearCurrent = false;
                        vBox_messages.getChildren().clear();
                        for (Node observable : vBox_messages.getChildren()) {
                            vBox_messages.getChildren().remove(observable);
                        }
                        anchorPane_right.getChildren().clear();
                    }
                }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Refresh the user requests in the Requests Tab
     * @throws IOException : When getting the instance of the SlyxSocket (singleton)
     */
    private void refreshContactRequests() throws IOException {
        // Set the contact request in PENDING state in the request area
        SlyxSocket slyxSocket = SlyxSocket.getInstance();
        vBox_request.getChildren().clear();
        for (Node observable : vBox_request.getChildren()) {
            vBox_request.getChildren().remove(observable);
        }
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(1000),
                ae -> {
                    try {
                        if (slyxSocket.hasNewPendingRequest) {
                            vBox_request.getChildren().clear();
                            for (Node observable : vBox_request.getChildren()) {
                                vBox_request.getChildren().remove(observable);
                            }
                            User[] users = slyxSocket.getUserRequests();

                            for (User u : users) {

//                                ContactRequestController contactRequestController = new ContactRequestController();
//                                contactRequestController.setWithUser(u);

                                Parent p = FXMLLoader.load(getClass().getResource("/slyx/scenes/contactRequest.fxml"));
                                p.getStylesheets().add(getClass().getResource("/slyx/css/contact.css").toExternalForm());

                                ((Label) p.lookup("#label_name")).setText(u.getFirstname() + " " + u.getLastname());

                                p.lookup("#button_reject_request").setOnMouseClicked(event -> {
                                    slyxSocket.sendRejectContactRequest(u.getId());
                                    slyxSocket.removeContactRequest(u);
                                });
                                p.lookup("#button_add_accept_request").setOnMouseClicked(event -> {
                                    slyxSocket.sendAcceptContactRequest(u.getId());
                                    try {
                                        Thread.sleep(10);
                                    } catch (InterruptedException e) {
                                        System.out.println(e.getMessage());
                                    }
                                    slyxSocket.needToRefreshContacts = true;
                                    slyxSocket.removeContactRequest(u);
                                    slyxSocket.sendGetPendingContactRequests();
                                });
                                vBox_request.getChildren().add(p);
                            }
                            slyxSocket.hasNewPendingRequest = false;
                            slyxSocket.sendGetPendingContactRequests();
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Add a specific message to the message list (VBox)
     * @param m : Message to print
     * @throws IOException : On FXMLLoader.load(...) call
     */
    private void putInVBoxMessages(Message m) throws IOException {
        Parent np;
        String in = getClass().getResource("/slyx/css/messageIn.css").toExternalForm();
        String out = getClass().getResource("/slyx/css/messageOut.css").toExternalForm();
        if ("IN".equals(m.getInOrOut())) {
            np = FXMLLoader.load(getClass().getResource("/slyx/scenes/message_in.fxml"));
            np.getStylesheets().add(in);
            ((Label) np.lookup("#label_content")).setText(m.getContent());
            ((Label) np.lookup("#label_date")).setText("Sent at : " + m.getSent().toString());
            vBox_messages.getChildren().add(np);
        } else {
            np = FXMLLoader.load(getClass().getResource("/slyx/scenes/message_out.fxml"));
            np.getStylesheets().add(out);
            ((Label) np.lookup("#label_content")).setText(m.getContent());
            ((Label) np.lookup("#label_date")).setText("Sent at : " + m.getSent().toString());
            vBox_messages.getChildren().add(np);
        }
        scrollPane_messages.setVvalue(1);
    }
}
