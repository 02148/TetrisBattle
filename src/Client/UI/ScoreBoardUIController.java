package Client.UI;

import Client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class ScoreBoardUIController {
    @FXML Button menuButton;
    @FXML VBox sbPop;
    @FXML ListView scoreListView;
    private Client client;
    private Stage primaryStage;


    public void  setClient(Client client) throws InterruptedException {
        this.client = client;
    }

    public  void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public void setUpScores(HashMap<String,Integer> scores){
        for(Map.Entry<String, Integer> set : scores.entrySet()){
            DecimalFormat df = new DecimalFormat("#");
            scoreListView.getItems().add(set.getKey() + ": " + df.format(set.getValue()));
        }


    }

    @FXML protected void handleGoToMenuButton(ActionEvent event) throws IOException {
        client.leaveRoom(null, null);

        client.roomUUID = "globalChat";

        FXMLLoader loader = new FXMLLoader(getClass().getResource("GlobalChatUI.fxml"));
        Scene scene = new Scene(loader.load());

        GlobalChatUIController globalChatUIController = loader.getController();
        globalChatUIController.setClient(client);
        globalChatUIController.setIsLoggedIn(true);
        globalChatUIController.setUpChatListner();

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();

        //Hide the popup
        sbPop.getScene().getWindow().hide();

    }


}
