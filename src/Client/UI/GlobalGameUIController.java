package Client.UI;

import Client.Client;
import Client.GameEngine;
import MainServer.Chat.ChatRepo;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GlobalGameUIController implements Initializable {
    @FXML private TextArea gameChatArea;
    @FXML private TextField gameChatTextField;
    private String user = "Username1";
    private GameEngine gameEngine;

    @FXML AnchorPane boardHolder;
    @FXML TextArea lines;
    private Client client;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void  setClient(Client client) {
        this.client = client;
    }

    @FXML
    protected void handleLeaveGameAction(ActionEvent event) throws IOException {
        gameEngine.stop();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GlobalChatUI.fxml"));
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(loader.load());

        GlobalChatUIController globalChatUIController = loader.getController();
        globalChatUIController.setClient(client);

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();


    }
    @FXML protected void  handleGameChatInputAction(ActionEvent event) throws InterruptedException {
            //testChatRepo.create(gameChatTextField.getText());
            gameChatArea.appendText("\n"+ ": " + gameChatTextField.getText() );
            gameChatTextField.clear();

        //TODO: Add functionality to update TextArea based on input from other players
    }
    //TODO: Add functions to show the games/Make it possible to play
    @FXML protected void handleStartGameAction(ActionEvent event){
        Board nBoard = new Board(63,94,25);
        boardHolder.getChildren().add(nBoard);
        gameEngine = new GameEngine(nBoard);
        boardHolder.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                gameEngine.keyDownEvent(keyEvent);
            }
        });
        Platform.runLater(() -> gameEngine.toThread().start());


        GameEngine.TaskRun task = new GameEngine.TaskRun();
        task.progressProperty().addListener((obs,oldProgress,newProgress) ->
                lines.setText(String.format("lines %.0f", newProgress.doubleValue()*100)));
        new Thread(task).start();
    }
    




}

