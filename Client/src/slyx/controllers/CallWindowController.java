package slyx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import slyx.utils.User;

/**
 * Created by Antoine Janvier
 * on 28/08/17.
 */
public class CallWindowController {
    @FXML
    AnchorPane anchorPane_callWindow;
    @FXML
    WebView webView_myVideo;
    @FXML
    Label label_from;
    @FXML
    Label label_to;

    private void call(int from, int to) {
        WebEngine webEngine = webView_myVideo.getEngine();
        webEngine.load("http://127.0.0.1:3000/room/" + from + "/" + to);
    }

    void setContacts(User from, User to) {
        label_from.setText(String.valueOf(from.getId()));
        label_to.setText(String.valueOf(to.getId()));

        ((Stage)anchorPane_callWindow.getScene().getWindow()).setTitle("Calling " + to.getFirstname());

        call(from.getId(), to.getId());
    }

    public void initialize() {
        // CSS
        anchorPane_callWindow
                .getStylesheets().add(getClass().getResource("/slyx/css/callWindow.css").toExternalForm());
    }
}
