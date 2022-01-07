package Main;

import Client.UI.Board;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jspace.RemoteSpace;

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.jspace.SpaceRepository;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(event -> System.out.println("Hello World!"));

        StackPane root = new StackPane();
        root.getChildren().add(btn);

        Scene scene = new Scene(root, 750, 500);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException {
        SpaceRepository repo = new SpaceRepository();
        repo.addGate("tcp://server:6969/?keep");

        launch(args);
    }
}
