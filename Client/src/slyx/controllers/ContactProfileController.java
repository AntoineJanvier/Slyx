package slyx.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import slyx.communication.SlyxSocket;
import slyx.utils.User;

import java.io.IOException;

/**
 * Created by Antoine Janvier
 * on 13/08/17.
 */
public class ContactProfileController {
    @FXML
    AnchorPane anchorPane_contactProfile;
    @FXML
    Label label_firstname;
    @FXML
    Label label_lastname;
    @FXML
    Label label_email;
    @FXML
    Button button_remove_contact;
    @FXML
    Button button_call_contact;
    @FXML
    ImageView imageView_contact_icon;

    private void clearAndDisable(TextField textField, Button button, VBox vBox) {
        vBox.getChildren().clear();
        for (Node observable : vBox.getChildren()) {
            vBox.getChildren().remove(observable);
        }
        textField.setDisable(true);
        button.setDisable(true);
    }

    void setContact(User user, VBox vBox, TextField textField, Button button) {
        label_firstname.setText(user.getFirstname());
        label_lastname.setText(user.getLastname());
        label_email.setText(user.getEmail());
        imageView_contact_icon.setImage(new Image(user.getPicture()));

        try {
            SlyxSocket slyxSocket = SlyxSocket.getInstance();
            button_remove_contact.setOnMouseClicked(event -> {
                // TODO : Test if contact is removed on both sides
                slyxSocket.sendRemoveContactOfContactList(user.getId());
                anchorPane_contactProfile.getChildren().clear();
                clearAndDisable(textField, button, vBox);
            });
//            button_call_contact.setOnMouseClicked(new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent event) {
//                    slyxSocket.sendCallContactRequest(slyxSocket.getMe().getId(), user.getId());
//
//                    try {
//                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/slyx/scenes/callWindow.fxml"));
//                        Parent parent = fxmlLoader.load();
//                        CallWindowController callWindowController = fxmlLoader.getController();
//                        callWindowController.setContacts(slyxSocket.getMe(), user);
//                        Stage stage = new Stage();
//                        stage.setScene(new Scene(parent));
//                        stage.show();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
            button_call_contact.setDisable(true);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void initialize() {
        // CSS
        anchorPane_contactProfile
                .getStylesheets().add(getClass().getResource("/slyx/css/contactProfile.css").toExternalForm());
    }
}
