package MainServer.GameSession;

import MainServer.Utils;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.BitSet;
import java.util.HashMap;

public class Consumer implements Runnable {
    Space in, shared, conns;
    int noConns, T = 10;

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
                var curConns = conns.queryAll(new FormalField(String.class));
                noConns = curConns.size();
                if (noConns == 0) {
                    System.out.println("Consumer@"+ Thread.currentThread().getId() + " finished");
                    Thread.currentThread().join(1);
                }

                HashMap<String, Object[]> allBoards = new HashMap<>();
                for (var c : curConns) {
                    Object[] raw_data = in.getp(
                            new FormalField(Double.class),
                            new ActualField((String)c[0]),
//                            new FormalField(String.class),
                            new FormalField(BitSet.class)
                    );
                    if (raw_data == null)
                        continue;

                    allBoards.put((String) c[0], raw_data);
                }
                shared.put(
                        Utils.getCurrentExactTimestamp(),
                        allBoards
                );

            } catch (Exception ignored) {
                System.out.println("Cons exception");
                ignored.printStackTrace();
            }

        }
    }
}
