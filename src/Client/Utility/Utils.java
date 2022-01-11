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

    public static Color intToColor(int i) {
        switch (i) {
            case 0:
                return Color.BLUE;
            case 1:
                return Color.CYAN;
            case 2:
                return Color.ORANGE;
            case 3:
                return Color.YELLOW;
            case 4:
                return Color.GREEN;
            case 5:
                return Color.PINK;
            case 6:
                return Color.RED;
        }
        return Color.BEIGE;
    }


}
