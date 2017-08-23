package slyx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/slyx/scenes/login.fxml"));
        primaryStage.setTitle("Slyx");
        root.getStylesheets().add(getClass().getResource("/slyx/css/login.css").toExternalForm());
        Scene scene = new Scene(root);
        //scene.getStylesheets()
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
