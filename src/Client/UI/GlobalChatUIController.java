package Client.UI;

import Client.Client;
import Main.Main;
import MainServer.Chat.ChatMessage;
import MainServer.Chat.ChatRepo;
import MainServer.GameRoom.GameRoom;
import MainServer.GameRoom.GameRoomRepo;
import MainServer.MainServer;
import MainServer.UserMgmt.UserRepo;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import Client.ChatListener;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GlobalChatUIController implements Initializable {
    @FXML private TextArea chatArea;
    @FXML private TextField chatTextField;
    @FXML private TextField username;
    @FXML private TextField roomUUID;
    @FXML private ToggleGroup group;

    private Client client;
    private boolean isLoggedIn = false;
    private boolean inGlobalRoom = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void  setClient(Client client){
        this.client = client;
        ChatListener chatListener = new ChatListener(chatArea);
        chatListener.setClient(client);
        chatListener.setServerToUser(client.serverToUser);
        Thread chatUpdater = new Thread(chatListener);
        Platform.runLater(()-> chatUpdater.start());
    }

    @FXML protected void handleExitButtonAction(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }
    @FXML protected void handleGoToLobbyButtonAction(ActionEvent event) throws IOException, InterruptedException {
        String response = "";
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GlobalGame.fxml"));
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(loader.load(),1300,800);
        GlobalGameUIController globalGameUIController = loader.getController();
        globalGameUIController.setClient(client);
        
        //Check if username and roomUUID was provided
        if(username.getText().trim().isEmpty()){
            username.setPromptText("Please input username");
            username.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        } else {
            if(!isLoggedIn){
                client.userName = username.getText();
                client.login();
                isLoggedIn = true;
            }


            //Check if room exists if not create one
            //Check if user want to join or host
            ToggleButton chosenButton = (ToggleButton) group.getSelectedToggle();
            String answer = chosenButton.getText();
            switch (answer){
                case "Host":
                    //User can host this
                    response = client.hostRoom();
                    if(response.equals("ok")){
                        primaryStage.setScene(scene);
                        primaryStage.centerOnScreen();
                        primaryStage.show();

                    } else {
                        roomUUID.setPromptText("Please input correct room ID");
                        username.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                    }
                    break;

                case "Join":
                    if (roomUUID.getText().trim().isEmpty()){
                        roomUUID.setPromptText("Please input room ID");
                        username.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                    } else {
                        //User should join this room
                        response = client.TryJoinRoom(roomUUID.getText());

                        //Handle response here
                        if(response.equals("ok")){
                            primaryStage.setScene(scene);
                            primaryStage.centerOnScreen();
                            primaryStage.show();
                        } else {
                            roomUUID.setPromptText("Please input correct room ID");
                            username.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                        }
                    }
                    break;
            }
        }
    }


    @FXML protected void handleChatInputAction(ActionEvent event) throws InterruptedException {
        if(!isLoggedIn){
            if(username.getText().trim().isEmpty()){
                username.setPromptText("Please input username");
                username.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
            } else {
                client.userName = username.getText();
                String response = client.login();
                if(response.equals("ok")){
                    isLoggedIn = true;

                    String[] chatResponse = client.sendGlobalChat(chatTextField.getText());
                    System.out.println(chatResponse[1]);
                    if(chatResponse[0].equals("ok")){
                        chatArea.appendText("\n"+ chatResponse[1] + " : " + username.getText() + ": " + chatTextField.getText() );
                        chatTextField.clear();
                    } else {
                        //Something went wrong
                    }
                }

            }
        } else {
            String[] chatResponse = client.sendGlobalChat(chatTextField.getText());
            if(chatResponse[0].equals("ok")){
                chatArea.appendText("\n"+ chatResponse[1] + " : " + username.getText() + ": " + chatTextField.getText() );
                chatTextField.clear();
            } else {
                //Something went wrong
            }

        }
    }


}
