package Client;


import javafx.application.Application;
import javafx.stage.Stage;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SpaceRepository;
import java.io.IOException;

// Right now a lot of crap is included - this is only used for debugging end development purposes - Magn.
public class Client extends Application {
  public String userName, UUID, roomUUID;
  public RemoteSpace userToServer, serverToUser, room;
  public boolean gameActive = false;
  private static RemoteSpace mainServer;

    @Override
  public void start(Stage primaryStage) {

  }
  public static void main(String[] args) throws IOException {
    //SpaceRepository repo = new SpaceRepository();
    //repo.addGate("tcp://LocalHost:6969/?mainServer");
    mainServer = new RemoteSpace("tcp://LocalHost:6969/main?mainServer");

    launch(args);
  }

  public String login() {
    Object[] loginResponse = new Object[3];

    try {


      mainServer.put(userName, "login","");
      loginResponse = mainServer.get(new ActualField(userName), new FormalField(String.class), new FormalField(String.class));

      if (loginResponse[1] == "ok") {
        UUID = (String) loginResponse[2];
      } else {
        //Error message
        System.out.println(loginResponse[1]);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return (String) loginResponse[1];
  }

  public String hostRoom() {
    Object[] roomResponse = new Object[3];
    try {

      mainServer.put(UUID, "create","");
      roomResponse = mainServer.get(new ActualField(UUID), new FormalField(String.class), new FormalField(String.class));

      if (roomResponse[1] == "ok") {
        roomUUID = (String) roomResponse[2];
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


      if (roomResponse[1] == "ok") {
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

      if (gameResponse[1] == "ok") {
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
