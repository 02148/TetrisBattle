package MainServer.GameSession;

import Client.Models.BoardState;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.BitSet;
import java.util.HashMap;

public class Producer implements Runnable {
    Space shared, out, conns;
    String spaceKeyIn, spaceKeyOut;
    // T is 1/F, where F is update frequency
    int T;

    public Producer(Space shared, Space out, Space conns, String spaceKeyIn, String spaceKeyOut) {
        this.shared = shared;
        this.out = out;
        this.conns = conns;
        this.spaceKeyIn = spaceKeyIn;
        this.spaceKeyOut = spaceKeyOut;
        this.T = spaceKeyIn.equals("full") ? 100 : 20; // if full sync, send once a second, else 500 times a second
    }

    /**
     * Sets update frequency, which should be faster (lower) for
     * the changelist Producer and slower (higher) for the sync Producer
     * @param newT The T to update to. T=1/F, where F is update frequency in Hz
     */
    public void setUpdateFrequency(int newT) {
        this.T = newT;
    }

    @Override
    public void run() {
        BoardState boardState = new BoardState(200);

        while (true) {
            try {
                Thread.sleep(T);
            } catch (InterruptedException ignored) {}
            try {
                var curConns = conns.queryAll(new FormalField(String.class));

                Object[] raw_data = shared.getp(
                        new ActualField(this.spaceKeyIn),
                        new FormalField(Double.class),
                        new FormalField(HashMap.class)
                );
                if (raw_data == null) {
                    System.out.println("PROD@" + this.spaceKeyIn + " >> sessData null");
                    continue;
                }

                HashMap<String, Object[]> allBoards = (HashMap<String, Object[]>) raw_data[2];

                out.put(
                        this.spaceKeyOut,
                        (double)raw_data[1],
                        allBoards
                );

//                System.out.println(">>");
                for (var c : curConns) {
                    var cur_data = allBoards.get((String)c[0]);
                    if (cur_data == null)
                        continue;
//                    System.out.println(
//                            (double) cur_data[1] + " - " +
//                            (String) cur_data[2] + " - " +
//                            (BitSet) cur_data[3]
//                    );

                    boardState.setBoardStateFromBitArray((BitSet) cur_data[3]);
                    System.out.println("@" + (String)c[0] + "\n" + boardState + "\n>>>>");

                }
            } catch (Exception e) {
                System.out.println("Prod@" + this.spaceKeyIn + " exception");
                e.printStackTrace();
            }
        }
//        System.out.println("Producer@"+ Thread.currentThread().getId() + " finished");
    }
}
