package MainServer.GameSession.Combat;

import java.io.Serializable;

public class AttackObject implements Serializable {
    int linesSent;
    String senderUUID, receiverUUID;

    public int getLinesSent() {
        return linesSent;
    }

    public String getSenderUUID() {
        return senderUUID;
    }

    public String getReceiverUUID() {
        return receiverUUID;
    }

    public AttackObject(int linesSent, String senderUUID, String receiverUUID) {
        this.linesSent = linesSent;
        this.senderUUID = senderUUID;
        this.receiverUUID = receiverUUID;
    }
}
