package Client.UI;

import Client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

import java.io.IOException;

public class GameScreenController {
    private static Client client;
    private static boolean initializeChatUI = true;
    private boolean closeGame = false;
    private static Scene chatUIScene;
    private static Scene gameUIScene;

    private static GlobalChatUIController chatUI_controller;
    private static GlobalGameUIController gameUI_controller;

    public static void setChatUI_controller(GlobalChatUIController chatUI_controller_get) {
        chatUI_controller = chatUI_controller_get;
    }

    public static void setClient(Client client_get) {
        client = client_get;
    }

    public static void hideScreen_chatUI() {
        chatUIScene.getWindow().hide();
    }

    public static void hideScreen_gameUI() {
        gameUIScene.getWindow().hide();
    }

    public static void setScreen_chatUI(Client client_get) {
        client = client_get;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(GameScreenController.class.getResource("GlobalChatUI.fxml"));

            SplitPane splitPane = fxmlLoader.load();
            chatUIScene = new Scene(splitPane, 600, 400);
            Stage primaryStage = new Stage();
            primaryStage.setScene(chatUIScene);

            primaryStage.centerOnScreen();

            //Get the controller and add client
            chatUI_controller = fxmlLoader.getController();
            chatUI_controller.setClient(client);
            System.out.println(client.serverToUser.getUri());

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setScreen_gameUI(ActionEvent event, Client client_get) {
        client = client_get;
        try {
            FXMLLoader loader = new FXMLLoader(GameScreenController.class.getResource("GlobalGame.fxml"));
            Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
            gameUIScene = new Scene(loader.load(),1300,800);

            gameUI_controller = loader.getController();
            gameUI_controller.setUpGlobalGameController(client);

            primaryStage.setScene(gameUIScene);
            primaryStage.centerOnScreen();
            primaryStage.show();
            client.sendChat(Client.userName + " joined the room");


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
