package slyx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Created by Antoine Janvier
 * on 13/08/17.
 */
public class ContactProfileController {
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


    public void removeContact() {
        // TODO : Send remove contact request
    }

    public void callContact() {
        // TODO : Call contact
    }

    public void initialize() {
    }
}
