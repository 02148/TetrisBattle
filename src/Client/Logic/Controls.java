package Client.Logic;

import Client.GameSession.ProducerPackageHandler;
import Client.Models.*;
import Client.UI.Board;
import Client.Utility.Utils;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.BitSet;
import java.util.Random;

public class Controls {
  private Board nBoard;
  private BoardState boardState;
  private boolean viewOnly;

  private Tetromino current_tetromino;
  private Tetromino ghost_tetromino;

  private Random rnd              = new Random();
  private int lastTetromino       = -1;
  private int savedTetromino      = -1;
  private boolean allowedToSwitch = true;
  private boolean isDead          = false;
  private BitSet savedBoardState  = null;

  private ProducerPackageHandler producerPackageHandler;


  public Controls(Board nBoard, BoardState boardState, boolean viewOnly) {
    this.nBoard = nBoard;
    this.boardState = boardState;
    if(!viewOnly)
      this.current_tetromino = newRandomTetromino();
    this.viewOnly = viewOnly;

    updateViewModel();

//    if(!this.viewOnly)
//      this.packageHandlerProducer.sendDeltaPackage();
  }

  public void setPackageHandlerFull(ProducerPackageHandler producerPackageHandler) {
    this.producerPackageHandler = producerPackageHandler;
  }

  // Update View and BoardState Methods
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
    } else if(keyCode == KeyCode.P) {
      print();
    } else if(keyCode == KeyCode.DIGIT1) {
      this.boardState.addRows(1);
    } else if(keyCode == KeyCode.DIGIT2) {
      this.boardState.addRows(2);
    } else if(keyCode == KeyCode.DIGIT3) {
      this.boardState.addRows(3);
    } else if(keyCode == KeyCode.DIGIT4) {
      this.boardState.addRows(4);
    }

    updateViewModel();

    if(!this.viewOnly)
      this.producerPackageHandler.sendDeltaPackage();
  }

  public void gameTick() {
    if(boardState.legalPosition(current_tetromino, 0, 1)) {
      moveTetromino(current_tetromino, 0, 1);
    } else {
      dropTetromino();
    }

    updateViewModel();

    if(!this.viewOnly)
      this.producerPackageHandler.sendDeltaPackage();
  }





  public void updateViewModel() {
    if(ghost_tetromino != null && boardState.legalPosition(ghost_tetromino, 0, 0) )
      boardState.removeTetromino(ghost_tetromino);

    if(!this.viewOnly) {
      boardState.removeTetromino(current_tetromino);
      updateGhost();
      boardState.insertTetromino(ghost_tetromino);
      boardState.insertTetromino(current_tetromino);
    }

    nBoard.loadBoardState(boardState);
  }

  public boolean isDead() {
    return isDead;
  }

  public void dropTetromino() {
    boardState.dropTetromino(current_tetromino);
    int numRowsRemoved = boardState.removeFilledRows(current_tetromino.posY);
    if(numRowsRemoved > 0){
      nBoard.setNumRowsRemoved(numRowsRemoved);
      nBoard.setNumRowsRemovedLevel(numRowsRemoved);
    }
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
      current_tetromino = Utils.newTetromino(tempTetro);
      lastTetromino = tempTetro;
    } else {
      savedTetromino = lastTetromino;
      current_tetromino = newRandomTetromino();
    }

    allowedToSwitch = false;
    boardState.insertTetromino(current_tetromino);
  }

  public void updateGhost() {
    int lowestY = boardState.getLowestLegalYcoord(current_tetromino);

    ghost_tetromino = Utils.newTetromino(lastTetromino);
    ghost_tetromino.color = new Color(ghost_tetromino.color.getRed(), ghost_tetromino.color.getGreen(), ghost_tetromino.color.getBlue(), 0.8).brighter();
    ghost_tetromino.posX = current_tetromino.posX;
    ghost_tetromino.posY = lowestY;
    ghost_tetromino.isGhost = true;
    ghost_tetromino.state = current_tetromino.state;
  }

  public Tetromino newRandomTetromino() {
    lastTetromino = rnd.nextInt(7);
    return Utils.newTetromino(lastTetromino);
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
