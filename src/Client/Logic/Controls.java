package Client.Logic;

import Client.GameSession.AttackProducer;
import Client.GameSession.ProducerPackageHandler;
import Client.Models.*;
import Client.UI.Board;
import Client.Utility.Utils;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.ArrayList;
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
  private int selectedOpponent    = -1;
  private int[] upcomingTetros    = new int[4];
  private boolean allowedToSwitch = true;
  private boolean isDead          = false;
  private BitSet savedBoardState  = null;
  private ArrayList<Board> opponentBoards;

  private ProducerPackageHandler producerPackageHandler;
  private AttackProducer attackProducer;
  private AttackProducer attackConsumer;


  public Controls(Board nBoard, BoardState boardState, boolean viewOnly) {
    this.nBoard = nBoard;
    this.boardState = boardState;
    if(!viewOnly)
      this.current_tetromino = newRandomTetromino();
    this.viewOnly = viewOnly;

    updateViewModel();

    for(int i = 0; i < upcomingTetros.length; i++) {
      upcomingTetros[i] = rnd.nextInt(7);
    }

    nBoard.updateUpcomingBlock(upcomingTetros);

//    if(!this.viewOnly)
//      this.packageHandlerProducer.sendDeltaPackage();
  }

  public void setPackageHandlerFull(ProducerPackageHandler producerPackageHandler) {
    this.producerPackageHandler = producerPackageHandler;
  }

  public void setAttackProducer(AttackProducer attackProducer) {
    this.attackProducer = attackProducer;
  }

  public void setOpponentBoards(ArrayList<Board> opponentBoards) { this.opponentBoards = opponentBoards; }


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
      nBoard.createSavedBlock(Utils.newTetromino(savedTetromino));
    } else if(keyCode == KeyCode.P) {
      print();
    } else if(keyCode == KeyCode.DIGIT1 || keyCode == KeyCode.DIGIT2 || keyCode == KeyCode.DIGIT3 || keyCode == KeyCode.DIGIT4 || keyCode == KeyCode.DIGIT5 || keyCode == KeyCode.DIGIT6 || keyCode == KeyCode.DIGIT7 || keyCode == KeyCode.DIGIT8) {
      toggleSelectedOpponent(keyCode);
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
      this.attackProducer.queueAttack(numRowsRemoved);
    }

    if(this.boardState.getAttackQueue() > 0) {
      this.boardState.addRows(this.boardState.getAttackQueue());
      this.boardState.resetAttackQueue();
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
    Tetromino nextTetro = Utils.newTetromino(upcomingTetros[upcomingTetros.length-1]);
    lastTetromino = upcomingTetros[upcomingTetros.length-1];
    for(int i = upcomingTetros.length-1; i >= 1; i--) {
      upcomingTetros[i] = upcomingTetros[i-1];
    }
    upcomingTetros[0]  = rnd.nextInt(7);
    nBoard.updateUpcomingBlock(upcomingTetros);
    return nextTetro;
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

  public void toggleSelectedOpponent(KeyCode digitKey) {
    if(selectedOpponent != -1)
      addOrRemoveOpponentBorder(false, selectedOpponent);
    selectedOpponent = (selectedOpponent == digitKey.getCode()- 49) ? -1 : (digitKey.getCode() - 49);
    if(selectedOpponent != -1)
      addOrRemoveOpponentBorder(true, selectedOpponent);
  }

  public void addOrRemoveOpponentBorder(boolean doAdd, int opponentId) {
    if(opponentBoards.size() > opponentId) {
      if(doAdd) {
        opponentBoards.get(opponentId).addBorder();
      } else {
        opponentBoards.get(opponentId).removeBorder();
      }
    }
  }

  public int getSelectedOpponent() {
    return this.selectedOpponent;
  }
}
