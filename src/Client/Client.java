package Client;

import Client.UI.Board;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.jspace.SpaceRepository;

import java.io.IOException;


// Right now a lot of crap is included - this is only used for debugging end development purposes - Magn.
public class Client extends Application {

  @Override
  public void start(Stage primaryStage) {
    Button btn = new Button();
    btn.setText("Say 'Hello World' to client");
    btn.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        System.out.println("Hello World!");
      }
    });

    StackPane root = new StackPane();
    root.getChildren().add(btn);

    Board nBoard = new Board(30, 30, 400);
    root.getChildren().add(nBoard);

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
