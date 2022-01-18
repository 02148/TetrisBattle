package Client.GameSession;

import Client.Models.BoardState;
import Client.Models.Mino;
import Client.Utility.Utils;

import java.util.*;

public class PackageHandler {
    private Mino[] lastBoardSnapshot;
    private HashMap<Integer, Integer> delta = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> deltaClone = new HashMap<Integer, Integer>();

    Queue<DeltaDataObject> deltaQueue;

    public PackageHandler(int size) {
        lastBoardSnapshot = new Mino[size];

        Comparator<DeltaDataObject> deltaComp = Comparator.comparingDouble(DeltaDataObject::getTimestamp);

        deltaQueue = new PriorityQueue<>(deltaComp);
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

    public void reapplyDelta(BoardState boardState) {
        for (var deltaDataObject : this.deltaQueue)
            boardState.updateBoardFromDeltaIntegerArray(deltaDataObject.getDeltaPkg());
    }

    public void removeOldDeltas(double lastFullPkgTimestamp) {
        var curDelta = this.deltaQueue.peek();
        if (curDelta == null)
            return;

        while (curDelta.getTimestamp() < lastFullPkgTimestamp) {
            this.deltaQueue.remove();
            curDelta = this.deltaQueue.peek();
        }
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
