package MainServer.GameSession.Modules;

import org.jspace.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class Duplicator implements Runnable {
    Space in, conns;
    HashMap<String, Space> out;
    int T;
    boolean stop = false;

    public Duplicator(Space in, HashMap<String, Space> out, Space conns) throws Exception {
        this.in = in;
        this.out = out;
        this.conns = conns;
        this.T = 100;
    }

    @Override
    public void run() {
        while (!this.stop) {
            try {
                var curConns = conns.queryAll(new FormalField(String.class));

                Object[] raw_data_full = in.get(new FormalField(Object.class));
                Object[] raw_data_delta = in.get(
                        new FormalField(String.class),
                        new FormalField(Double.class),
                        new FormalField(Object.class));
                if (raw_data_full == null && raw_data_delta == null) {
                    throw new Exception("DUPLICATOR >> No new data");
                }

                if(raw_data_full == null) {
                    var data = (HashMap<String, Object[]>) raw_data_delta[0];

                    var dataPlayer = data.get("player1");
                    var name = dataPlayer[0];
                    var time = dataPlayer[1];
                    var datad = dataPlayer[2];
                    Object[] transObj = new Object[]{name, time, datad};

                    for (var c : curConns) {
                        String userUUID = (String) c[0];

                        if (out.containsKey(userUUID))
                            out.get(userUUID).put(transObj);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopThread() {
        this.stop = true;
    }
}
