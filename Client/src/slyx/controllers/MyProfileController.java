package slyx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import slyx.utils.User;

/**
 * Created by Antoine Janvier
 * on 24/08/17.
 */
public class MyProfileController {
    @FXML
    AnchorPane anchorPane_myProfile;
    @FXML
    ImageView imageView_myImage;
    @FXML
    Label label_myName;
    @FXML
    Label label_myEmail;

    void setMe(User user) {
        label_myName.setText(user.getFirstname() + " " + user.getLastname());
        label_myEmail.setText(user.getEmail());
        imageView_myImage.setImage(new Image(user.getPicture()));
    }

    public void initialize() {
        anchorPane_myProfile
                .getStylesheets().add(getClass().getResource("/slyx/css/myProfile.css").toExternalForm());
    }
}
