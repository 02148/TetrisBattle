package Client;

import Client.Client;
import Client.UI.ScoreBoardUIController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.print.PageLayout;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.IOException;
import java.util.HashMap;

public class gameOverListner implements Runnable{
    Client client;
    public RemoteSpace serverToUser;
    public boolean stop = false;
    private Stage primaryStage;

    public gameOverListner( Client client, Stage primaryStage){
        this.client = client;
        serverToUser = client.serverToUser;
        this.primaryStage = primaryStage;
    }


    @Override
    public void run() {

        while(!stop){
            try {
                Object[] serverResponse = serverToUser.query(
                        new FormalField(String.class),
                        new FormalField(String.class),
                        new ActualField(client.roomUUID),
                        new FormalField(Object.class)
                );

                if(serverResponse[1].equals("ok")){
                    System.out.println("Got game over response ");
                    HashMap<String, Integer> scores = (HashMap<String, Integer>) serverResponse[3];
                    setUpPopUp(primaryStage, scores);
                    stop = true;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }

    private void setUpPopUp(Stage primaryStage, HashMap<String,Integer> scores){

        FXMLLoader loader = new FXMLLoader(getClass().getResource("UI/ScoreBoard.fxml"));
        try {
            VBox popUpUI = loader.<VBox>load();

            Popup popUp = new Popup();
            popUp.getContent().addAll(popUpUI);

            Platform.runLater(() -> popUp.show(primaryStage));

            ScoreBoardUIController scoreBoardUIController = loader.getController();
            scoreBoardUIController.setClient(client);
            scoreBoardUIController.setPrimaryStage(primaryStage);
            scoreBoardUIController.setUpScores(scores);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }
}
