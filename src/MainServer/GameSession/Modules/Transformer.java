package MainServer.GameSession.Modules;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.BitSet;
import java.util.HashMap;

public class Transformer implements Runnable {
    Space in, out, conns;
    int T;

    public Transformer(Space in, Space out, Space conns) {
        this.in = in;
        this.out = out;
        this.conns = conns;
        this.T = 100;
    }

    @Override
    public void run() {
        var allBoards = new HashMap<String, Object[]>();
        try {
            if (!allBoards.isEmpty())
                allBoards.clear();
            var curConns = conns.queryAll(new FormalField(String.class));

            for (var c : curConns) {
                String userUUID = (String)c[0];
                Object[] raw_data = in.getp(
                        new ActualField(userUUID),
                        new FormalField(Double.class),
                        new FormalField(Object.class)
                );
                if (raw_data == null)
                    continue;

                allBoards.put(userUUID, raw_data);
            }

            out.put(allBoards);
        } catch (InterruptedException e) {
            System.out.println("TRANSFORMER@" + Thread.currentThread() + " Exception");
            e.printStackTrace();
        }
    }
}
