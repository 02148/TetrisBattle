package Client.GameSession;

import Client.Models.BoardState;
import Client.Models.Mino;
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
        if(board[index] != this.lastBoardSnapshot[index] && delta.get(index) != minoToColorInt(board[index])) {
            this.delta.put(index, minoToColorInt(board[index]));
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

    private int minoToColorInt(Mino mino) {
        if(mino != null)
            return colorToInt(mino.color);
        return -1;
    }

    private int colorToInt(Color color) {
        if (color.equals(Color.BLUE)) {
            return 0;
        } else if(color.equals(Color.CYAN)) {
            return 1;
        } else if(color.equals(Color.ORANGE)) {
            return 2;
        } else if(color.equals(Color.YELLOW)) {
            return 3;
        } else if(color.equals(Color.GREEN)) {
            return 4;
        } else if(color.equals(Color.PINK)) {
            return 5;
        } else if(color.equals(Color.RED)) {
            return 6;
        }
        return -1;
    }
}
