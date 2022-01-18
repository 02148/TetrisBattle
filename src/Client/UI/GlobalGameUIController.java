package Client.UI;

import Client.Client;
import Client.GameEngine;
import Client.GameSession.Consumer;
import Client.Logic.Controls;
import Client.Logic.LocalGame;
import Client.Logic.Opponent;
import Client.Models.BoardState;
import MainServer.GameRoom.GameRoom;
import MainServer.GameRoom.GameRoomRepo;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class GlobalGameUIController implements Initializable {
    @FXML private TextArea gameChatArea;
    @FXML private TextField gameChatTextField;
    private LocalGame localGame;
    @FXML Button startGameButton;
    @FXML Button leaveGameButton;
    @FXML AnchorPane boardHolder;

    //Players
    @FXML AnchorPane player1View;
    @FXML AnchorPane player2View;
    @FXML AnchorPane player3View;
    @FXML AnchorPane player4View;
    @FXML AnchorPane player5View;
    @FXML AnchorPane player6View;
    @FXML AnchorPane player7View;
    @FXML AnchorPane player8View;




    @FXML TextArea lines;
    @FXML TextArea level;


    private Client client;
    private ChatListener chatListener;
    private List<AnchorPane> playerViews = new ArrayList<>();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerViews.add(player1View);
        playerViews.add(player2View);
        playerViews.add(player3View);
        playerViews.add(player4View);
        playerViews.add(player5View);
        playerViews.add(player6View);
        playerViews.add(player7View);
        playerViews.add(player8View);


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
        if(localGame != null){
            localGame.stop();
        }
        chatListener.stop = true;
        client.leaveRoom();

        client.roomUUID = "globalChat";

        FXMLLoader loader = new FXMLLoader(getClass().getResource("GlobalChatUI.fxml"));
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(loader.load());

        GlobalChatUIController globalChatUIController = loader.getController();
        globalChatUIController.setClient(client);
        globalChatUIController.setIsLoggedIn(true);
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
        List<String> playersInRoom = client.TryStartGame();

        if(true){
            startGameButton.setDisable(true);
            startGameButton.setVisible(false);


            // Making local game
            localGame = new LocalGame(63, 94, client.roomUUID);
            boardHolder.getChildren().add(localGame.getViewModel());
            localGame.getViewModel().getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent keyEvent) {
                    localGame.keyDownEvent(keyEvent);
                }
            });
            Platform.runLater(() -> localGame.toThread().start());


            // Making a second board for testing purposes
            //Opponent opponent1 = new Opponent(300, 95, "player1");
            //boardHolder.getChildren().add(opponent1.getBoardView());



            //Adding the other player boards
            int numPlayers = playersInRoom.size();

            System.out.println("There are "+ numPlayers + " in room "+ client.roomUUID);

            for(int i = 1; i < playersInRoom.size(); i++ ){
                System.out.println("Initializing board for player nr " + i + " with id " + playersInRoom.get(i));

                Opponent newOpponent = new Opponent(playersInRoom.get(i));
                addPlayerBoard(newOpponent.getBoardView(), playerViews.get(i-1));
            }



            LocalGame.TaskRunLines taskRunLines = new LocalGame.TaskRunLines();

            taskRunLines.progressProperty().addListener((obs,oldProgress,newProgress) ->
                    lines.setText(String.format("Lines %.0f", (newProgress.doubleValue()*100)/2)));
            taskRunLines.messageProperty().addListener((obs,oldProgress,newProgress) ->
                    level.setText("Level " + newProgress.toString()));

            Platform.runLater(() -> new Thread(taskRunLines).start());

        } else {
            //cant start game

        }
    }

    private void addPlayerBoard(Board playerBoard, AnchorPane pane){
        AnchorPane.setBottomAnchor(playerBoard, 5.0);
        AnchorPane.setTopAnchor(playerBoard,8.0);
        AnchorPane.setLeftAnchor(playerBoard, 50.0);
        AnchorPane.setRightAnchor(playerBoard, 5.0);
        pane.getChildren().add(playerBoard);
    }
    




}

