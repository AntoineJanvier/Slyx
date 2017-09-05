package slyx.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import slyx.Version;
import slyx.communication.SlyxSocket;
import slyx.validators.LoginValidator;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import static slyx.exceptions.SlyxError.ERR_CONNECTION;
import static slyx.exceptions.SlyxError.ERR_EMAIL;
import static slyx.exceptions.SlyxError.ERR_PASSWORD;
import static slyx.exceptions.SlyxErrors.getError;

public class LoginController {
    private Timeline timelineRefreshVersion = null;

    @FXML
    AnchorPane anchorPane_general;
    @FXML
    Label label_connection;
    @FXML
    Label label_get_update;
    @FXML
    TextField tf_email;
    @FXML
    PasswordField tf_password;
    @FXML
    Button btn_sign_in;
    @FXML
    Hyperlink hyperlink_sign_up;
    @FXML
    Hyperlink hyperlink_get_update;
    @FXML
    Label label_error_hint;
    @FXML
    ImageView imageView_logo;

    @FXML
    public void launch_next_screen() {
        SlyxSocket slyxSocket = null;
        try {
            slyxSocket = SlyxSocket.getInstance();
        } catch (IOException e) {
            System.out.println("Problem on Sign in");
        }

        // Get connection information
        String u_email = tf_email.getText();
        String u_pwd = tf_password.getText();

        // Test inputs
        if (!LoginValidator.isValidEmailAddress(u_email))
            label_error_hint.setText(getError(ERR_EMAIL));
        else if (!LoginValidator.isValidPassword(u_pwd))
            label_error_hint.setText(getError(ERR_PASSWORD));
        else {
            if (slyxSocket != null) {
                slyxSocket.setMe(null);
                // If all seems ok, request the server a connection
                slyxSocket.sendAskConnection(u_email, u_pwd);

                // Test singleton of Me object to know if we can launch the app just if he is connected
                while (slyxSocket.getMe() == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        System.out.println("Thread.sleep() interrupted");
                    }
                }
                if (slyxSocket.getMe().isConnected()) {

                    // Save email to a log file to retrieve on next login
                    try {
                        PrintWriter printWriter = new PrintWriter("logins.txt", "UTF-8");
                        printWriter.write(slyxSocket.getMe().getEmail());
                        printWriter.close();
                    } catch (FileNotFoundException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    // Close login window
                    Stage stage = (Stage) btn_sign_in.getScene().getWindow();
                    stage.close();

                    // Launch app window
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/slyx/scenes/slyx.fxml"));
                        Parent parent = fxmlLoader.load();
                        Stage next_stage = new Stage();
                        next_stage.setTitle("Slyx");
                        next_stage.setScene(new Scene(parent));
                        next_stage.setResizable(false);
                        timelineRefreshVersion.stop();
                        next_stage.show();
                    } catch (IOException e) {
                        System.out.println("FXMLLoader.load(...) error");
                    }
                } else {
                    label_error_hint.setText(getError(ERR_CONNECTION));
                }
            }
        }
    }
    public void initialize() {

        String lastEmailConnected = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader("logins.txt");
        } catch (FileNotFoundException e) {
            try {
                PrintWriter printWriter = new PrintWriter("logins.txt");
                printWriter.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        }

        if (fileReader != null) {
            try (BufferedReader br = new BufferedReader(fileReader)) {
                String line;
                while ((line = br.readLine()) != null) {
                    lastEmailConnected = line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // CSS
        anchorPane_general
                .getStylesheets().add(getClass().getResource("/slyx/css/login.css").toExternalForm());

        imageView_logo.setImage(new Image("/slyx/images/slyx-logo.png"));
        tf_email.setText(lastEmailConnected != null ? lastEmailConnected : "");
        tf_password.setText("");

        try {
            SlyxSocket slyxSocket = SlyxSocket.getInstance();
            label_get_update.setText("Checking updates...");

            hyperlink_sign_up.setOnMouseClicked(event -> {
                try {
                    new ProcessBuilder("x-www-browser", slyxSocket.getIpAddress() + ":3000/sign").start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            hyperlink_get_update.setOnMouseClicked(event -> {
                try {
                    new ProcessBuilder("x-www-browser", slyxSocket.getIpAddress() + ":3000").start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            slyxSocket.sendAskVersion();

            timelineRefreshVersion = new Timeline(new KeyFrame(
                    Duration.millis(1000),
                    ae -> initSetUpdateLabel(SlyxSocket.getVersion().split("\\."))));
            timelineRefreshVersion.setCycleCount(Animation.INDEFINITE);
            timelineRefreshVersion.play();

        } catch (IOException e) {
            label_get_update.setText("Error while checking updates");
            e.printStackTrace();
        }
    }

    private void initSetUpdateLabel(String[] v) {
        if (Integer.parseInt(v[0]) > Version.MAJOR) {
            label_get_update.setText("A new major update is available, download it");
            hyperlink_get_update.setText("here");
        } else if (Integer.parseInt(v[1]) > Version.INTERMEDIATE) {
            label_get_update.setText("A new intermediate update is available, download it");
            label_get_update.setLayoutX(label_get_update.getLayoutX() - 200);
            hyperlink_get_update.setText("here");
        } else if (Integer.parseInt(v[2]) > Version.MINOR) {
            label_get_update.setText("A new minor update is available, download it");
            hyperlink_get_update.setText("here");
        } else {
            label_get_update.setText("Up to date");
        }
    }
}
