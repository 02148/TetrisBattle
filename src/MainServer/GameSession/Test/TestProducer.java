package MainServer.GameSession.Test;

import Client.GameSession.Producer;
import Client.Models.BoardState;
import MainServer.GameSession.GameSession;
import Client.GameSession.Consumer;
import org.jspace.*;

import java.io.IOException;
import java.util.BitSet;
import java.util.Random;

import static MainServer.Utils.getCurrentExactTimestamp;

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

    public static void main(String[] args) throws Exception {
        int PORT = 1337;

        SequentialSpace conns = new SequentialSpace();
        conns.put("player1");
        GameSession sess = new GameSession("69420", conns);

        Producer pDelta = new Producer("tcp://localhost:" + PORT + "/69420?keep",
                "player1",
                "delta"
        );

        Producer pFull = new Producer("tcp://localhost:" + PORT + "/69420?keep",
                "player1",
                "full"
        );

        var board = new BoardState(200);
        Consumer consumerDelta = new Consumer("tcp://localhost:" + PORT + "/player1?keep", board, "delta");
        Consumer consumerFull = new Consumer("tcp://localhost:" + PORT + "/player1?keep", board, "full");

        (new Thread(pDelta)).start();
        (new Thread(pFull)).start();
        (new Thread(consumerDelta)).start();
        (new Thread(consumerFull)).start();

        while (true){
            Thread.sleep(1000);
//            System.out.println(board.toString()+"\n");
        }
    }
}
