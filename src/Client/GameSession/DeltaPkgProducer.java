package Client.GameSession;

import Client.Models.BoardState;
import org.jspace.*;

import java.io.IOException;
import java.rmi.server.ExportException;

import static MainServer.Utils.getCurrentExactTimestamp;

/**
 * ClientSide Producer for producing and sending delta packages to ServerSide
 */
public class DeltaPkgProducer {
    RemoteSpace serverSpace;
    String clientUUID;
    BoardState boardState;
    Space localSpace;

    public DeltaPkgProducer(String URI,
                            String clientUUID,
                            BoardState boardState) throws IOException {
        this.serverSpace = new RemoteSpace(URI);
        this.clientUUID = clientUUID;
        this.boardState = boardState;
        this.localSpace = new QueueSpace();

        Thread communicator = new Thread(() -> {
            while(true) {
                try {
                    int[] data = (int[]) localSpace.get(new FormalField(Object.class))[0]; // blocking async operation

                    this.serverSpace.put("delta",
                            getCurrentExactTimestamp(),
                            this.clientUUID,
                            data
                    );
                } catch (Exception ignored) {}
            }
        });

        communicator.start();
    }


    public void sendBoard() {
        try {
            localSpace.put(this.boardState.getLatestDeltaAndReset());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
