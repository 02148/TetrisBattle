package Client.GameSession;

import Client.Models.BoardState;
import org.jspace.*;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Random;

import static MainServer.Utils.getCurrentExactTimestamp;

/**
 * ClientSide Producer for sending packages to ServerSide
 */
public class Producer implements Runnable {
    RemoteSpace serverSpace;
    String clientUUID, packageType;
    int T; // Clock Period
    BoardState boardState;

    public Producer(String URI,
                    String clientUUID,
                    String packageType) throws IOException {
        this.serverSpace = new RemoteSpace(URI);
        this.clientUUID = clientUUID;
        this.packageType = packageType;
        this.boardState = new BoardState(200);
        T = packageType.equals("full") ? 1000 : 100;
    }

    public Producer(RemoteSpace serverSpace,
                    String clientUUID,
                    String packageType,
                    BoardState boardState) {
        this.serverSpace = serverSpace;
        this.clientUUID = clientUUID;
        this.packageType = packageType;
        T = 100;
        this.boardState = boardState;
    }

    // TODO get actual delta map when connected to client!
    private int[] getDeltaPkg() {
//        if (this.boardState == null) {
//            var map = new HashMap<Integer, Integer>();
//            map.put(new Random().nextInt(199), new Random().nextInt(6));
//            return map;
//        }
//        try {
//            return this.boardState.getLatestDeltaAndReset();
//        } catch (Exception e) {
            var map = new HashMap<Integer, Integer>();
            map.put(new Random().nextInt(199), new Random().nextInt(6));
            return new int[]{new Random().nextInt(199), new Random().nextInt(6)};
//        }
    }

    // TODO get actual board state BitSet when connected to client!
    private BitSet getFullPkg() {
        var curState = BitSet.valueOf(new long[] {1926344373436416L, 0, 8538824957113663488L, -5251191672863194661L, -355150468054712001L, 5761390391605366816L, 2696450152401329538L, -9027746953628520504L, 2618843117036503038L, 900900});
        curState.set((int) (Math.random() * 100));
        return curState;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (packageType.equals("full")) {
                    var boardState = getFullPkg();

                    // TODO TEMP PRINT current board state
//                    this.boardState.setBoardStateFromBitArray(boardState);
//                    System.out.println(this.boardState);

                    serverSpace.put(this.packageType,
                            getCurrentExactTimestamp(),
                            this.clientUUID,
                            boardState
                            );
                } else if (packageType.equals("delta")) {
                    var boardState = getDeltaPkg();
                    serverSpace.put(
                            this.packageType,
                            getCurrentExactTimestamp(),
                            this.clientUUID,
                            boardState);
                } else
                    throw new IllegalArgumentException("Package type " + this.packageType + " not defined.");

                Thread.sleep(this.T);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
