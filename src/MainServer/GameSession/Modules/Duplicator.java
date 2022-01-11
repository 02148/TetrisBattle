package MainServer.GameSession.Modules;

import org.jspace.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class Duplicator implements Runnable {
    Space in, conns;
    HashMap<String, Space> out;
    int T;

    public Duplicator(Space in, HashMap<String, Space> out, Space conns) throws Exception {
        this.in = in;
        this.out = out;
        this.conns = conns;
        this.T = 100;
    }

    @Override
    public void run() {
        while (true) {
            try {
                var curConns = conns.queryAll(new FormalField(String.class));

                Object[] raw_data = in.get(
                        new FormalField(HashMap.class)
                );
                if (raw_data == null)
                    throw new Exception("DUPLICATOR >> No new data");

                var data = (HashMap<String, Object[]>) raw_data[0];

                System.out.println(Arrays.toString(raw_data));

                for (var c : curConns) {
                    String userUUID = (String) c[0];

                    if (out.containsKey(userUUID))
                        out.get(userUUID).put(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
