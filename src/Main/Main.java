package Main;


import Client.Client;
import Client.UI.GlobalChatUIController;

import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;


import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    static Client currClient = new Client();


    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("../Client/UI/GlobalChatUI.fxml"));
            Parent root = fxmlLoader.load();

            //Get the controller and add client + main server
            GlobalChatUIController globalChatUIController = fxmlLoader.getController();
            globalChatUIController.setClient(currClient);

            Scene scene = new Scene(root,600,400);
            primaryStage.setScene(scene);
            //primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }

        primaryStage.show();


    }

    public static void main(String[] args) throws IOException {
        currClient.main(args);
        launch(args);

    }
}
