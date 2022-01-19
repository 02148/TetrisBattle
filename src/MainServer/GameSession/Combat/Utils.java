package MainServer.GameSession.Combat;

import org.jspace.FormalField;
import org.jspace.Space;

import java.util.HashMap;
import java.util.Random;

public class Utils {
    Random rnd;
    Space conns;
    HashMap<Integer, Integer> linesSentStats;

    public Utils(Space conns) {
        this.rnd = new Random();
        this.linesSentStats = new HashMap<>();
        this.conns = conns;
    }
}
