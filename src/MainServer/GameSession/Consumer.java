package MainServer.GameSession;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.BitSet;

public class Consumer implements Runnable {
    Space in, shared, conns;
    int T = 10;

    public Consumer(Space in, Space shared, Space conns) {
        this.in = in;
        this.shared = shared;
        this.conns = conns;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(T);
            } catch (InterruptedException ignored) {}

            try {
                Object[] raw_data = in.getp(
                        new FormalField(Double.class),
                        new FormalField(String.class),
                        new FormalField(BitSet.class)
                );
                if (raw_data == null) {
                    System.out.println("CONS - sessData null");
                    continue;
                }

                shared.put(
                        (double)raw_data[0],
                        (String)raw_data[1],
                        (BitSet)raw_data[2]
                );
            } catch (Exception ignored) {
                System.out.println("Cons exception");
                ignored.printStackTrace();
            }

        }
//        System.out.println("Consumer@"+ Thread.currentThread().getId() + " finished");
    }
}
