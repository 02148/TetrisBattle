package MainServer.GameSession.Modules;

import MainServer.Utils;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.BitSet;
import java.util.HashMap;

public class Transformer2 implements Runnable {
    Space in, shared, conns;
    String spaceKey;
    int noConns, T = 10;
    HashMap<String, Double> lastTimestamp; // maps each UUID to last received timestamp, to ensure chronological data receival

    public Transformer2(Space in, Space shared, Space conns, String spaceKey) {
        this.in = in;
        this.shared = shared;
        this.conns = conns;
        this.spaceKey = spaceKey;
        this.lastTimestamp = new HashMap<>();
        this.T = 20; //spaceKey.equals("full") ? 1000 : 20; // if full sync, send once a second, else 500 times a second
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(T);
            } catch (InterruptedException ignored) {}

            try {
                var curConns = conns.queryAll(new FormalField(String.class));
                noConns = curConns.size();
                if (noConns == 0) {
                    System.out.println("Transformer2@"+ Thread.currentThread().getId() + " finished");
                    Thread.currentThread().join(1);
                }

                HashMap<String, Object[]> allBoards = new HashMap<>();
                for (var c : curConns) {
                    Object[] raw_data = in.getp(
                            new ActualField(this.spaceKey),
                            new FormalField(Double.class),
                            new ActualField((String)c[0]),
                            new FormalField(BitSet.class)
                    );
                    if (raw_data == null)
                        continue;

                    String UUID = (String) raw_data[2];
                    double curTimestamp = (double) raw_data[1];
                    if (lastTimestamp.containsKey(UUID)) {
                        if (lastTimestamp.get(UUID) > curTimestamp) { // retrieved entry for user is older than last in pipeline
                            System.out.println("CONS@" + this.spaceKey + " >> Old data retrieved, dropping");
                            continue;
                        }
                    }

                    lastTimestamp.put(UUID, curTimestamp);

                    allBoards.put((String) UUID, raw_data);
                }
                shared.put(
                        this.spaceKey,
                        Utils.getCurrentExactTimestamp(),
                        allBoards
                );


            } catch (Exception e) {
                System.out.println("Cons exception");
                e.printStackTrace();
            }

        }
    }
}
