package Client.UI;

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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


import java.io.IOException;

public class GlobalChatUIController {
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






    @FXML protected void handleExitButtonAction(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }
    @FXML protected void handleGoToLobbyButtonAction(ActionEvent event) throws IOException, InterruptedException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GlobalGame.fxml"));
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(loader.load(),1300,800);
        
        //Check if username and roomUUID was provided
        if(username.getText().trim().isEmpty()){
            username.setText("Please input username");
            username.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        }
        else if ((ToggleButton) group.getSelectedToggle() == null ){
            //Need to choose wether to host or join
            Platform.exit();
            System.exit(0);
        } else {
            //Check if room exists if not create one
            //Check if user want to join or host
            ToggleButton chosenButton = (ToggleButton) group.getSelectedToggle();
            String answer = chosenButton.getText();
            switch (answer){
                case "Host":
                        //User can host this
                        testGameRoomRepo.create(username.getText());primaryStage.setScene(scene);
                        primaryStage.centerOnScreen();
                        primaryStage.show();
                    
                    break;
                case "Join":
                    if (roomUUID.getText().trim().isEmpty()){
                        roomUUID.setText("Please input room ID");
                        username.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                    } else {
                        //User should join this room
                        if(testGameRoomRepo.exists(roomUUID.getText())){
                            testGameRoomRepo.addConnection(username.getText(), roomUUID.getText());
                            primaryStage.setScene(scene);
                            primaryStage.centerOnScreen();
                            primaryStage.show();
                        } else {
                            roomUUID.setText("Please input correct room ID");
                            username.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                        }


                    }

                    break;
            }

        }


    }

    @FXML protected  void handlUsernameInputAction(ActionEvent event) throws Exception {
        if(testUserRepo.exists(username.getText()) == false){
            testUserRepo.create(username.getText());
            username.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
        }

    }

    @FXML protected void handleChatInputAction(ActionEvent event) throws InterruptedException {
        if(testUserRepo.exists(username.getText()) == false){
            username.setText("Please input username");
            username.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        }else {
            testChatRepo.create(chatTextField.getText());
            chatArea.appendText("\n"+ username.getText() + ": " + chatTextField.getText() );
            chatTextField.clear();
        }
    }


}
