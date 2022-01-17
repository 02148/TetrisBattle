package Client.GameSession;

import Client.Models.BoardState;
import org.jspace.*;

import java.io.IOException;

import static MainServer.Utils.getCurrentExactTimestamp;

/**
 * ClientSide Producer for producing and sending delta packages to ServerSide
 */
public class DeltaPkgProducer {
    RemoteSpace serverSpace;
    String clientUUID;
    BoardState boardState;

    public DeltaPkgProducer(String URI,
                            String clientUUID,
                            BoardState boardState) throws IOException {
        this.serverSpace = new RemoteSpace(URI);
        this.clientUUID = clientUUID;
        this.boardState = boardState;
    }

    public void sendBoard() {
        try {
            this.serverSpace.put("delta",
                    getCurrentExactTimestamp(),
                    this.clientUUID,
                    this.boardState.getLatestDeltaAndReset()
            );
        } catch (Exception ignored) {}
    }
}
