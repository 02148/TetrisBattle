package Client.Logic;

import Client.Models.*;
import Client.UI.Board;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.BitSet;
import java.util.Random;

public class Controls {
  private Board nBoard;
  private BoardState boardState;

  private Tetromino current_tetromino;
  private Tetromino ghost_tetromino;

  private Random rnd              = new Random();
  private int lastTetromino       = -1;
  private int savedTetromino      = -1;
  private boolean allowedToSwitch = true;
  private boolean isDead          = false;
  private BitSet savedBoardState  = null;

  public Controls(Board nBoard, BoardState boardState) {
    this.nBoard = nBoard;
    this.boardState = boardState;
    this.current_tetromino = newRandomTetromino();

    updateView();
  }

  public void keyDownEvent(KeyCode keyCode) {
    if(keyCode == KeyCode.UP) {
      rotateTetromino(current_tetromino);
    } else if(keyCode == KeyCode.DOWN && boardState.legalPosition(current_tetromino, 0, 1)) {
      moveTetromino(current_tetromino, 0, 1);
    } else if(keyCode == KeyCode.LEFT && boardState.legalPosition(current_tetromino, -1, 0)) {
      moveTetromino(current_tetromino, -1, 0);
    } else if(keyCode == KeyCode.RIGHT && boardState.legalPosition(current_tetromino, 1, 0)) {
      moveTetromino(current_tetromino, 1, 0);
    } else if(keyCode == KeyCode.SPACE) {
      dropTetromino();
    } else if(keyCode == KeyCode.C && allowedToSwitch) {
      switchCurrentTetromino();
    } else if(keyCode == KeyCode.T) {
      loadSavedState();
    } else if(keyCode == KeyCode.P) {
      print();
    }

    updateView();
  }

  public void gameTick() {
    if(boardState.legalPosition(current_tetromino, 0, 1)) {
      moveTetromino(current_tetromino, 0, 1);
    } else {
      dropTetromino();

//      if(boardState.removeFilledRows(current_tetromino.posY)){
//        nBoard.setNumRowsRemoved();
//      }

    }

     updateView();
  }

  public void updateView() {
    boardState.removeTetromino(current_tetromino);
    if(ghost_tetromino != null && boardState.legalPosition(ghost_tetromino, 0, 0) )
      boardState.removeTetromino(ghost_tetromino);

    updateGhost();

    boardState.insertTetromino(ghost_tetromino);
    boardState.insertTetromino(current_tetromino);

    if(!isDead)
      nBoard.loadBoardState(boardState);
  }

  public boolean isDead() {
    return isDead;
  }

  public void dropTetromino() {
    boardState.dropTetromino(current_tetromino);
    boardState.removeFilledRows(current_tetromino.posY);
    current_tetromino = newRandomTetromino();
    if(!boardState.legalPosition(current_tetromino, 0, 0))
      isDead = true;
    boardState.insertTetromino(current_tetromino);
    allowedToSwitch = true;
  }

  public void rotateTetromino(Tetromino tetromino) {
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

  public void moveTetromino(Tetromino tetromino, int posX_incr, int posY_incr) {
    boardState.removeTetromino(tetromino);
    tetromino.posX += posX_incr;
    tetromino.posY += posY_incr;
    boardState.insertTetromino(tetromino);
  }

  public void switchCurrentTetromino() {
    boardState.removeTetromino(current_tetromino);

    if(savedTetromino != -1) {
      int tempTetro = savedTetromino;
      savedTetromino = lastTetromino;
      current_tetromino = newTetromino(tempTetro);
    } else {
      savedTetromino = lastTetromino;
      current_tetromino = newRandomTetromino();
    }

    allowedToSwitch = false;
    boardState.insertTetromino(current_tetromino);
  }

  public void updateGhost() {
    int lowestY = boardState.getLowestLegalYcoord(current_tetromino);

    ghost_tetromino = newTetromino(lastTetromino);
    ghost_tetromino.color = new Color(ghost_tetromino.color.getRed(), ghost_tetromino.color.getGreen(), ghost_tetromino.color.getBlue(), 0.8).brighter();
    ghost_tetromino.posX = current_tetromino.posX;
    ghost_tetromino.posY = lowestY;
    ghost_tetromino.state = current_tetromino.state;
  }

  public Tetromino newRandomTetromino() {
    lastTetromino = rnd.nextInt(7);
    return newTetromino(lastTetromino);
  }

  public Tetromino newTetromino(int index) {
    switch(index) {
      case 0:
        return new I_Block();
      case 1:
        return new J_Block();
      case 2:
        return new L_Block();
      case 3:
        return new O_Block();
      case 4:
        return new S_Block();
      case 5:
        return new T_Block();
      case 6:
        return new Z_Block();
    }
    return null;
  }


  // MISC METHODS
  public void loadSavedState() {
    if(savedBoardState == null) {
      savedBoardState = boardState.toBitArray();
    } else {
      boardState.setBoardStateFromBitArray(savedBoardState);
    }
  }
  public void print() {
    System.out.println(boardState.toString());

    BitSet bitset = boardState.toBitArray();
    for(int i = 0; i < bitset.length(); i++) {
      if(i%30==0 && i != 0)
        System.out.println();
      System.out.print(bitset.get(i) ? "1 " : "0 ");
    }
  }
}
