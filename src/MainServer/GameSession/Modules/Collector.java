package MainServer.GameSession.Modules;

import org.jspace.FormalField;
import org.jspace.Space;

public class Collector implements Runnable {
    Space deltaIn, fullIn, out;

    public Collector(Space deltaIn, Space fullIn, Space out) {
        this.deltaIn = deltaIn;
        this.fullIn = fullIn;
        this.out = out;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // userUUID, timestamp, (HashMap<Integer,Integer>)packageData
                var deltaPkg = this.deltaIn.getp(
                        new FormalField(String.class),
                        new FormalField(Double.class),
                        new FormalField(Object.class));
                // userUUID, timestamp, (BitSet)packageData
                var fullPkg = this.fullIn.getp(
                        new FormalField(String.class),
                        new FormalField(Double.class),
                        new FormalField(Object.class)
                );

                if (deltaPkg != null)
                    this.out.put(deltaPkg[0],
                            deltaPkg[1],
                            deltaPkg[2]);
                if (fullPkg != null)
                    this.out.put(deltaPkg[0],
                            fullPkg[1],
                            fullPkg[2]);
            } catch (InterruptedException e) {
                System.out.println("COLLECTOR EXCEPTION >> ");
                e.printStackTrace();
            }
        }
    }
}
