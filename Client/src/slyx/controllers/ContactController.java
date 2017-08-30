package slyx.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import slyx.communication.SlyxSocket;
import slyx.utils.Message;
import slyx.utils.User;

import java.io.IOException;


/**
 * Created by Antoine Janvier
 * on 31/07/17.
 */
public class ContactController {
    @FXML
    AnchorPane anchorPane_contact;
    @FXML
    Label label_firstname;
    @FXML
    Label label_lastname;
    @FXML
    ImageView imageView_contact_icon;
    @FXML
    Circle circle_notifications;
    @FXML
    Rectangle rect_connected;

    void setContact(User user, VBox vBox, TextField textField, Button button, AnchorPane anchorPane,
                    ScrollPane scrollPane) {

        label_firstname.setText(user.getFirstname());
        label_lastname.setText(user.getLastname());
        imageView_contact_icon.setImage(new Image(user.getPicture()));

        if (user.isConnected())
            rect_connected.setStyle("-fx-fill: green");
        else
            rect_connected.setStyle("-fx-fill: red");


        try {
            SlyxSocket slyxSocket = SlyxSocket.getInstance();

            if (slyxSocket.listOfContactWhoHasNewMessages.containsKey(user.getId()))
                circle_notifications.setVisible(true);
            else
                circle_notifications.setVisible(false);


            anchorPane_contact.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    slyxSocket.idOfCurrentContactPrinted = user.getId();
                    slyxSocket.sendGetMessagesOfContactRequest(slyxSocket.getMe(), user);

                    textField.setDisable(false);
                    button.setDisable(false);

                    if (circle_notifications.isVisible()) {
                        circle_notifications.setVisible(false);
                        slyxSocket.listOfContactWhoHasNewMessages.remove(user.getId());
                        slyxSocket.needToRefreshContacts = true;
                    }

                    try {

                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/slyx/scenes/contactProfile.fxml"));
                        Parent parent = fxmlLoader.load();
                        ContactProfileController contactProfileController = fxmlLoader.getController();
                        contactProfileController.setContact(user, vBox, textField, button);

                        // Add in scene
                        anchorPane.getChildren().clear();
                        anchorPane.getChildren().add(parent);

                        button.setOnMouseClicked(event1 -> {
                            slyxSocket.sendMessage(
                                    textField.getText(),
                                    slyxSocket.getHashmapContacts().get(user.getId())
                            );
                            textField.setText("");
                        });
                        textField.setOnKeyPressed(event12 -> {
                            if (event12.getCode().equals(KeyCode.ENTER)) {
                                slyxSocket.sendMessage(
                                        textField.getText(),
                                        slyxSocket.getHashmapContacts().get(user.getId())
                                );
                                textField.setText("");
                            }
                        });

                        Message[] messages = slyxSocket.getMessagesOfContact(user);
                        slyxSocket.clearVBox(vBox);
                        for (Message message : messages) {
                            putInVBoxMessages(message, vBox, scrollPane);
                        }
                        Timeline timeline = new Timeline(new KeyFrame(
                                Duration.millis(500),
                                ae -> {
                                    try {
                                        if (!user.hashMapNewMessages.isEmpty()) {
                                            Message[] messagesOfContact = user.getNewMessages();
                                            for (Message m : messagesOfContact) {
                                                if (slyxSocket.idOfCurrentContactPrinted == user.getId())
                                                    putInVBoxMessages(m, vBox, scrollPane);
                                                user.removeNewMessage(m);
                                            }
                                            scrollPane.setVvalue(1);
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
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void initialize() {
        // CSS
        anchorPane_contact
                .getStylesheets().add(getClass().getResource("/slyx/css/contact.css").toExternalForm());
    }

    /**
     * Add a specific message to the message list (VBox)
     * @param message : Message to print
     * @throws IOException : On FXMLLoader.load(...) call
     */
    private void putInVBoxMessages(Message message, VBox vBox, ScrollPane scrollPane) throws IOException {
        FXMLLoader fxmlLoader;
        Parent parent;
        if ("IN".equals(message.getInOrOut())) {
            fxmlLoader = new FXMLLoader(getClass().getResource("/slyx/scenes/message_in.fxml"));
            parent = fxmlLoader.load();
            MessageInController messageInController = fxmlLoader.getController();
            messageInController.setMessage(message);
        } else {
            fxmlLoader = new FXMLLoader(getClass().getResource("/slyx/scenes/message_out.fxml"));
            parent = fxmlLoader.load();
            MessageOutController messageOutController = fxmlLoader.getController();
            messageOutController.setMessage(message);
        }

        vBox.getChildren().add(parent);
        scrollPane.setVvalue(1);
    }
}
