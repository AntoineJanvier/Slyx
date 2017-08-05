package slyx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Created by Antoine Janvier
 * on 31/07/17.
 */
public class ContactController {
    @FXML
    WebView webView_contact_icon;
    @FXML
    Label label_firstname;
    @FXML
    Label label_lastname;

    public void initialize() {
        // Load default icon in WebView for contacts
        WebEngine engine = webView_contact_icon.getEngine();
        String url = "http://localhost:3000/images/icon_profile.png";
        engine.load(url);
        /*
        TODO : Get if contact is connected
         */
    }
}
