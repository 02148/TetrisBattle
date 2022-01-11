package Client;


import Client.UI.GlobalChatUIController;
import Main.Main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SpaceRepository;
import java.io.IOException;

// Right now a lot of crap is included - this is only used for debugging end development purposes - Magn.
public class Client extends Application {
  public static String userName;
  public static String UUID;

  public String roomUUID;

  public static RemoteSpace userToServer;
  public static RemoteSpace serverToUser;
  public RemoteSpace room;

  public boolean gameActive = false;

  private static RemoteSpace mainServer;

    @Override
  public void start(Stage primaryStage) {


  }
  public static void main(String[] args) throws IOException {
    //SpaceRepository repo = new SpaceRepository();
    //repo.addGate("tcp://LocalHost:6969/?mainServer");
    mainServer = new RemoteSpace("tcp://localhost:6969/main?mainServer");
    userToServer = new RemoteSpace("tcp://localhost:6969/userToServer?conn");
    serverToUser = new RemoteSpace("tcp://localhost:6969/serverToUser?conn");

    //launch(args);
  }


  public static String login() {
    Object[] loginResponse = new Object[3];

    try {
      //userName = "holder";
      userToServer.put(userName, "login","something");
      loginResponse = serverToUser.get(new ActualField(userName), new FormalField(String.class), new FormalField(String.class));
      if (loginResponse[1].equals("ok")) {
        UUID = (String) loginResponse[2];
        System.out.println("Logged in repsonse got from server");
      } else {
        //Error message
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return (String) loginResponse[1];
  }

  public String hostRoom() {
    Object[] roomResponse = new Object[3];
    try {

      userToServer.put(UUID, "create","");
      roomResponse = serverToUser.get(new ActualField(UUID), new FormalField(String.class), new FormalField(String.class));
      if (roomResponse[1].equals("ok")) {

        roomUUID = (String) roomResponse[2];
        System.out.println("Room can be started by UI");
        //Room can be started by UI
      } else {
        //Error message
        System.out.println(roomResponse[1]);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return (String) roomResponse[1];
  }

  public String TryJoinRoom(String roomName) {
    Object[] roomResponse = new Object[3];
    try {
      mainServer.put(UUID, "join", roomName);
      roomResponse = mainServer.get(new ActualField(UUID), new FormalField(String.class), new FormalField(String.class));


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
    return (String) roomResponse[1];

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

  public String sendGlobalChat(String message){
    Object[] chatResponse = new Object[2];
    try {
      userToServer.put(UUID, "globalChat", message);
      chatResponse = serverToUser.get(new ActualField(UUID), new FormalField(String.class));

      if (chatResponse[1].equals("ok")) {
        System.out.println("Chat can be sent by UI");

        //Chat can be sent and UI updated


      } else {
        //Error message
        System.out.println(chatResponse[1]);
      }

    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return (String) chatResponse[1];

  }

  public void sendGameRoomChat(){
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
