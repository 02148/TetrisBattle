package MainServer.Chat;

import MainServer.GameRoom.GameRoomRepo;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

//Deletes old messages in Space
public class ChatRoomListener implements Runnable {
    private GameRoomRepo rooms;
    private SpaceRepository chatChannels;
    private SequentialSpace chat;
    private String roomUUID;

    public void setRooms(GameRoomRepo rooms) {
        this.rooms = rooms;
    }

    public void setChat(SpaceRepository chatChannels) {
        this.chatChannels = chatChannels;
        this.chat = new SequentialSpace();
        this.chatChannels.add(roomUUID, chat);
    }

    public ChatRoomListener(String roomUUID) {
        this.roomUUID = roomUUID;
    }

    Object[] messageInput = new Object[4];

    public void run() {
        try {
            while (rooms.exists(roomUUID)) {
                messageInput = chat.query(new FormalField(String.class),
                        new ActualField(roomUUID),
                        new FormalField(String.class),
                        new FormalField(Double.class),
                        new FormalField(String.class));
                System.out.println("got new message: " + messageInput[4]);
                for (int i = 0; i < rooms.queryConnections(roomUUID).size(); i++) {
                    chat.get(new ActualField(messageInput[0]), new ActualField(messageInput[3]));
                    System.out.println("Got read token from client");
                }
                chat.get(new FormalField(String.class),
                        new ActualField(roomUUID),
                        new FormalField(String.class),
                        new ActualField(messageInput[3]),
                        new FormalField(String.class));
                System.out.println("Deleted chatMessage in room " + roomUUID);
            }
            chatChannels.remove(roomUUID);
            System.out.println(roomUUID + " chatChannel was deleted");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
