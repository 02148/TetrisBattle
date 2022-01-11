package MainServer.Chat;

import MainServer.UserMgmt.User;
import MainServer.Utils;

public class ChatMessage {
    public String UUID, message;
    double timeStamp;



    public ChatMessage(Object[] q){
        this(
            (String)q[0],
            (String)q[1],
            (double)q[2]
        );
    }
    public ChatMessage( String UUID, String message){
        this.UUID = UUID;
        this.message = message;
        this.timeStamp = Utils.getCurrentTimestamp();
    }

    public ChatMessage(String UUID, String message, double timeStamp){
        this.UUID = UUID;
        this.message = message;
        this.timeStamp = timeStamp;
    }
}
