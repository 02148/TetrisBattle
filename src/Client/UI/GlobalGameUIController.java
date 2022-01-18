package Client.UI;

import Client.Client;
import Client.GameEngine;
import Client.GameSession.Consumer;
import Client.Logic.Controls;
import Client.Logic.LocalGame;
import Client.Logic.Opponent;
import Client.Models.BoardState;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import Client.ChatListener;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GlobalGameUIController implements Initializable {
    @FXML private TextArea gameChatArea;
    @FXML private TextField gameChatTextField;
    private GameEngine gameEngine;
    @FXML Button startGameButton;
    @FXML Button leaveGameButton;
    @FXML AnchorPane boardHolder;
    @FXML TextArea lines;
    @FXML TextArea level;


    private Client client;
    private ChatListener chatListener;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void  setClient(Client client) throws InterruptedException {
        this.client = client;
        chatListener = new ChatListener(gameChatArea);
        chatListener.setClient(client);
        chatListener.stop = false;
        Thread chatUpdater = new Thread(chatListener);
        Platform.runLater(()-> chatUpdater.start());
    }



    @FXML
    protected void handleLeaveGameAction(ActionEvent event) throws IOException {
        if(gameEngine != null){
            gameEngine.stop();
        }
        chatListener.stop = true;
        client.leaveRoom();
        client.roomUUID = "globalChat";

        FXMLLoader loader = new FXMLLoader(getClass().getResource("GlobalChatUI.fxml"));
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(loader.load());

        GlobalChatUIController globalChatUIController = loader.getController();
        globalChatUIController.setClient(client);
        globalChatUIController.setUpChatListner();

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();


    }
    @FXML protected void  handleGameChatInputAction(ActionEvent event) throws InterruptedException {
            client.sendChat(gameChatTextField.getText());
            gameChatTextField.clear();

        //TODO: Add functionality to update TextArea based on input from other players
    }
    //TODO: Add functions to show the games/Make it possible to play
    @FXML protected void handleStartGameAction(ActionEvent event){
        startGameButton.setDisable(true);
        startGameButton.setVisible(false);


        // Making local game
        LocalGame localGame = new LocalGame(63, 94, "player1");
        boardHolder.getChildren().add(localGame.getViewModel());
        localGame.getViewModel().getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                localGame.keyDownEvent(keyEvent);
            }
        });
        Platform.runLater(() -> localGame.toThread().start());


        // Making a second board for testing purposes
        Opponent opponent1 = new Opponent(300, 95, "player1");
        boardHolder.getChildren().add(opponent1.getBoardView());



        GameEngine.TaskRunLines taskRunLines = new GameEngine.TaskRunLines();

        taskRunLines.progressProperty().addListener((obs,oldProgress,newProgress) ->
                lines.setText(String.format("Lines %.0f", (newProgress.doubleValue()*100)/2)));
        taskRunLines.messageProperty().addListener((obs,oldProgress,newProgress) ->
                level.setText("Level " + newProgress.toString()));

        Platform.runLater(() -> new Thread(taskRunLines).start());


    }
    




}

