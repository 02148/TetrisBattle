package Client.GameSession;

import Client.Models.BoardState;
import Client.Models.Mino;
import Client.Utility.Utils;
import javafx.scene.paint.Color;

import java.util.HashMap;

public class PackageHandler {
    private Mino[] lastBoardSnapshot;
    private HashMap<Integer, Integer> delta = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> deltaClone = new HashMap<Integer, Integer>();

    public PackageHandler(int size) {
        lastBoardSnapshot = new Mino[size];
    }

    public void updateDelta(Mino[] board, int index) {
        if(board[index] != this.lastBoardSnapshot[index]) {
            this.delta.put(index, Utils.minoToColorInt(board[index]));
        } else if(this.delta.containsKey(index)) {
            this.delta.remove(index);
        }
    }

    public HashMap<Integer, Integer> retrieveAndResetDelta(Mino[] board) {
        deltaClone.clear();
        for(int k : delta.keySet()) {
            this.lastBoardSnapshot[k] = board[k];
            deltaClone.put(k, delta.get(k));
        }
        delta.clear();
        return deltaClone;
    }
}
