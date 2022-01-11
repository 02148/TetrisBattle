package MainServer.GameSession.Modules;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.BitSet;
import java.util.HashMap;

public class Dispatcher  implements Runnable {
    Space in, delta, full, conns;
    int noConns, T;
    HashMap<String, Double> lastTimestamps;

    public Dispatcher(Space in,
                      Space delta,
                      Space full,
                      Space conns,
                      HashMap<String, Double> lastTimestamps) {
        this.in = in;
        this.delta = delta;
        this.full = full;
        this.conns = conns;
        this.noConns = 0;
        this.T = 1;
        this.lastTimestamps = lastTimestamps;
    }

    @Override
    public void run() {
        var allBoards = new HashMap<String, Object[]>();
        while (true) {
            try {
                var curConns = conns.queryAll(new FormalField(String.class));
                this.noConns = curConns.size();

                allBoards.clear();
                for (var c : curConns) {
                    Object[] raw_data = in.getp(
                            new FormalField(String.class),
                            new FormalField(Double.class),
                            new ActualField((String)c[0]),
                            new FormalField(Object.class)
                    );
                    if (raw_data == null)
                        continue;

                    String packageType = (String)raw_data[0];
                    double timestamp = (double)raw_data[1];
                    String UUID = (String)raw_data[2];
                    Object packageData = raw_data[3];

                    if (lastTimestamps.containsKey(UUID)) {
                        if (lastTimestamps.get(UUID) > timestamp) { // retrieved entry for user is older than last in pipeline
                            System.out.println("DISPATCHER@" + Thread.currentThread() + " >> Old data retrieved, dropping");
                            continue;
                        }
                    }

                    if (packageType.equals("full"))
                        this.full.put(UUID, timestamp, (BitSet)packageData);
                    if (packageType.equals("delta"))
                        this.delta.put(UUID, timestamp, (HashMap<Integer,Integer>)packageData);

                    lastTimestamps.put(UUID, timestamp);
                }


            } catch (InterruptedException e) {
                System.out.println("DISPATCHER@" + Thread.currentThread() + " Exception");
                e.printStackTrace();
            }
        }
    }
}