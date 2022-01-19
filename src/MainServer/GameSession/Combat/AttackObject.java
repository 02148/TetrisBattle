package MainServer.GameSession.Combat;

public class AttackObject {
    int linesSent, senderIdx, receiverIdx;

    public AttackObject(int linesSent, int senderIdx, int receiverIdx) {
        this.linesSent = linesSent;
        this.senderIdx = senderIdx;
        this.receiverIdx = receiverIdx;
    }
}
