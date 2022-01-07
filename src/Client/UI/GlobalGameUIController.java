package Client.UI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class GlobalGameUIController {
    @FXML private TextArea gameChatArea;
    @FXML private TextField gameChatTextField;
    private String user = "Username1";

    @FXML
    protected void handleLeaveGameAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GlobalChatUI.fxml"));
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();


    }
    @FXML protected void  handleGameChatInputAction(ActionEvent event){
        gameChatArea.appendText("\n"+ user + ": " + gameChatTextField.getText() );
        gameChatTextField.clear();
    }
}
