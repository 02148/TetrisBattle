package Client;

import Client.UI.Board;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.jspace.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLOutput;
import java.util.UUID;


// Right now a lot of crap is included - this is only used for debugging end development purposes - Magn.
public class Client extends Application {
  public String userName, UUID, roomUUID;
  public RemoteSpace userToServer, serverToUser, room;
  public boolean gameActive = false;

  @Override
  public void start(Stage primaryStage) {
    Button btn = new Button();
    btn.setText("Say 'Hello World' to client");

    Board nBoard = new Board(300, 30, 20);

    StackPane root = new StackPane();
    root.getChildren().add(btn);
    root.getChildren().add(nBoard);
    Scene scene = new Scene(root, 750, 500);


    btn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        System.out.println("Hello World!");
      }
    });

    primaryStage.setTitle("Hello World!");
    primaryStage.setScene(scene);
    primaryStage.show();

    GameEngine gameEngine = new GameEngine(nBoard);

    scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent keyEvent) {
        gameEngine.keyDownEvent(keyEvent);
      }
    });

    gameEngine.toThread().start();
  }
  public static void main(String[] args) throws IOException {
    //SpaceRepository repo = new SpaceRepository();
    //repo.addGate("tcp://LocalHost:6969/?mainServer");

    launch(args);
  }

  public void login() {
    try {
      this.userToServer = new RemoteSpace("tcp://localhost:6969/userToServer?conn");
      this.serverToUser = new RemoteSpace("tcp://localhost:6969/ServerToUser?conn");

      userName = "holder";
      Object[] loginResponse = new Object[0];
      userToServer.put(userName, "login","something");
      loginResponse = serverToUser.get(new ActualField(userName), new FormalField(String.class), new FormalField(String.class));
      if (loginResponse[1].equals("ok")) {
        UUID = (String) loginResponse[2];
      } else {
        //Error message
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }

  }

  public void hostRoom() {

    try {
      Object[] roomResponse = new Object[0];
      userToServer.put(UUID, "create","");
      roomResponse = serverToUser.get(new ActualField(UUID), new FormalField(String.class), new FormalField(String.class));
      if (roomResponse[1].equals("ok")) {
        roomUUID = (String) roomResponse[2];
        System.out.println("ok");
        //Room can be started by UI
      } else {
        //Error message
        System.out.println(roomResponse[1]);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void TryJoinRoom(String roomName) {
    try {
      Object[] roomResponse = new Object[0];
      userToServer.put(UUID, "join", roomName);
      roomResponse = serverToUser.get(new ActualField(UUID), new FormalField(String.class), new FormalField(String.class));

      if (roomResponse[1].equals("ok")) {
        roomUUID = (String) roomResponse[2];
        //Room can be started by UI

        //New thread waiting for game to start
      } else {
        //Error message
        System.out.println(roomResponse[1]);
      }

    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void TryStartGame() {
    try {
      Object[] gameResponse = new Object[0];
      room.put(UUID, "start");
      gameResponse = room.get(new ActualField(UUID), new FormalField(String.class), new FormalField(String.class));

      if (gameResponse[1].equals("ok")) {
        gameActive = true;
        //Game can be started by UI

      } else {
        //Error message
        System.out.println(gameResponse[1]);
      }

    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
