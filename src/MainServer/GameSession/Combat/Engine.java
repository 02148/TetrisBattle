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
    HashMap<Integer, Integer> tetrisStreaks; // <senderIdx, no of back-2-back tetrises>


    public Engine(Space conns, Space combatSpace) {
        this.rnd = new Random();
        this.linesSentStats = new HashMap<>();
        this.linesReceivedStats = new HashMap<>();
        this.tetrisStreaks = new HashMap<>();
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

    /**
     * Computes number of lines to send to receiver depending on the number of lines from sender.
     * Maps lines received to lines sent with following logic:
     * 1 -> 0.
     * 2 -> 1.
     * 3 -> 2.
     * Tetris, no streak -> 4.
     * Tetris, streak of 1 -> 5.
     * Tetris, streak of >2 -> 8.
     * @param senderIdx Index of sender
     * @param noOfLinesIn Number of lines from sender
     * @return Number of lines to receiver
     */
    public int computeAttackSize(int senderIdx, int noOfLinesIn) {
        boolean isTetris = noOfLinesIn == 4;

        int noOfLinesOut = noOfLinesIn - 1;

        if (isTetris) {
            int curStreak = this.tetrisStreaks.get(senderIdx) + 1;
            noOfLinesOut = curStreak == 1 ? 4 : (curStreak == 2 ? 5 : 8);
            this.tetrisStreaks.put(senderIdx, curStreak);
        }
        else
            this.tetrisStreaks.put(senderIdx,0);

        return noOfLinesOut;
    }

    public AttackObject receiveAttack() {
        AttackObject attackObj = null;
        try {
            attackObj = (AttackObject)this.combatSpace.get(new FormalField(AttackObject.class))[0];
            this.linesSentStats.put(attackObj.getSenderIdx(), attackObj.getLinesSent());
        } catch (InterruptedException e) { e.printStackTrace(); }

        return attackObj;
    }

    public void sendAttack(AttackObject attackObj) {
        int idx = getRandomPlayerIndex();

        // default of -1 means that no target player is selected -> server chooses random target
        if (attackObj.getReceiverIdx() != -1)
            idx = attackObj.getReceiverIdx();

        int linesToSend = computeAttackSize(idx, attackObj.getLinesSent());

        // if 0 are computed (if 1 line is sent), simply stop execution before sending
        if (linesToSend == 0)
            return;

        try {
            this.combatSpace.put(idx, linesToSend);
            this.linesReceivedStats.put(idx, linesToSend);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            var incoming = receiveAttack();
            sendAttack(incoming);
        }
    }
}
