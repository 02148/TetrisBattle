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


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GlobalChatUIController implements Initializable {
    @FXML private TextArea chatArea;
    @FXML private TextField chatTextField;
    private String user = "Username1";
    private String testUUID = "123456";
    @FXML private TextField username;
    @FXML private TextField roomUUID;
    @FXML private ToggleGroup group;
    private UserRepo testUserRepo = new UserRepo();
    private ChatRepo testChatRepo = new ChatRepo();
    private GameRoomRepo testGameRoomRepo = new GameRoomRepo();
    private Client client;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void  setClient(Client client){
        this.client = client;
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
        
        //Check if username and roomUUID was provided
        if(username.getText().trim().isEmpty()){
            username.setPromptText("Please input username");
            username.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        } else {
            client.login();

            //Check if room exists if not create one
            //Check if user want to join or host
            ToggleButton chosenButton = (ToggleButton) group.getSelectedToggle();
            String answer = chosenButton.getText();
            switch (answer){
                case "Host":
                    //User can host this
                    response = client.hostRoom();
                    if(response == "ok"){
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
                        if(response == "ok"){
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
        String response = client.login();
        if(response != "ok"){
            username.setPromptText("Please input username");
            username.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        }else {
            testChatRepo.create(chatTextField.getText());
            chatArea.appendText("\n"+ username.getText() + ": " + chatTextField.getText() );
            chatTextField.clear();
        }
    }


}
