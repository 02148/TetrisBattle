package Client.Logic;

import Client.Models.BoardState;
import Client.Models.Tetromino;
import Client.UI.Board;
import javafx.scene.input.KeyCode;

public class Controls {
  public Controls() {

  }

  public void keyDownEvent(KeyCode keyCode, Tetromino current_tetromino, BoardState boardState) {

    if(keyCode == KeyCode.UP) {
      int[][] rightWallKickData = current_tetromino.getRightWallKickData();
      int whichKick = boardState.getLegalRotation(current_tetromino.getRightRotation(), rightWallKickData, current_tetromino.posX, current_tetromino.posY);
      if(whichKick != -1) {
        boardState.removeTetromino(current_tetromino);
        current_tetromino.rotateRight();
        current_tetromino.posX += rightWallKickData[whichKick][0];
        current_tetromino.posY += rightWallKickData[whichKick][1];
        boardState.insertTetromino(current_tetromino);
      }

    } else if(keyCode == KeyCode.DOWN && boardState.legalPosition(current_tetromino, 0, 1)) {
      boardState.removeTetromino(current_tetromino);
      current_tetromino.posY++;
      boardState.insertTetromino(current_tetromino);
    } else if(keyCode == keyCode.LEFT && boardState.legalPosition(current_tetromino, -1, 0)) {
      boardState.removeTetromino(current_tetromino);
      current_tetromino.posX--;
      boardState.insertTetromino(current_tetromino);
    } else if(keyCode == KeyCode.RIGHT && boardState.legalPosition(current_tetromino, 1, 0)) {
      boardState.removeTetromino(current_tetromino);
      current_tetromino.posX++;
      boardState.insertTetromino(current_tetromino);
    }
  }

  public void moveDown(Tetromino current_tetromino, BoardState boardState) {
    System.out.println("Move Dwn");

    if(boardState.legalPosition(current_tetromino, 0, 1)) {
      boardState.removeTetromino(current_tetromino);
      current_tetromino.posY++;
      boardState.insertTetromino(current_tetromino);
    } else {
      boardState.placeTetromino(current_tetromino);
    }
  }
}
