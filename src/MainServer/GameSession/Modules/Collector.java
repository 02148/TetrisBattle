package MainServer.GameSession.Modules;

import org.jspace.FormalField;
import org.jspace.Space;

public class Collector implements Runnable {
    Space deltaIn, fullIn, out;
    boolean stop = false;

    public Collector(Space deltaIn, Space fullIn, Space out) {
        this.deltaIn = deltaIn;
        this.fullIn = fullIn;
        this.out = out;
    }

    @Override
    public void run() {
        while (!this.stop) {
            try {
                // (HashMap<String,BitSet>)packageData
                var fullPkg = this.fullIn.get(
                        new FormalField(Object.class)
                );

                // userUUID, timestamp, (HashMap<Integer,Integer>)packageData
                var deltaPkg = this.deltaIn.get(
                        new FormalField(String.class),
                        new FormalField(Double.class),
                        new FormalField(Object.class));

                if (deltaPkg != null) {
                    this.out.put(deltaPkg);
                    if (fullPkg != null)
                        this.out.put(fullPkg); // :)
                }
            } catch (InterruptedException e) {
                System.out.println("COLLECTOR EXCEPTION >> ");
                e.printStackTrace();
            }
        }
    }

    public void stopThread() {
        this.stop = true;
    }
}
