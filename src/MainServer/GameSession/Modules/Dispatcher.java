package MainServer.GameSession.Modules;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.BitSet;
import java.util.HashMap;

public class Dispatcher  implements Runnable {
    Space in, delta, full, conns;
    int T;
    HashMap<String, Double> lastTimestamps;

    public Dispatcher(Space in,
                      Space delta,
                      Space full,
                      Space conns) {
        this.in = in;
        this.delta = delta;
        this.full = full;
        this.conns = conns;
        this.T = 10;
        this.lastTimestamps = new HashMap<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object[] raw_data = in.get(
                        new FormalField(String.class),
                        new FormalField(Double.class),
                        new FormalField(String.class),
                        new FormalField(Object.class)
                );
                if (raw_data == null)
                    continue;

                String packageType = (String)raw_data[0];
                double timestamp = (double)raw_data[1];
                String userUUID = (String)raw_data[2];
                Object packageData = raw_data[3];

                if (lastTimestamps.containsKey(userUUID)) {
                    if (lastTimestamps.get(userUUID) > timestamp) { // retrieved entry for user is older than last in pipeline
                        System.out.println("DISPATCHER@" + Thread.currentThread() + " >> Old data retrieved, dropping");
                        continue;
                    }
                }

//                    System.out.println(userUUID + timestamp + packageData);

                if (packageType.equals("full"))
                    this.full.put(userUUID, timestamp, packageData);
                if (packageType.equals("delta"))
                    this.delta.put(userUUID, timestamp, packageData);

                lastTimestamps.put(userUUID, timestamp);


            } catch (InterruptedException e) {
                System.out.println("DISPATCHER@" + Thread.currentThread() + " Exception");
                e.printStackTrace();
            }
        }
    }
}