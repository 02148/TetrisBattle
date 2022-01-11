package Client.Utility;

import Client.Models.*;
import javafx.scene.paint.Color;

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
        if (color.equals(Color.CYAN)) {
            return 0;
        } else if(color.equals(Color.BLUE)) {
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

    public static Color intToColor(int i) {
        return switch (i) {
            case 0 -> Color.CYAN;
            case 1 -> Color.BLUE;
            case 2 -> Color.ORANGE;
            case 3 -> Color.YELLOW;
            case 4 -> Color.GREEN;
            case 5 -> Color.PINK;
            case 6 -> Color.RED;
            default -> Color.BEIGE;
        };
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
}
