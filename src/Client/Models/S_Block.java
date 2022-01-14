package Client.Models;

import Client.Utility.Utils;
import javafx.scene.paint.Color;

public class S_Block extends Tetromino{
  private final int[][] rotations_ = new int[][] {
          {0,0,0,0,
           0,0,1,1,
           0,1,1,0,
           0,0,0,0},
          {0,0,0,0,
           0,0,1,0,
           0,0,1,1,
           0,0,0,1},
          {0,0,0,0,
           0,0,0,0,
           0,0,1,1,
           0,1,1,0},
          {0,0,0,0,
           0,1,0,0,
           0,1,1,0,
           0,0,1,0}
  };

  private final int[][][] wallKickData_ = new int[][][] {
          {{0,0}, {-1,0}, {-1,-1}, {0,2}, {-1,2}}, // State 0 to State 1 // State 0 and right rotation
          {{0,0}, {1,0}, {1,1}, {0,-2}, {1,-2}}, // State 1 to State 0 // State 1 and  left rotation
          {{0,0}, {1,0}, {1,1}, {0,-2}, {1,-2}}, // State 1 to State 2 // State 1 and right rotation
          {{0,0}, {-1,0}, {-1,-1}, {0,2}, {-1,2}}, // State 2 to State 1 // State 2 and  left rotation
          {{0,0}, {1,0}, {1,-1}, {0,2}, {1,2}}, // State 2 to State 3 // State 2 and right rotation
          {{0,0}, {-1,0}, {-1,1}, {0,-2}, {-1,-2}}, // State 3 to State 2 // State 3 and  left rotation
          {{0,0}, {1,0}, {-1,1}, {0,-2}, {-1,-2}}, // State 3 to State 0 // State 3 and right rotation
          {{0,0}, {1,0}, {1,-1}, {0,2}, {-1,-2}}  // State 0 to State 3 // State 0 and  left rotation
  };

  public S_Block() {
    this.posX = 3;
    this.posY = -2;
<<<<<<< HEAD
    this.color = Utils.tetrominoTypeToColor(this.getClass());
=======
    this.color = Color.LIME;
>>>>>>> origin/master
    this.rotations = rotations_;
    this.wallKickData = wallKickData_;
  }
}
