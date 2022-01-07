package Main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.StageStyle;

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jspace.SpaceRepository;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("../Client/UI/GlobalChatUI.fxml"));
            primaryStage.initStyle(StageStyle.UNDECORATED);
            Parent root = fxmlLoader.load();
            Scene sc = new Scene(root,600,400);
            primaryStage.setScene(sc);
        } catch (IOException e) {
            e.printStackTrace();
        }


        primaryStage.show();


    }


    public static void main(String[] args) throws IOException {
        SpaceRepository repo = new SpaceRepository();
        repo.addGate("tcp://server:6969/?keep");

        launch(args);
    }
}
