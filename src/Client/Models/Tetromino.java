package Client.Models;

import javafx.scene.paint.Color;

public abstract class Tetromino implements TetrominoInterface {
  public int[][] rotations;
  public int[][][] wallKickData;
  public int posY, posX;
  public Color color;
  public int state = 0;

  @Override
  public int[] getCurrentRotation() {
    return rotations[state];
  }

  @Override
  public int[] getRotation(int rotationIndex) {
    return rotations[rotationIndex];
  }

  @Override
  public int[] getRightRotation() { return rotations[state == 3 ? 0 : state+1]; }

  @Override
  public int[] getLeftRotation() { return rotations[state == 0 ? 3 : state-1]; }

  @Override
  public int[][] getRightWallKickData() {
    return wallKickData[state == 0 ? 0 : state*2-1];
  }

  @Override
  public int[][] getLeftWallKickData() {
    return wallKickData[state == 0 ? 7 : state*2];
  }

  @Override
  public void rotateRight() { state = state == 3 ? 0 : state+1; }

  @Override
  public void rotateLeft() { state = state == 0 ? 3 : state-1; }
}
