package Client.GameSession;

import Client.Models.BoardState;
import com.google.gson.internal.LinkedTreeMap;
import org.jspace.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

public class Consumer implements Runnable {
    RemoteSpace rs;
    BoardState boardState;
    String packageType;

    public Consumer(String URI, BoardState boardState, String packageType) throws IOException, InterruptedException {
        this.rs = new RemoteSpace(URI);
        this.boardState = boardState;
        this.packageType = packageType;
    }

    @Override
    public void run() {
        while (true) {
            try {
                var data = rs.get(new FormalField(Object.class));
                if (this.packageType.equals("full")) {
                    BitSet newBoardState = (BitSet)data[0];
                    this.boardState.setBoardStateFromBitArray(newBoardState);
                } else if (this.packageType.equals("delta")) {
                    var changeListDto = (HashMap<String, ArrayList>)data[0];
                    var res = changeListDto.get("player1");
                    var res2 =(LinkedTreeMap<String, Double>) res.get(2);

                    System.out.println();
                    this.boardState.updateBoardFromDeltaTreeMap(res2);

                    //TODO: Take into account all players
                } else {
                    throw new Exception("Unknown Package Type!");
                }
                System.out.println(this.boardState);
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}