package Client.UI;

import Client.Client;
import Client.GameEngine;
import Client.GameSession.Consumer;
import Client.Logic.Controls;
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
import java.util.List;
import java.util.ResourceBundle;

public class GlobalGameUIController implements Initializable {
    @FXML private TextArea gameChatArea;
    @FXML private TextField gameChatTextField;
    private GameEngine gameEngine;
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

        client.roomUUID = "globalChat";

        FXMLLoader loader = new FXMLLoader(getClass().getResource("GlobalChatUI.fxml"));
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(loader.load());

        GlobalChatUIController globalChatUIController = loader.getController();
        globalChatUIController.setClient(client);
        globalChatUIController.setIsLoggedIn(true);

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
    @FXML protected void handleStartGameAction(ActionEvent event) throws InterruptedException {
        List<String> playersInRoom = client.TryStartGame();

        if(playersInRoom!= null){
            startGameButton.setDisable(true);
            startGameButton.setVisible(false);
            Board nBoard = new Board(63,94,25);
            boardHolder.getChildren().add(nBoard);
            gameEngine = new GameEngine(nBoard);
            nBoard.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent keyEvent) {
                    gameEngine.keyDownEvent(keyEvent);
                }
            });
            Platform.runLater(() -> gameEngine.toThread().start());


            //Adding the other player boards
            int numPlayers = playersInRoom.size();

            System.out.println("There are "+ numPlayers + " in room "+ client.roomUUID);

            // Making a second board for testing purposes
            Board nBoard2 = new Board(300,94,10);
            boardHolder.getChildren().add(nBoard2);

            BoardState boardState2 = new BoardState(200);
            Controls controls2 = new Controls(nBoard2, boardState2, true);
            try {
                Consumer consumerFull = new Consumer("tcp://localhost:1337/player1?keep", boardState2, controls2, "full"); // haps haps full
                Consumer consumerDelta = new Consumer("tcp://localhost:1337/player1?keep", boardState2, controls2, "delta"); // haps haps delta
                (new Thread(consumerFull)).start();
                (new Thread(consumerDelta)).start();
            } catch (Exception e) {}





            GameEngine.TaskRunLines taskRunLines = new GameEngine.TaskRunLines();
            taskRunLines.progressProperty().addListener((obs,oldProgress,newProgress) ->
                    lines.setText(String.format("Lines %.0f", (newProgress.doubleValue()*100)/2)));
            taskRunLines.messageProperty().addListener((obs,oldMessage,newMessage) ->
                    level.setText("Level " + newMessage.toString()));

            Platform.runLater(() -> new Thread(taskRunLines).start());


            Board player1Board = new Board(12);
            addPlayerBoard(player1Board, player1View);

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

