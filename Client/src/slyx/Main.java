package slyx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/slyx/scenes/login.fxml"));
            primaryStage.setTitle("Slyx");
            root.getStylesheets().add(getClass().getResource("/slyx/css/login.css").toExternalForm());
            Scene scene = new Scene(root);
            //scene.getStylesheets()
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Error while launching the app");
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
