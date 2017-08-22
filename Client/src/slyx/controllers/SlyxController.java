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
import javafx.scene.input.KeyEvent;
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
    @FXML
    BorderPane borderPane_general;
    @FXML
    AnchorPane anchorPane_right;
    @FXML
    HBox hBox_top;
    @FXML
    VBox vBox_left;
    @FXML
    Button btn_send_message;
    @FXML
    TextField tf_message_to_send;
    @FXML
    Button btn_disconnection;
    @FXML
    ImageView imageView_my_icon;
    @FXML
    Label label_my_firstname;
    @FXML
    Label label_my_lastname;
    @FXML
    Label label_my_email;
    @FXML
    VBox vBox_request;
    @FXML
    VBox vBox_messages;
    @FXML
    ScrollPane scrollPane_messages;
    @FXML
    Tab tabPaneTab_requests;
    @FXML
    TabPane tabPane_left;

    public void launchAddNewContactWindow() throws IOException {
        // Launch Settings window
        Parent next_root = FXMLLoader.load(getClass().getResource("/slyx/scenes/addContact.fxml"));
        Stage next_stage = new Stage();
        next_stage.setTitle("Add Contact");
        next_stage.setScene(new Scene(next_root));
        next_stage.show();
    }

    public void launchSettingsWindow() throws IOException {
        // Launch Settings window
        Parent next_root = FXMLLoader.load(getClass().getResource("/slyx/scenes/settings.fxml"));
        Stage next_stage = new Stage();
        next_stage.setTitle("Settings");
        next_stage.setScene(new Scene(next_root));
        next_stage.show();
    }

    public void disconnect() throws IOException {
        SlyxSocket slyxSocket = SlyxSocket.getInstance();
        slyxSocket.close();

        Parent next_root = FXMLLoader.load(getClass().getResource("/slyx/scenes/login.fxml"));
        Stage stage = (Stage) btn_disconnection.getScene().getWindow();
        stage.close();
        Stage next_stage = new Stage();
        next_stage.setTitle("Slyx");
        next_stage.setScene(new Scene(next_root));
        next_stage.show();
    }

    public void initialize() throws IOException {
        SlyxSound.playSound("LOGIN");

        SlyxSocket slyxSocket = SlyxSocket.getInstance();

        // Set my informations
        User me = slyxSocket.getMe();
        //slyxSocket.sendGetMySettings();
        label_my_firstname.setText(me.getFirstname());
        label_my_lastname.setText(me.getLastname());
        label_my_email.setText(me.getEmail());
        imageView_my_icon.setImage(new Image(me.getPicture()));

        refresh(slyxSocket);
    }

    private void refresh(SlyxSocket slyxSocket) throws IOException {
        slyxSocket.sendGetContactsRequest(slyxSocket.getMe());
        slyxSocket.sendGetPendingContactRequests(slyxSocket.getMe());
        slyxSocket.sendGetUsersNotInContactList(slyxSocket.getMe());

        refreshContacts();
        refreshContactRequests(slyxSocket);
    }

    private void refreshContactsInContactList(User u) throws IOException {
        SlyxSocket slyxSocket = SlyxSocket.getInstance();
        Parent p = FXMLLoader.load(getClass().getResource("/slyx/scenes/contact.fxml"));
        ((Label) p.lookup("#label_firstname")).setText(u.getFirstname());
        ((Label) p.lookup("#label_lastname")).setText(u.getLastname());
        ((ImageView) p.lookup("#imageView_contact_icon")).setImage(new Image(u.getPicture()));

        p.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                slyxSocket.sendGetMessagesOfContactRequest(slyxSocket.getMe(), u);
                try {
                    Parent p = FXMLLoader.load(getClass().getResource("/slyx/scenes/contactProfile.fxml"));

                    // Set elements
                    ((Label) p.lookup("#label_firstname")).setText(u.getFirstname());
                    ((Label) p.lookup("#label_lastname")).setText(u.getLastname());
                    ((Label) p.lookup("#label_email")).setText(u.getEmail());
                    ((ImageView) p.lookup("#imageView_contact_icon")).setImage(new Image(u.getPicture()));

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
                    scrollPane_messages.setVvalue(scrollPane_messages.getVmax());

                    Timeline timeline = new Timeline(new KeyFrame(
                            Duration.millis(500),
                            ae -> {
                                try {
                                    if (!u.hashMapNewMessages.isEmpty()) {
                                        Message[] messagesOfContact = u.getNewMessages();
                                        for (Message m : messagesOfContact) {
                                            putInVBoxMessages(m);
                                            u.removeNewMessage(m.getId());
                                        }
                                        scrollPane_messages.setVvalue(scrollPane_messages.getVmax());
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
        vBox_left.getChildren().add(p);
    }

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
                        if (!slyxSocket.newContacts.isEmpty()) {
                            User[] users = slyxSocket.getNewContacts();
                            for (User u : users) {
                                refreshContactsInContactList(u);
                                slyxSocket.removeNewContact(u.getId());
                            }
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void refreshContactRequests(SlyxSocket slyxSocket) throws IOException {
        // Set the contact request in PENDING state in the request area
        slyxSocket.sendGetPendingContactRequests(slyxSocket.getMe());
        User[] requests = slyxSocket.getUserRequests();
        vBox_request.getChildren().clear();
        for (Node observable : vBox_request.getChildren()) {
            vBox_request.getChildren().remove(observable);
        }
        for (User u : requests) {
            Parent p = FXMLLoader.load(getClass().getResource("/slyx/scenes/contactRequest.fxml"));
            ((Label) p.lookup("#label_name")).setText(u.getFirstname() + " " + u.getLastname());

            Button button_Reject = (Button) p.lookup("#button_reject_request");
            button_Reject.setOnAction(event -> {
                slyxSocket.sendRejectContactRequest(u.getId());
                p.setDisable(true);
                button_Reject.setDisable(true);
            });
            Button button_Accept = (Button) p.lookup("#button_add_accept_request");
            button_Accept.setOnAction(event -> {
                slyxSocket.sendAcceptContactRequest(u.getId());
                p.setDisable(true);
                button_Accept.setDisable(true);
            });
            vBox_request.getChildren().add(p);
        }
    }
    private void putInVBoxMessages(Message m) throws IOException {
        Parent np;
        if ("IN".equals(m.getInOrOut())) {
            np = FXMLLoader.load(getClass().getResource("/slyx/scenes/message_in.fxml"));
            ((Label) np.lookup("#label_content")).setText(m.getContent());
            ((Label) np.lookup("#label_date")).setText("Sent at : " + m.getSent().toString());
            vBox_messages.getChildren().add(np);
        } else {
            np = FXMLLoader.load(getClass().getResource("/slyx/scenes/message_out.fxml"));
            ((Label) np.lookup("#label_content")).setText(m.getContent());
            ((Label) np.lookup("#label_date")).setText("Sent at : " + m.getSent().toString());
            vBox_messages.getChildren().add(np);
        }
    }
}
