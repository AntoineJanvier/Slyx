package slyx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;


/**
 * Created by Antoine Janvier
 * on 31/07/17.
 */
public class ContactController {
    @FXML
    AnchorPane anchorPane_contact;
    @FXML
    ImageView imageView_contact_icon;
    @FXML
    Label label_firstname;
    @FXML
    Label label_lastname;

    public void initialize() {
//        String imagePath = "http://localhost:3000/images/icon_profile.png";
//        Image image = new Image(imagePath);
//        imageView_contact_icon.setImage(image);
        /*
        TODO : Get if contact is connected
         */
    }
}
