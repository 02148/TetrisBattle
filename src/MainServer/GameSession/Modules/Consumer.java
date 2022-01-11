package MainServer.GameSession.Modules;

import Client.Models.BoardState;
import org.jspace.*;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;

public class Consumer implements Runnable {
    RemoteSpace rs;
    BoardState boardState;
    String packageType;

    public Consumer(String URI, BoardState boardState, String packageType) throws IOException {
        this.rs = new RemoteSpace(URI);
        this.boardState = boardState;
        this.packageType = packageType;
    }

    @Override
    public void run() {
        while (true) {
            try {
                var data = rs.get(new FormalField(Object.class));
                if (this.packageType.equals("sync")) {
                    BitSet newBoardState = (BitSet)data[0];
                    this.boardState.setBoardStateFromBitArray(newBoardState);
                } else if (this.packageType.equals("changelist")) {
                    HashMap<Integer,Integer> changeList = (HashMap<Integer,Integer>)data[0];
                    this.boardState.
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}