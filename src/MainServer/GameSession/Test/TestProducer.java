package MainServer.GameSession.Test;

import MainServer.GameSession.GameSession;
import org.jspace.*;

import java.io.IOException;
import java.util.BitSet;
import java.util.Date;
import java.util.Random;

import static MainServer.Utils.getCurrentExactTimestamp;
import static MainServer.Utils.getCurrentTimestamp;

public class TestProducer implements Runnable {
    RemoteSpace rs;
    Random rnd;
    String uuid, spaceKey;
    int T; // T = 1/F
    static int noConns = 4;

    public BitSet getDummyBoard() {
        return BitSet.valueOf(new long[] {1926344373436416L, 0, 8538824957113663488L, -5251191672863194661L, -355150468054712001L, 5761390391605366816L, 2696450152401329538L, -9027746953628520504L, 2618843117036503038L, 900900});
    }

    public TestProducer(String uri, String uuid, String spaceKey) throws IOException {
        this.uuid = uuid;
        this.rs = new RemoteSpace(uri);
        this.spaceKey = spaceKey;
        this.rnd = new Random();
        this.T = spaceKey.equals("full") ? 100 : 20; // if full sync, send once a second, else 500 times a second
    }

    @Override
    public void run() {
        double cur = 0;
        while (true) {
            try {
                cur += rnd.nextGaussian();
                BitSet bs;
                if (this.spaceKey.equals("full"))
                    bs = getDummyBoard();
                else
                    bs = new BitSet(600); // getDummyBoard();
                bs.set((int)Math.abs(cur));

                rs.put(this.spaceKey,getCurrentExactTimestamp(), this.uuid, bs);

                Thread.sleep(T);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {

        SequentialSpace conns = new SequentialSpace();

        for (int i = 0; i < noConns; i++)
            conns.put(""+i);

        conns.put("player1");
        GameSession sess = new GameSession("69420", conns);
        StackSpace s = new StackSpace();

        for (int i = 0; i < noConns; i++) {
            (new Thread(new TestProducer("tcp://localhost:6969/69420?keep",
                    "" + i,
                    "delta")))
                    .start();
            (new Thread(new TestProducer("tcp://localhost:6969/69420?keep",
                    "" + i,
                    "full")))
                    .start();
        }

        while (true){}
    }
}
