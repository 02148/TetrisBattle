package MainServer.Chat;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

import java.util.HashMap;
import java.util.List;

public class ChatRepo {
    public Space globalChat;
    public HashMap<String,Space> chatRepository = new HashMap<>();

    public ChatRepo() throws InterruptedException {
        this.globalChat = new SequentialSpace();
        globalChat.put(new ActualField("global Lock"));
        globalChat.put(new ActualField("reader Lock"));
        globalChat.put(new ActualField("readers"), new ActualField(1));

        chatRepository.put("globalChat",globalChat);
    }

    public void insertChatMessage(ChatMessage m,String roomUUID) throws InterruptedException{
        chatRepository.get(roomUUID).put(m.UUID, m.message, m.timeStamp);
        System.out.println("MESSAGE WAS INSERTED");
    }

    public ChatMessage getChatMessage(String roomUUID) throws InterruptedException {
        Object[] q = chatRepository.get(roomUUID).getp(new FormalField(String.class),
                new FormalField(String.class),
                new FormalField(Double.class));
        return new ChatMessage(q);
    }
    public ChatMessage queryChatMessage(String uuid, String roomUUID) throws InterruptedException{
        var m = chatRepository.get(roomUUID).queryp( new ActualField(uuid),
                new FormalField(String.class),
                new FormalField(Double.class));
        return new ChatMessage(m);
    }

    public Space createChatRoom(String roomUUID){
        Space chatRoom = null;
        if(chatRepository.get(roomUUID) == null){
            chatRoom = new SequentialSpace();
            chatRepository.put(roomUUID,chatRoom);
        }
        return chatRoom;
    }

    public ChatMessage createMessage(String UUID, String message, String roomUUID) throws InterruptedException {
        ChatMessage c = new ChatMessage(UUID, message);
        insertChatMessage(c,roomUUID);
        return c;
    }

    public List<Object[]> queryAllMessages(String roomUUID) throws InterruptedException{
        var allMessages = chatRepository.get(roomUUID).queryAll(new FormalField(String.class),
                new FormalField(String.class),
                new FormalField(Double.class)
        );
        return allMessages;
    }
}
