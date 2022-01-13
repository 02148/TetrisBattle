package Client;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.IOException;

import javafx.scene.control.TextArea;

public class ChatListener implements Runnable {
    public Client client;
    public TextArea chatArea;
    public RemoteSpace personalChatSpace;
    public RemoteSpace serverToUser;
    public boolean stop = true;

    public ChatListener(TextArea chat, String UUID) {
        this.chatArea = chat;
        try {
            System.out.println("URI for chat: " + "tcp://localhost:6971/" + UUID +"?conn");
            personalChatSpace = new RemoteSpace("tcp://localhost:6971/" + UUID +"?conn");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void setClient(Client client) {
        this.client = client;
    }


    @Override
    public void run() {

        while (!stop) {
            System.out.println("chatListener is running..");
            try {
                Thread.sleep(1000);
                Object[] chatInput = new Object[4];
                String[] message = new String[3];


                chatInput = personalChatSpace.get(new FormalField(String.class),
                        new FormalField(Double.class),
                        new FormalField(String.class)
                );


                if (chatInput != null) {
                    System.out.println("Got message from another client");
                    message[0] = (String) chatInput[0]; //Message
                    message[1] = Double.toString((Double) chatInput[1]); //Timestamp
                    message[2] = (String) chatInput[2]; //Username
                    String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date ((long) ((double) chatInput[1]) *1000));
                    chatArea.appendText("\n" +  date + " " + message[2] + ": " + message[0]);
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
        System.out.println("GlobalChatListener is not running");


    }
}
