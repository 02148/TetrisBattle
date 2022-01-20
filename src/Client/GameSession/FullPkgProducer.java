package Client.GameSession;

import Client.Models.BoardState;
import org.jspace.*;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Random;

import static MainServer.Utils.getCurrentExactTimestamp;

/**
 * ClientSide Producer for producing and sending full packages to ServerSide
 */
public class FullPkgProducer implements Runnable {
    RemoteSpace serverSpace;
    String clientUUID;
    int T; // Clock Period
    BoardState boardState;
    boolean stop = false;

    public FullPkgProducer(String URI,
                           String clientUUID,
                           BoardState boardState) throws IOException {
        this.serverSpace = new RemoteSpace(URI);
        this.clientUUID = clientUUID;
        this.boardState = boardState;
        T = 1000; // 1 second update rate
    }

    public void sendBoard() {
        try {
            this.serverSpace.put("full",
                    getCurrentExactTimestamp(),
                    this.clientUUID,
                    this.boardState.toBitArray()
            );
        } catch (Exception ignored) {}
    }

    @Override
    public void run() {
        while (!this.stop) {
            try {
                sendBoard();
                Thread.sleep(this.T);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.stop = true;
    }
}
