package Client.GameSession;

import Client.Models.BoardState;
import Client.Models.Mino;
import Client.UI.Board;
import Client.Utility.Utils;

import java.util.*;

public class PackageHandlerConsumer {
    private Mino[] lastBoardSnapshot;
    private HashMap<Integer, Integer> delta = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> deltaClone = new HashMap<Integer, Integer>();
    private BoardState boardState;
    private Board nBoard;
    private Queue<DeltaDataObject> deltaQueue;

    public PackageHandlerConsumer(int size, BoardState boardState, Board nBoard) {
        this.lastBoardSnapshot = new Mino[size];
        this.boardState = boardState;
        this.nBoard = nBoard;

        Comparator<DeltaDataObject> deltaComp = Comparator.comparingDouble(DeltaDataObject::getTimestamp);
        this.deltaQueue = new PriorityQueue<>(deltaComp);
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


    public void addDeltaToQueue(double timestamp, int[] newDelta) {
        deltaQueue.add(new DeltaDataObject(timestamp, newDelta));
    }

    public void applyFull(BitSet newBoardState) {
        this.boardState.setBoardStateFromBitArray(newBoardState);
    }

    public void applyDelta(int[] newDelta) {
        this.boardState.updateBoardFromDelta(newDelta);
    }

    public void reapplyDeltas() {
        for (var deltaDataObject : this.deltaQueue)
            this.boardState.updateBoardFromDelta(deltaDataObject.getDeltaPkg());
    }

    public void removeOldDeltas(double lastFullPkgTimestamp) {
        var curDelta = this.deltaQueue.peek();
        if (curDelta == null)
            curDelta = new DeltaDataObject(0, null);

        while (curDelta != null && curDelta.getTimestamp() < lastFullPkgTimestamp) {
            this.deltaQueue.remove();
            curDelta = this.deltaQueue.peek();
        }
    }

    public void updateViewModel() {
        this.nBoard.loadBoardState(boardState);
    }

    public void printBoard() {
        System.out.println(this.boardState);
    }
}

class DeltaDataObject {
    double timestamp;
    int[] deltaPkg;

    public double getTimestamp() {
        return timestamp;
    }

    public int[] getDeltaPkg() {
        return deltaPkg;
    }

    public DeltaDataObject(double timestamp, int[] deltaPkg) {
        this.timestamp = timestamp;
        this.deltaPkg = deltaPkg;
    }
}
