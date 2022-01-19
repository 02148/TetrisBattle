package MainServer.GameSession.Combat;

import java.io.Serializable;

public class AttackObject implements Serializable {
    int linesSent, senderIdx, receiverIdx;

    public int getLinesSent() {
        return linesSent;
    }

    public int getSenderIdx() {
        return senderIdx;
    }

    public int getReceiverIdx() {
        return receiverIdx;
    }

    public AttackObject(int linesSent, int senderIdx, int receiverIdx) {
        this.linesSent = linesSent;
        this.senderIdx = senderIdx;
        this.receiverIdx = receiverIdx;
    }
}
