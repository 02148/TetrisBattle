package MainServer.GameSession;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.BitSet;

public class Producer implements Runnable {
    Space shared, out, conns;
    int T = 2;

    public Producer(Space shared, Space out, Space conns) {
        this.shared = shared;
        this.out = out;
        this.conns = conns;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(T);
            } catch (InterruptedException ignored) {}
            try {
                Object[] raw_data = shared.getp(
                        new FormalField(Double.class),
                        new FormalField(String.class),
                        new FormalField(BitSet.class)
                );
                if (raw_data == null) {
//                    System.out.println("PROD - sessData null");
                    continue;
                }
                out.put(
                        (double)raw_data[0],
                        (String)raw_data[1],
                        (BitSet)raw_data[2]
                );

                System.out.println(
                        (double)raw_data[0] + " - " +
                        (String)raw_data[1] + " - " +
                        (BitSet)raw_data[2]
                );

            } catch (Exception ignored) {
                System.out.println("Prod exception");
                ignored.printStackTrace();
            }
        }
//        System.out.println("Producer@"+ Thread.currentThread().getId() + " finished");
    }
}
