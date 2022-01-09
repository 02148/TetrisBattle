package Client.Logic;

import Client.Models.BoardState;
import Client.Models.Tetromino;
import Client.UI.Board;
import javafx.scene.input.KeyCode;

public class Controls {
  public Controls() { }

  public void keyDownEvent(KeyCode keyCode, Tetromino current_tetromino, BoardState boardState) {
    if(keyCode == KeyCode.UP) {
      rotateTetromino(boardState, current_tetromino);
    } else if(keyCode == KeyCode.DOWN && boardState.legalPosition(current_tetromino, 0, 1)) {
      moveTetromino(boardState, current_tetromino, 0, 1);
    } else if(keyCode == KeyCode.LEFT && boardState.legalPosition(current_tetromino, -1, 0)) {
      moveTetromino(boardState, current_tetromino, -1, 0);
    } else if(keyCode == KeyCode.RIGHT && boardState.legalPosition(current_tetromino, 1, 0)) {
      moveTetromino(boardState, current_tetromino, 1, 0);
    }
  }

  public void moveDown(Tetromino current_tetromino, BoardState boardState) {
    if(boardState.legalPosition(current_tetromino, 0, 1)) {
      moveTetromino(boardState, current_tetromino, 0, 1);
    } else {
      boardState.placeTetromino(current_tetromino);
    }
  }

  public void rotateTetromino(BoardState boardState, Tetromino tetromino) {
    int[][] rightWallKickData = tetromino.getRightWallKickData();
    int whichKick = boardState.getLegalRotation(tetromino.getRightRotation(), rightWallKickData, tetromino.posX, tetromino.posY);
    if(whichKick != -1) {
      boardState.removeTetromino(tetromino);
      tetromino.rotateRight();
      tetromino.posX += rightWallKickData[whichKick][0];
      tetromino.posY += rightWallKickData[whichKick][1];
      boardState.insertTetromino(tetromino);
    }
  }

  public void moveTetromino(BoardState boardState, Tetromino tetromino, int posX_incr, int posY_incr) {
    boardState.removeTetromino(tetromino);
    tetromino.posX += posX_incr;
    tetromino.posY += posY_incr;
    boardState.insertTetromino(tetromino);
  }
}
