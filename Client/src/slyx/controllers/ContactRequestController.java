package slyx.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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

    public void setWithUser(User user) {

    }

    public void initialize() {
//        anchorPane_contact.getStylesheets().add(getClass().getResource("/slyx/css/contact.css").toExternalForm());
    }
}
