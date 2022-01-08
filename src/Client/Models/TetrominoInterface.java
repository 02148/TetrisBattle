package Client.Models;

import javafx.scene.paint.Color;

public interface TetrominoInterface {
  int[] getCurrentRotation();
  int[] getRotation(int rotationIndex);
  int[] getRightRotation();
  int[] getLeftRotation();
  int[][] getRightWallKickData();
  int[][] getLeftWallKickData();
  void rotateRight();
  void rotateLeft();
}
