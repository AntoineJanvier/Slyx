package slyx.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
private Timeline timelineRefreshMessages = null;
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

            if (user.hasNewMessages)
                circle_notifications.setVisible(true);
            else
                circle_notifications.setVisible(false);

            anchorPane_contact.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    user.hasNewMessages = false;
                    textField.setDisable(false);
                    button.setDisable(false);
                    slyxSocket.refreshNumberForMessages = 2;
                    if (circle_notifications.isVisible()) {
                        circle_notifications.setVisible(false);
                        slyxSocket.listOfContactWhoHasNewMessages.remove(user.getId());
                    }

                    if (slyxSocket.idOfCurrentContactPrinted != user.getId()) {
                        slyxSocket.contactChange = true;

                        slyxSocket.idOfCurrentContactPrinted = user.getId();
                        slyxSocket.resetMessagePrinted(user.getId(), vBox);
                        slyxSocket.idOfCurrentContactPrinted = user.getId();

                        if (user.messages.size() == 0)
                            slyxSocket.sendGetMessagesOfContactRequest(user);
                        else
                            slyxSocket.sendGetNewMessagesOfContactRequest(slyxSocket.getMe(), user.messages.get(user.messages.lastEntry().getKey()).getId());

                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/slyx/scenes/contactProfile.fxml"));
                            Parent parent = fxmlLoader.load();
                            ContactProfileController contactProfileController = fxmlLoader.getController();
                            contactProfileController.setContact(user, vBox, textField, button);

                            // Add in right panel
                            anchorPane.getChildren().clear();
                            anchorPane.getChildren().add(parent);

                            // Add comportment to "SEND" button
                            button.setOnMouseClicked(event1 -> {
                                slyxSocket.sendMessage(
                                        textField.getText(),
                                        slyxSocket.getHashmapContacts().get(user.getId()),
                                        vBox
                                );
                                textField.setText("");
                            });

                            // Set textfield comportment on ENTER key pressed
                            textField.setOnKeyPressed(event12 -> {
                                if (event12.getCode().equals(KeyCode.ENTER)) {
                                    slyxSocket.sendMessage(
                                            textField.getText(),
                                            slyxSocket.getHashmapContacts().get(user.getId()),
                                            vBox
                                    );
                                    textField.setText("");
                                }
                            });

                            String contactIcon = user.getPicture();
                            Image contactImageIcon = new Image(contactIcon);
                            String myIcon = slyxSocket.getMe().getPicture();
                            Image myContactIcon = new Image(myIcon);

                            for (Message m : slyxSocket.contacts.get(slyxSocket.idOfCurrentContactPrinted).messages.values()) {
                                if ("IN".equals(m.getInOrOut()))
                                    putInVBoxMessages(m, vBox, contactImageIcon);
                                else
                                    putInVBoxMessages(m, vBox, myContactIcon);
                                m.printed = true;
                                slyxSocket.messagesPrinted++;
                            }
                            scrollPane.setVvalue(1);
                            slyxSocket.needToEmptyVBoxMessages = false;

                            slyxSocket.stopTimeline();

                            timelineRefreshMessages = new Timeline(new KeyFrame(
                                    Duration.millis(500),
                                    ae -> {
                                        if (slyxSocket.receivedCloseRequest)
                                            timelineRefreshMessages.stop();
                                        try {
                                            if (slyxSocket.needToEmptyVBoxMessages) {
                                                slyxSocket.clearVBox(vBox);
                                                slyxSocket.messagesPrinted = 0;
                                                slyxSocket.needToEmptyVBoxMessages = false;
                                            }
                                            if (user.messages.size() > slyxSocket.messagesPrinted ||
                                                    slyxSocket.refreshNumberForMessages > 0) {
                                                for (Message m : slyxSocket.contacts.get(slyxSocket.idOfCurrentContactPrinted).messages.values()) {
                                                    if (!m.printed) {
                                                        if ("IN".equals(m.getInOrOut()))
                                                            putInVBoxMessages(m, vBox, contactImageIcon);
                                                        else
                                                            putInVBoxMessages(m, vBox, myContactIcon);

                                                        m.printed = true;
                                                        slyxSocket.messagesPrinted++;
                                                    }
                                                }
                                                scrollPane.setVvalue(1);
                                            }
                                        } catch (IOException e) {
                                            System.out.println(e.getMessage());
                                        }
                                        if (slyxSocket.refreshNumberForMessages > 0) {
                                            slyxSocket.refreshNumberForMessages--;
                                            scrollPane.setVvalue(1);
                                        }
                                    }));
                            timelineRefreshMessages.setCycleCount(Animation.INDEFINITE);
                            timelineRefreshMessages.play();
                            slyxSocket.timelineMessages = timelineRefreshMessages;
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
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
     *
     * @param message : Message to print
     * @throws IOException : On FXMLLoader.load(...) call
     */
    private void putInVBoxMessages(Message message, VBox vBox, Image image) throws IOException {
        FXMLLoader fxmlLoader;
        Parent parent;
        if ("IN".equals(message.getInOrOut())) {
            fxmlLoader = new FXMLLoader(getClass().getResource("/slyx/scenes/message_in.fxml"));
            parent = fxmlLoader.load();
            MessageInController messageInController = fxmlLoader.getController();
            messageInController.setMessage(message, image, message.getContent());
        } else {
            fxmlLoader = new FXMLLoader(getClass().getResource("/slyx/scenes/message_out.fxml"));
            parent = fxmlLoader.load();
            MessageOutController messageOutController = fxmlLoader.getController();
            messageOutController.setMessage(message, image, message.getContent());
        }

        vBox.getChildren().add(parent);
    }
}
