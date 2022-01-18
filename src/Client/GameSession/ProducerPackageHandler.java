package Client.GameSession;

import Client.Models.BoardState;
import Client.Models.Mino;
import Client.UI.Board;
import Client.Utility.Utils;

import java.util.*;

public class ProducerPackageHandler {
    private Mino[] lastBoardSnapshot;
    private HashMap<Integer, Integer> delta = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> deltaClone = new HashMap<Integer, Integer>();
    private DeltaPkgProducer deltaPackageProducer;
    private FullPkgProducer fullPackageProducer;
    private BoardState boardState;
    private Board nBoard;

    public ProducerPackageHandler(int size, BoardState boardState, Board nBoard, DeltaPkgProducer deltaPkgProducer, FullPkgProducer fullPkgProducer) {
        this.lastBoardSnapshot = new Mino[size];
        this.deltaPackageProducer = deltaPkgProducer;
        this.fullPackageProducer = fullPkgProducer;
        this.boardState = boardState;
        this.nBoard = nBoard;
    }

    public void updateDelta(Mino[] board, int index) {
        if (board[index] != this.lastBoardSnapshot[index]) {
            this.delta.put(index, Utils.minoToColorInt(board[index]));
        } else if (this.delta.containsKey(index)) {
            this.delta.remove(index);
        }
    }

    public HashMap<Integer, Integer> retrieveAndResetDelta(Mino[] board) {
        deltaClone.clear();
        for (int k : delta.keySet()) {
            this.lastBoardSnapshot[k] = board[k];
            deltaClone.put(k, delta.get(k));
        }
        delta.clear();
        return deltaClone;
    }

    public void sendDeltaPackage() {
        this.deltaPackageProducer.sendBoard();
    }
}
