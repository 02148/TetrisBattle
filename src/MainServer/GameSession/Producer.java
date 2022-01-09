package MainServer.GameSession;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.BitSet;
import java.util.HashMap;

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
                var curConns = conns.queryAll(new FormalField(String.class));

                Object[] raw_data = shared.getp(
                        new FormalField(Double.class),
                        new FormalField(HashMap.class)
                );
                if (raw_data == null) {
//                    System.out.println("PROD - sessData null");
                    continue;
                }

                HashMap<String, Object[]> allBoards = (HashMap<String, Object[]>) raw_data[1];

                out.put(
                        (double)raw_data[0],
                        raw_data[1]
                );

                System.out.println(">>");
                for (var c : curConns) {
                    var cur_data = allBoards.get((String)c[0]);
                    if (cur_data == null)
                        continue;
                    System.out.println(
                            (double) cur_data[0] + " - " +
                            (String) cur_data[1] + " - " +
                            (BitSet) cur_data[2]
                    );
                }
            } catch (Exception ignored) {
                System.out.println("Prod exception");
                ignored.printStackTrace();
            }
        }
//        System.out.println("Producer@"+ Thread.currentThread().getId() + " finished");
    }
}
