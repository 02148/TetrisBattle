package Client;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.IOException;

import javafx.scene.control.TextArea;

public class ChatListener implements Runnable {
    public Client client;
    public TextArea chatArea;
    public RemoteSpace chatSpace;
    public RemoteSpace serverToUser;
    public boolean stop = false;

    public ChatListener(TextArea chat, String roomUUID) {
        this.chatArea = chat;
        try {
            System.out.println("URI for chat: " + "tcp://localhost:6971/" + roomUUID +"?conn");
            chatSpace = new RemoteSpace("tcp://localhost:6971/" + roomUUID +"?conn");
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
                Thread.sleep(1000);
                Object[] chatInput = new Object[4];
                String[] message = new String[4];

                chatSpace.get(new ActualField("reader Lock"));
                System.out.println("Got reader lock");
                Object[] readers = chatSpace.get(new ActualField("readers"), new FormalField(Integer.class));
                chatSpace.put("readers", (Integer) readers[1] + 1 );
                if((Integer) readers[1] == 0){
                    chatSpace.get(new ActualField("global Lock"));
                    System.out.println("Got global lock");
                }
                chatSpace.put("reader Lock");
                if((Integer) readers[1] == 0){
                    chatInput = chatSpace.get(new FormalField(String.class),
                            new ActualField("recived"),
                            new FormalField(String.class),
                            new FormalField(Double.class),
                            new FormalField(String.class));
                } else {
                    chatInput = chatSpace.query(new FormalField(String.class), new ActualField("recived"), new FormalField(String.class), new FormalField(Double.class),new FormalField(String.class));
                }

                chatSpace.get(new ActualField("reader Lock"));
                readers = chatSpace.get(new ActualField("readers"), new FormalField(Integer.class));
                chatSpace.put("readers", (Integer) readers[1]-1);
                if ((Integer) readers[1] == 1){
                    chatSpace.put("global Lock");
                }
                chatSpace.put("reader Lock");


                if (chatInput != null) {
                    System.out.println("Got message from another client");
                    message[0] = (String) chatInput[0]; //UUID
                    message[1] = (String) chatInput[2]; //Message
                    message[2] = Double.toString((Double) chatInput[3]); //Timestamp
                    message[3] = (String) chatInput[4];
                    String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date ((long) ((double) chatInput[3]) *1000));
                    chatArea.appendText("\n" +  date + " " + message[3] + ": " + message[1]);
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
        System.out.println("GlobalChatListener Stopped Running");


    }
}
