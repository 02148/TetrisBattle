package Client.UI;

import Client.Client;
import Client.GameSession.Consumer;
import Client.GameSession.ConsumerPackageHandler;
import Client.Logic.LocalGame;
import Client.Logic.Opponent;
import Client.GameOverListener;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import Client.ChatListener;
import javafx.concurrent.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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

    @FXML Text player1Text;
    @FXML Text player2Text;
    @FXML Text player3Text;
    @FXML Text player4Text;
    @FXML Text player5Text;
    @FXML Text player6Text;
    @FXML Text player7Text;
    @FXML Text player8Text;

    @FXML Text roomTitle;




    @FXML TextArea lines;
    @FXML TextArea level;


    private Client client;
    private ChatListener chatListener;
    private GameOverListener gameOverListner;
    private List<AnchorPane> playerViews = new ArrayList<>();

    private List<Text> playerNames = new ArrayList<>();
    Consumer consumerDelta;
    Consumer consumerFull;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerViews.add(player1View);
        playerNames.add(player1Text);

        playerViews.add(player2View);
        playerNames.add(player2Text);

        playerViews.add(player3View);
        playerNames.add(player3Text);

        playerViews.add(player4View);
        playerNames.add(player4Text);

        playerViews.add(player5View);
        playerNames.add(player5Text);

        playerViews.add(player6View);
        playerNames.add(player6Text);

        playerViews.add(player7View);
        playerNames.add(player7Text);

        playerViews.add(player8View);
        playerNames.add(player8Text);


    }
    public void setUpGlobalGameController(Client client) throws InterruptedException {
        this.client = client;
        chatListener = new ChatListener(gameChatArea);
        chatListener.setClient(client);
        chatListener.stop = false;
        Thread chatUpdater = new Thread(chatListener);
        Platform.runLater(()-> chatUpdater.start());

    }



    @FXML
    protected void handleLeaveGameAction(ActionEvent event) throws IOException {
        chatListener.stop = true;

        if(localGame != null){
            localGame.stop();
        }

        client.leaveRoom(consumerDelta, consumerFull);

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


    }


    @FXML protected void handleStartGameAction(ActionEvent event){
        HashMap<String,List<String>> playersInRoomInfo = client.TryStartGame();
        List<String> playersInRoom = playersInRoomInfo.get("UUID");
        List<String> playersInRoomNames = playersInRoomInfo.get("Names");
        playersInRoom.remove(client.UUID);

        //Setup listener to display scoreboard
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        gameOverListner = new GameOverListener(client, primaryStage);
        Platform.runLater(() -> new Thread(gameOverListner).start());


        if(playersInRoom != null){
            startGameButton.setDisable(true);
            startGameButton.setVisible(false);

            //Adding the other player boards
            int numPlayers = playersInRoom.size();

            System.out.println("There are "+ numPlayers + " in room "+ client.roomUUID);

            HashMap<String, ConsumerPackageHandler> consumerPackageHandlers = new HashMap<>();
            ArrayList<Board> opponentBoards = new ArrayList<>();

            for(int i = 0; i < playersInRoom.size(); i++){
                for(int j = 0; j < playersInRoomNames.size(); j++){
                    if(playersInRoomNames.get(j).equals(client.getUserName())) continue;
                    playerNames.get(i).setText(playersInRoomNames.get(j));
                }

            }



            for(int i = 0; i < playersInRoom.size(); i++ ){
                if(playersInRoom.get(i).equals(client.UUID)) continue;

                System.out.println("Initializing board for player nr " + (i+1) + " with id " + playersInRoom.get(i));

                Opponent newOpponent = new Opponent(playersInRoom.get(i));
                opponentBoards.add(newOpponent.getBoardView());

                addPlayerBoard(newOpponent.getBoardView(), playerViews.get(i));

                consumerPackageHandlers.put(playersInRoom.get(i), newOpponent.getConsumerPackageHandler());
            }
            if(!playersInRoom.isEmpty()) {
                try {
                    consumerDelta = new Consumer(client.UUID, playersInRoom, consumerPackageHandlers, "delta");
                    consumerFull = new Consumer(client.UUID, playersInRoom, consumerPackageHandlers, "full");

                    (new Thread(consumerDelta)).start();
                    (new Thread(consumerFull)).start();
                } catch (Exception e) {}
            }


            // Making local game
            localGame = new LocalGame(63, 94, client.roomUUID, client.UUID, playersInRoom, opponentBoards);
            boardHolder.getChildren().add(localGame.getViewModel());
            localGame.getViewModel().getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent keyEvent) {
                    localGame.keyDownEvent(keyEvent);
                }
            });
            Platform.runLater(() -> localGame.toThread().start());


            LocalGame.uiUpdater.progressProperty().addListener((obs,oldProgress,newProgress) ->
                    lines.setText(String.format("Lines %.0f", (newProgress.doubleValue()*100)/2)));
            LocalGame.uiUpdater.messageProperty().addListener((obs,oldProgress,newProgress) ->
                    level.setText("Level " + newProgress.toString()));
            LocalGame.uiUpdater.titleProperty().addListener((obs,oldProgress,newProgress) ->
                    {
                        //Get the list
                        String[] line = lines.getText().split(" ");
                        System.out.println("SCORE" + line[1]);
                        client.currScore = Integer.parseInt(line[1]);

                        localGame.stop();
                        client.gameOver(consumerDelta, consumerFull);

                    });

            Platform.runLater(() ->{
                LocalGame.uiUpdater.reset();
                LocalGame.uiUpdater.start();
            } );
        } else {
            //cant start game

        }
    }

    private void addPlayerBoard(Board playerBoard, AnchorPane pane){
        AnchorPane.setBottomAnchor(playerBoard, 0.0);
        AnchorPane.setTopAnchor(playerBoard,15.0);
        AnchorPane.setLeftAnchor(playerBoard, 50.0);
        AnchorPane.setRightAnchor(playerBoard, 5.0);
        pane.getChildren().add(playerBoard);
    }


    




}

