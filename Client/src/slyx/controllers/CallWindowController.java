package slyx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Created by Antoine Janvier
 * on 28/08/17.
 */
public class CallWindowController {
    @FXML
    WebView webView_myVideo;
    @FXML
    Label label_from;
    @FXML
    Label label_to;

    public void initialize() {
        WebEngine webEngine = webView_myVideo.getEngine();
        webEngine.load("http://127.0.0.1:3000/room/" + label_from.getText() + "/" + label_to.getText());
    }
}
