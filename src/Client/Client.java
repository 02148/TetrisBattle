package Client;


import Client.UI.GlobalChatUIController;
import Main.Main;
import MainServer.Utils;
import common.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jspace.*;

import java.awt.*;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Right now a lot of crap is included - this is only used for debugging end development purposes - Magn.
public class Client extends Application {
  public static String userName;
  public static String UUID;

  public String roomUUID = "globalChat";

  public static RemoteSpace userToServer;
  public RemoteSpace serverToUser;
  public RemoteSpace chatSpace;

  public boolean isGameActive = false;
  public int currScore = 0;

  private static RemoteSpace mainServer;


  public static ChatListener chatListener;

  @Override
  public void start(Stage primaryStage) {

  }
  public void main(String[] args) throws IOException {
    //SpaceRepository repo = new SpaceRepository();
    //repo.addGate("tcp://" + Constants.IP_address+ ":6969/?mainServer");
    mainServer = new RemoteSpace("tcp://" + Constants.IP_address+ ":6969/main?mainServer");
    userToServer = new RemoteSpace("tcp://" + Constants.IP_address+ ":6969/userToServer?conn");
    serverToUser = new RemoteSpace("tcp://" + Constants.IP_address+ ":6969/serverToUser?conn");

    //launch(args);
  }


  public String login() {
    Object[] loginResponse = new Object[3];

    try {
      //userName = "holder";
      userToServer.put(userName, "login","","",0);
      loginResponse = serverToUser.get(new ActualField(userName), new FormalField(String.class), new FormalField(String.class));
      if (loginResponse[1].equals("ok")) {
        UUID = (String) loginResponse[2];
        System.out.println("Logged in response got from server");
        chatSpace = new RemoteSpace("tcp://" + Constants.IP_address+ ":4242/globalChat?conn");
      } else {
        //Error message
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return (String) loginResponse[1];
  }

  public String hostRoom() {
    Object[] roomResponse = new Object[4];
    try {

      userToServer.put(UUID, "create","","",0);
      roomResponse = serverToUser.get(new ActualField(UUID),
              new FormalField(String.class), //Response
              new FormalField(String.class), //RoomUUID
              new FormalField(String.class)); //Room nr
      if (roomResponse[1].equals("ok")) {

        roomUUID = (String) roomResponse[2];
        System.out.println("Room can be started by UI");

        chatSpace = new RemoteSpace("tcp://" + Constants.IP_address+ ":4242/" + roomUUID + "?conn");

        //Room can be started by UI
        System.out.println("Room can be joined using " + roomResponse[3]);

        isGameActive = true;

      } else {
        //Error message
        System.out.println(roomResponse[1]);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return (String) roomResponse[1];
  }

  public String TryJoinRoom(String newRoomName) {
    Object[] roomResponse = new Object[3];
    try {
      userToServer.put(UUID, "join", newRoomName,"",0);
      roomResponse = serverToUser.get(new ActualField(UUID),
              new FormalField(String.class),
              new FormalField(String.class));


      if (roomResponse[1].equals("ok")) {
        roomUUID = (String) roomResponse[2];
        chatSpace = new RemoteSpace("tcp://" + Constants.IP_address+ ":4242/" + roomUUID + "?conn");
        //Room can be started by UI
        System.out.println("Trying to join room");


        //New thread waiting for game to start
      } else {
        //Error message
        System.out.println(roomResponse[1]);
      }

    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return (String) roomResponse[1];

  }



  public void leaveRoom() {
    try {
      userToServer.put(UUID, "leave", roomUUID,"", 0);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public List<String> TryStartGame() {
    Object[] gameResponse = new Object[0];
    List<String> players = null;
    try {

      userToServer.put(UUID, "start", roomUUID,"", 0);
      gameResponse = serverToUser.get(new ActualField(UUID), new FormalField(String.class), new FormalField(List.class));
      players = (List<String>) gameResponse[2];


      if (gameResponse[1].equals("ok")) {
        isGameActive = true;



        //Game can be started by UI

      } else {

        //Error message
        System.out.println(gameResponse[1]);
      }

    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return players;
  }

  public void sendChat(String message) throws InterruptedException {
    Object[] chatResponse = new Object[4];
    try {
      chatSpace.put(UUID, roomUUID, userName, Utils.getCurrentExactTimestamp(), message);

    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Thread.sleep(50);
  }
  public String getUserName(){
    return userName;
  }

  public void gameOver() {
    Object[] gameResponse = new Object[0];

    try {

      userToServer.put(UUID, "gameOver", roomUUID, userName, currScore);
      gameResponse = serverToUser.query(
              new ActualField(UUID),
              new FormalField(String.class),
              new FormalField(String.class),
              new FormalField(Object.class));
      System.out.println("Client: Got game over response ");


      if (gameResponse[1].equals("ok")) {
        isGameActive = false;

        //Game ended

      } else {

        //Game can not be ended
        System.out.println(gameResponse[1]);
      }

    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}







