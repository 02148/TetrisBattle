package MainServer.Chat;

import MainServer.UserMgmt.User;
import MainServer.Utils;

public class ChatMessage {
    public String username, UUID, message;
    double timeStamp;



    public ChatMessage(Object[] q){
        this(
            (String)q[0],
            (String)q[1],
            (String)q[2],
            (double)q[3]
        );
    }
    public ChatMessage(String username, String UUID, String message){
        this.username = username;
        this.UUID = UUID;
        this.message = message;
        this.timeStamp = Utils.getCurrentTimestamp();
    }

    public ChatMessage(String username, String UUID, String message, double timeStamp){
        this.username = username;
        this.UUID = UUID;
        this.message = message;
        this.timeStamp = timeStamp;
    }
}
