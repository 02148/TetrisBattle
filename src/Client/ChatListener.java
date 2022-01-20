package Client;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.IOException;
import java.util.UUID;

import javafx.scene.control.TextArea;

public class ChatListener implements Runnable {
    public Client client;
    public TextArea chatArea;
    public RemoteSpace chatSpace;
    public RemoteSpace serverToUser;
    private Double lastMessageTime = 0.0;
    private String lastSender = "";
    public boolean stop = false;

    public ChatListener(TextArea chat) {
        this.chatArea = chat;
    }


    public void setClient(Client client) {
        this.client = client;
        chatSpace = client.chatSpace;
    }


    @Override
    public void run() {

        while (!stop) {
            System.out.println("chatListener is running on " + client.roomUUID);
            try {
                Object[] chatInput = new Object[4];
                String[] message = new String[3];


                chatInput = chatSpace.query(new FormalField(String.class),
                        new ActualField(client.roomUUID),
                        new FormalField(String.class),
                        new FormalField(Double.class),
                        new FormalField(String.class));

                if (chatInput != null && (!(chatInput[0].equals(lastSender)) || (Double) chatInput[3] > lastMessageTime)) {
                    lastMessageTime = (Double) chatInput[3];
                    lastSender = (String) chatInput[0];
                    System.out.println("Got message from another client");
                    //Send read token to server room listener
                    chatSpace.put(chatInput[0],chatInput[3]);
                    System.out.println("Send read token to chat space " + chatSpace.getUri());

                    message[0] = (String) chatInput[4]; //Message
                    message[1] = Double.toString((Double) chatInput[3]); //Timestamp
                    message[2] = (String) chatInput[2]; //Username
                    String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date ((long) ((double) chatInput[3]) *1000));
                    chatArea.appendText("\n" +  date + " " + message[2] + ": " + message[0]);
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
        System.out.println("ChatListener is not running");


    }
}
