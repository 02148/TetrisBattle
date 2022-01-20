package Client.UI;

import Client.Client;
import Main.Main;
import common.Constants;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SplashScreenController implements Initializable {
    @FXML private AnchorPane splashScreen;
    private SplitPane splitPane;
    private Client client;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void setClient(Client client){
        this.client = client;
    }
    public void splash() {
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    splashScreen.getScene().getWindow().hide();
                } catch (Exception e){
                    System.out.println(e);
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        splashScreen.getScene().getWindow().hide();
                        GameScreenController.setClient(client);
                        GameScreenController.setScreen_chatUI(client);
                    }
                });
            }
        }.start();
    }
}


