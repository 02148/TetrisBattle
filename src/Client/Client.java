package Client;

import Client.UI.Board;
import Main.Main;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jspace.SpaceRepository;

import java.io.IOException;


// Right now a lot of crap is included - this is only used for debugging end development purposes - Magn.
public class Client extends Application {

  @Override
  public void start(Stage primaryStage) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("../Client/UI/GlobalChatUI.fxml"));
      Parent root = fxmlLoader.load();
      Scene scene = new Scene(root,600,400);
      primaryStage.setScene(scene);
      primaryStage.initStyle(StageStyle.UNDECORATED);
      primaryStage.centerOnScreen(); ;
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
