package Client.UI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class GlobalChatUIController {


    @FXML protected void handleExitButtonAction(ActionEvent event) {
        //TODO: Add exit function
        Platform.exit();
        System.exit(0);
    }
    @FXML protected void handleGoToLobbyButtonAction(ActionEvent event) {
        //TODO: Add go to lobby function
    }
}
