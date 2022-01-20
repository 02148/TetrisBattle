package MainServer.GameSession.Combat;

import org.jspace.ActualField;
import common.Constants;
import org.jspace.FormalField;
import org.jspace.Space;
import org.jspace.SpaceRepository;
import org.jspace.StackSpace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;

public class CombatEngine implements Runnable {
    Random rnd;
    SpaceRepository combatRepository;
    Space conns, combatSpace;
    HashMap<String, Integer> linesSentStats;     // <senderUUID, noOfLines>
    HashMap<String, Integer> linesReceivedStats; // <receiverUUID, noOfLines>
    HashMap<String, Integer> tetrisStreaks;      // <senderUUID, no of back-2-back tetrises>
    String gameUUID;
    boolean stop = false;

    public CombatEngine(Space conns, SpaceRepository combatRepo, String gameUUID) {
        this.gameUUID = gameUUID;
        this.combatRepository = combatRepo;
        this.rnd = new Random();
        this.linesSentStats = new HashMap<>();
        this.linesReceivedStats = new HashMap<>();
        this.tetrisStreaks = new HashMap<>();
        this.conns = conns;

        // Creates the space in which the users sends lines
        this.combatSpace = new StackSpace();
        this.combatRepository.add(this.gameUUID, this.combatSpace);

        this.combatRepository.addGate("tcp://" + Constants.IP_address+ ":42069/?keep");
    }

    /**
     * Selects random player currently in the session.
     * TODO Should take into account linesSentStats to ensure a uniform distribution.
     * @return index of player: integer
     */
    public String getRandomPlayerUUID(String senderUUID) {
        int idx = -1;
        ArrayList<String> curPlayers = null;
        try {
            curPlayers = (ArrayList<String>) this.conns
                    .queryAll(new FormalField(String.class))
                    .stream()
                    .map(x -> (String)x[0])
                    .filter(x -> !x.equals(senderUUID))
                    .collect(Collectors.toList());

            idx = (int) (Math.random() * curPlayers.size());
        } catch (Exception e) { e.printStackTrace();}

        if (curPlayers == null)
            return null;

        return curPlayers.get(idx);
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
     * @param senderUUID Index of sender
     * @param noOfLinesIn Number of lines from sender
     * @return Number of lines to receiver
     */
    public int computeAttackSize(String senderUUID, int noOfLinesIn) {
        boolean isTetris = noOfLinesIn == 4;

        int noOfLinesOut = noOfLinesIn - 1;

        if (isTetris) {
            if (!this.tetrisStreaks.containsKey(senderUUID))
                this.tetrisStreaks.put(senderUUID,0);
            int curStreak = this.tetrisStreaks.get(senderUUID) + 1;
            noOfLinesOut = curStreak == 1 ? 4 : (curStreak == 2 ? 5 : 8);
            this.tetrisStreaks.put(senderUUID, curStreak);
        }
        else
            this.tetrisStreaks.put(senderUUID,0);

        return noOfLinesOut;
    }

    public AttackObject receiveAttack() {
        AttackObject attackObj = null;
        try {
            attackObj = (AttackObject)this.combatSpace.get(new ActualField("outgoing"), new FormalField(Object.class))[1];
            this.linesSentStats.put(attackObj.getSenderUUID(), attackObj.getLinesSent());
        } catch (InterruptedException e) { e.printStackTrace(); }

        return attackObj;
    }

    public void sendAttack(AttackObject attackObj) {
        // target is chosen at random initially
        String uuid = getRandomPlayerUUID(attackObj.senderUUID);

        // none is default, and means no specific target
        if (!attackObj.getReceiverUUID().equals("none"))
            uuid = attackObj.getReceiverUUID();

        int linesToSend = computeAttackSize(uuid, attackObj.getLinesSent());

        // if 0 are computed (if 1 line is sent), simply stop execution before sending
        if (linesToSend == 0)
            return;

        try {
            this.combatSpace.put("incoming", uuid, linesToSend);
            this.linesReceivedStats.put(uuid, linesToSend);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!stop) {
            var incoming = receiveAttack();
            sendAttack(incoming);
        }
    }


    public void deleteCombatEngine() {
        this.combatRepository.remove(this.gameUUID);
        this.stop = true;
    }
}
