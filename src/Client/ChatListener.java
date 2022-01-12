package Client;

import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.IOException;

import javafx.scene.control.TextArea;

public class ChatListener implements Runnable {
    public Client client;
    public TextArea chatArea;
    public RemoteSpace chatSpace;
    public boolean stop = false;

    public ChatListener(TextArea chat, String roomUUID) {
        this.chatArea = chat;
        try {
            System.out.println("URI for chat: " + "tcp://localhost:6969/" + roomUUID +"?conn");
            chatSpace = new RemoteSpace("tcp://localhost:6969/" + roomUUID +"?conn");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        System.out.println("chatListener started running..");
        while (!stop) {
            try {
                Object[] chatInput = new Object[3];
                String[] message = new String[3];

                chatInput = chatSpace.getp(new FormalField(String.class), new FormalField(String.class), new FormalField(Double.class));
                if (chatInput != null) {
                    System.out.println("Got message from another client");
                    message[0] = (String) chatInput[0]; //UUID
                    message[1] = (String) chatInput[1]; //Message
                    message[2] = Double.toString((Double) chatInput[2]); //Timestamp
                    String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date ((long) ((double) chatInput[2]) *1000));
                    chatArea.appendText("\n" +  date + " " + client.getUserName()+ ": " + message[1]);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
        System.out.println("GlobalChatListener Stopped Running");


    }
}
