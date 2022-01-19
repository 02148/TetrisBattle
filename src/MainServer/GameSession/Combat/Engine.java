package MainServer.GameSession.Combat;

import org.jspace.FormalField;
import org.jspace.Space;

import java.util.HashMap;
import java.util.Random;

public class Engine implements Runnable {
    Random rnd;
    Space conns, combatSpace;
    HashMap<Integer, Integer> linesSentStats; // <senderIdx, noOfLines>
    HashMap<Integer, Integer> linesReceivedStats; // <receiverIdx, noOfLines>


    public Engine(Space conns, Space combatSpace) {
        this.rnd = new Random();
        this.linesSentStats = new HashMap<>();
        this.conns = conns;
        this.combatSpace = combatSpace;
    }

    /**
     * Selects random player currently in the session.
     * TODO Should take into account linesSentStats to ensure a uniform distribution.
     * @return index of player: integer
     */
    public int getRandomPlayerIndex() {
        int idx = -1;
        try {
            var noOfPlayers = (long) this.conns
                    .getAll(new FormalField(String.class))
                    .size();

            idx = (int) (rnd.nextInt() * noOfPlayers);
        } catch (Exception e) { e.printStackTrace();}

        return idx;
    }

    public AttackObject receiveAttack() {
        AttackObject attackObj = null;
        try {
            attackObj = (AttackObject)this.combatSpace.get(new FormalField(Object.class))[0];
            this.linesSentStats.put(attackObj.getSenderIdx(), attackObj.getLinesSent());
        } catch (InterruptedException e) { e.printStackTrace(); }

        return attackObj;
    }

    public void sendAttack(AttackObject attackObj) {
        int idx = getRandomPlayerIndex();

        // default of -1 means that no target player is selected -> server chooses random target
        if (attackObj.getReceiverIdx() != -1)
            idx = attackObj.getReceiverIdx();

        try {
            this.combatSpace.put(idx, attackObj.getLinesSent());
            this.linesReceivedStats.put(idx, attackObj.getLinesSent());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }
}
