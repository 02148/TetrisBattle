package Client.UI;

import Client.Client;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

import java.io.IOException;

public class GameScreenController {
    private static Client client;
    private boolean closeGame = false;

    private static GlobalChatUIController chatUI_controller;

    public static void setChatUI_controller(GlobalChatUIController chatUI_controller_get) {
        chatUI_controller = chatUI_controller_get;
    }

    public static void setClient(Client client_get) {
        client = client_get;
    }

    public static void setScreen_chatUI() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(GameScreenController.class.getResource("GlobalChatUI.fxml"));
            SplitPane splitPane = fxmlLoader.load();

            Scene scene = new Scene(splitPane, 600, 400);
            Stage primaryStage = new Stage();
            primaryStage.setScene(scene);

            primaryStage.centerOnScreen();
            //Get the controller and add client
            chatUI_controller = fxmlLoader.getController();
            chatUI_controller.setClient(client);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
