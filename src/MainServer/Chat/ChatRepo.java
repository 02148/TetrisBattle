package MainServer.Chat;

import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

public class ChatRepo {
    Space s;

    public ChatRepo(){this.s = new SequentialSpace();}

    public void insertChatMessage(ChatMessage m) throws InterruptedException{
        s.put( m.UUID, m.message, m.timeStamp);
    }

    public ChatMessage getChatMessage() throws InterruptedException {
        Object[] q = s.getp(new FormalField(String.class),
                new FormalField(String.class),
                new FormalField(String.class),
                new FormalField(Double.class));
        return new ChatMessage(q);
    }

    public void create(String UUID, String message) throws InterruptedException {
        ChatMessage c = new ChatMessage(UUID, message);
        insertChatMessage(c);
    }
}
