package Client.UI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class GlobalChatUIController {
    @FXML private TextArea chatArea;
    @FXML private TextField chatTextField;
    private String user = "Username1";



    @FXML protected void handleExitButtonAction(ActionEvent event) {
        //TODO: Add exit function
        Platform.exit();
        System.exit(0);
    }
    @FXML protected void handleGoToLobbyButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GlobalGame.fxml"));
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(loader.load(),1300,800);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();//TODO: Add go to lobby function
    }

    @FXML protected void handleChatInputAction(ActionEvent event){
        //TODO: add functionality to add text to textview
        chatArea.appendText("\n"+ user + ": " + chatTextField.getText() );
        chatTextField.clear();
    }
}
