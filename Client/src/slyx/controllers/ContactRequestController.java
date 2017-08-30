package slyx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import slyx.communication.SlyxSocket;
import slyx.utils.User;

import java.io.IOException;


/**
 * Created by Antoine Janvier
 * on 13/08/17.
 */
public class ContactRequestController {
    @FXML
    AnchorPane anchorPane_contact;
    @FXML
    Label label_name;
    @FXML
    ImageView imageView_contact_icon;
    @FXML
    Button button_reject_request;
    @FXML
    Button button_add_accept_request;

    void setWithUser(User user, boolean before) {
        label_name.setText(user.getFirstname() + " " + user.getLastname());
        try {
            SlyxSocket slyxSocket = SlyxSocket.getInstance();
            if (!before) {
                button_reject_request.setOnMouseClicked(event -> {
                    slyxSocket.sendRejectContactRequest(user.getId());
                    slyxSocket.removeContactRequest(user);
                    anchorPane_contact.setVisible(false);
                });
            } else {
                button_reject_request.setVisible(false);
            }
            button_add_accept_request.setOnMouseClicked(event -> {
                if (before) {
                    slyxSocket.sendAddContactRequest(user.getId());
                } else {
                    slyxSocket.sendAcceptContactRequest(user.getId());
                    slyxSocket.removeContactRequest(user);
                    slyxSocket.sendGetPendingContactRequests();
                }
                anchorPane_contact.setVisible(false);
                slyxSocket.needToRefreshContacts = true;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        // CSS
        anchorPane_contact
                .getStylesheets().add(getClass().getResource("/slyx/css/contactRequest.css").toExternalForm());
    }
}
