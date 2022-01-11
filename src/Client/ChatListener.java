package Client;

import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.awt.*;
import javafx.scene.control.TextArea;

public class ChatListener implements Runnable {
    public RemoteSpace serverToUser;
    public Client client;
    public TextArea globalChatArea;

    public ChatListener(TextArea chat) {
        this.globalChatArea = chat;
        chat.appendText("\n" + "Testing if connecting is working ");

    }


    public void setServerToUser(RemoteSpace serverToUser) {
        this.serverToUser = serverToUser;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        System.out.println("GlobalChatListener started running..");

        while (!client.isGameActive) {
            try {
                Thread.sleep(1000);
                Object[] globalChatInput = new Object[2];
                String[] message = new String[3];

                globalChatInput = serverToUser.queryp(new FormalField(String.class), new FormalField(String.class), new FormalField(String.class), new FormalField(Double.class));
                //System.out.println("Not getting any chat messages");
                if (globalChatInput != null) {
                    System.out.println("Got message from another client");
                    message[0] = (String) globalChatInput[0];
                    message[1] = (String) globalChatInput[2];
                    message[2] = Double.toString((Double) globalChatInput[3]);

                    globalChatArea.appendText("Time: " + message[3] + "User: " + message[0] + "Message: " + message[2]);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
        System.out.println("GlobalChatListener Stopped Running");


    }
}
