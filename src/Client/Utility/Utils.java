package Client.Utility;

import Client.Models.*;
import common.Constants;
import javafx.scene.paint.Color;

import java.util.HashMap;

public abstract class Utils {

    public static Tetromino newTetromino(int index) {
        return switch (index) {
            case 0 -> new I_Block();
            case 1 -> new J_Block();
            case 2 -> new L_Block();
            case 3 -> new O_Block();
            case 4 -> new S_Block();
            case 5 -> new T_Block();
            case 6 -> new Z_Block();
            default -> null;
        };
    }

    public static int minoToColorInt(Mino mino) {
        if(mino != null)
            return colorToInt(mino.color);
        return -1;
    }

    public static int colorToInt(Color color) {
        for(int i = 0; i < 7; i++) {
            if(color.equals(Constants.tetroColors[i])) {
                return i;
            }
        }
        return -1;
    }

    public static Color intToColor(int i) {
        return Constants.tetroColors[i];
    }

    public static int tetrominoToInt(Class tetroClass) {
        if(tetroClass.equals(I_Block.class)) {
            return 0;
        } else if(tetroClass.equals(J_Block.class)) {
            return 1;
        } else if(tetroClass.equals(L_Block.class)) {
            return 2;
        } else if(tetroClass.equals(O_Block.class)) {
            return 3;
        } else if(tetroClass.equals(S_Block.class)) {
            return 4;
        } else if(tetroClass.equals(T_Block.class)) {
            return 5;
        }
        // Z_Block
        return 6;
    }

    public static Color tetrominoTypeToColor(Class tetroClass) {
        int tetroInt = tetrominoToInt(tetroClass);
        return intToColor(tetroInt);
    }

    public static int[] deltaHashmapToArray(HashMap<Integer, Integer> delta) {
        int[] deltaArray = new int[delta.size()*2];
        int counter = 0;
        for(var key : delta.keySet()) {
            deltaArray[counter*2]   = key;
            deltaArray[counter*2+1] = delta.get(key);
            counter++;
        }
        return deltaArray;
    }
}
