package Client.UI;

import Client.Client;
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
    private ChatListener chatListener;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setClient(Client client){
        this.client = client;
    }

    public void setUpChatListner(){
        chatListener = new ChatListener(chatArea, client.UUID);
        chatListener.setClient(client);
        Thread chatUpdater = new Thread(chatListener);
        Platform.runLater(()-> chatUpdater.start());
    }
    @FXML protected void handleLoginAction(ActionEvent event){
        if(!isLoggedIn){
            client.userName = username.getText();
            String response = client.login();
            isLoggedIn = true;
            if(response.equals("ok")){
                isLoggedIn = true;

                client.setUpGLobalChat();

                setUpChatListner();
                chatListener.stop = false;

            }
        }


    }

    @FXML protected void handleExitButtonAction(ActionEvent event) {
        if(chatListener != null){
            chatListener.stop = true;
        }
       
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
                        if(chatListener != null){
                            chatListener.stop = true;
                        }
                        GlobalGameUIController globalGameUIController = loader.getController();
                        globalGameUIController.setClient(client);
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
                        roomUUID.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                    } else {

                        //User should join this room
                        response = client.TryJoinRoom(roomUUID.getText());

                        //Handle response here
                        if(response.equals("ok")){
                            if(chatListener != null){
                                chatListener.stop = true;
                            }

                            GlobalGameUIController globalGameUIController = loader.getController();
                            globalGameUIController.setClient(client);
                            primaryStage.setScene(scene);
                            primaryStage.centerOnScreen();
                            primaryStage.show();
                        } else {
                            roomUUID.setPromptText("Please input correct room ID");
                            roomUUID.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
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

                    client.sendGlobalChat(chatTextField.getText());
                    chatTextField.clear();

                    setUpChatListner();
                    chatListener.stop = false;

                }

            }
        } else {
            client.sendGlobalChat(chatTextField.getText());
            chatTextField.clear();
            }
        }
    }



