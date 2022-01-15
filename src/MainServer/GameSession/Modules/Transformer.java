package MainServer.GameSession.Modules;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.Arrays;
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
        Object[] collectedData;
        int counter;
        while (true) {
            collectedData = new Object[]{new Object(),new Object(),new Object(),
                                         new Object(),new Object(),new Object(),
                                         new Object(),new Object(),new Object(),
                                         new Object(),new Object(),new Object(),
                                         new Object(),new Object(),new Object(),
                                         new Object(),new Object(),new Object(),
                                         new Object(),new Object(),new Object(),
                                         new Object(),new Object(),new Object()};
            counter = 0;

            try {
                if (!allBoards.isEmpty())
                    allBoards.clear();
                var curConns = conns.queryAll(new FormalField(String.class));

                for (var c : curConns) {
                    String userUUID = (String) c[0];
                    Object[] raw_data = in.getp(
                            new ActualField(userUUID),
                            new FormalField(Double.class),
                            new FormalField(Object.class)
                    );
                    if (raw_data == null || raw_data[0] == null)
                        continue;

                    System.out.println("TRANSFORMER@"+Thread.currentThread()+ " >> " + Arrays.toString(raw_data));

                    collectedData[counter*3]   = raw_data[0];
                    collectedData[counter*3+1] = raw_data[1];
                    collectedData[counter*3+2] = raw_data[2];
                    allBoards.put(userUUID, raw_data);
                    counter++;
                }

                if(!allBoards.isEmpty())
                    out.put(collectedData);
//                System.out.println("Transformer >> " + allBoards);
                Thread.sleep(this.T);
            } catch (InterruptedException e) {
                System.out.println("TRANSFORMER@" + Thread.currentThread() + " >> Exception");
                e.printStackTrace();
            }
        }
    }
}
