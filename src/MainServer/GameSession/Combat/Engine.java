package MainServer.GameSession.Combat;

import org.jspace.FormalField;
import org.jspace.Space;

import java.util.HashMap;
import java.util.Random;

public class Engine implements Runnable {
    Random rnd;
    Space conns;
    HashMap<Integer, Integer> linesSentStats;


    public Engine(Space conns) {
        this.rnd = new Random();
        this.linesSentStats = new HashMap<>();
        this.conns = conns;
    }

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




    @Override
    public void run() {

    }
}
