package Client;

import Client.Logic.Controls;
import Client.Models.*;
import Client.UI.Board;
import javafx.concurrent.Task;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.lang.instrument.Instrumentation;
import java.util.BitSet;
import java.util.Random;

public class GameEngine implements Runnable{
  private Controls controller;
  private Tetromino current_tetromino;
  private BoardState boardState;
  private static Board nBoard;
  private Random rnd = new Random();
  private int lastTetromino = -1;
  private int savedTetromino = -1;
  private boolean allowedToSwitch = true;
  private BitSet savedBoardState = null;
  private static Instrumentation instrumentation;
  private static boolean gameOver = false;

  public GameEngine(Board nBoard) {
    this.controller = new Controls();
    this.boardState = new BoardState(200);
    this.nBoard = nBoard;
    newRandomTetromino();

    boardState.insertTetromino(current_tetromino);
    nBoard.loadBoardState(boardState);
  }

  public Thread toThread() {
    return new Thread(this);
  }

  @Override
  public void run() {
    try {
      while(true) {
        Thread.sleep(1000);
        if(boardState.legalPosition(current_tetromino, 0, 1)) {
          controller.moveDown(current_tetromino, boardState);
          nBoard.loadBoardState(boardState);
        } else {
          controller.moveDown(current_tetromino, boardState);
          if(boardState.removeFilledRows(current_tetromino.posY)){
            nBoard.setNumRowsRemoved();
          }
          newRandomTetromino();
          allowedToSwitch = true;
        }
      }
    } catch(Exception e){}
  }

  public void newRandomTetromino() {
    lastTetromino = rnd.nextInt(7);
    newTetromino(lastTetromino);
  }

  public void newTetromino(int index) {
    switch(index) {
      case 0:
        current_tetromino = new I_Block();
        break;
      case 1:
        current_tetromino = new J_Block();
        break;
      case 2:
        current_tetromino = new L_Block();
        break;
      case 3:
        current_tetromino = new O_Block();
        break;
      case 4:
        current_tetromino = new S_Block();
        break;
      case 5:
        current_tetromino = new T_Block();
        break;
      case 6:
        current_tetromino = new Z_Block();
        break;
    }

    if(!boardState.legalPosition(current_tetromino, 0, 0 )) {
      System.out.println("YOU DEAD");
      gameOver = true;
    }

    boardState.insertTetromino(current_tetromino);
    nBoard.loadBoardState(boardState);
  }

  public void keyDownEvent(KeyEvent keyEvent) {
    controller.keyDownEvent(keyEvent.getCode(), current_tetromino, boardState);

    if(keyEvent.getCode() == KeyCode.SPACE) {
      boardState.dropTetromino(current_tetromino);
      boardState.removeFilledRows(current_tetromino.posY);

      newRandomTetromino();
      allowedToSwitch = true;
    }

    if(keyEvent.getCode() == KeyCode.P) {
      System.out.println(boardState.toString());

      BitSet bitset = boardState.toBitArray();
      for(int i = 0; i < bitset.length(); i++) {
        if(i%30==0 && i != 0)
          System.out.println();
        System.out.print(bitset.get(i) ? "1 " : "0 ");
      }
    }

    if(keyEvent.getCode() == KeyCode.C && allowedToSwitch) {
      allowedToSwitch = false;
      boardState.removeTetromino(current_tetromino);

      if(savedTetromino != -1) {
        int tempTetro = savedTetromino;
        savedTetromino = lastTetromino;
        newTetromino(tempTetro);
      } else {
        savedTetromino = lastTetromino;
        newRandomTetromino();
      }

      boardState.insertTetromino(current_tetromino);
    }

    if(keyEvent.getCode() == KeyCode.T) {
      if(savedBoardState == null) {
        savedBoardState = boardState.toBitArray();
      } else {
        boardState.setBoardStateFromBitArray(savedBoardState);
      }
    }

    nBoard.loadBoardState(boardState);
  }
  public static class TaskRun extends Task<Void> {
    @Override
    protected Void call() throws Exception {
      while(!gameOver){
        updateProgress(nBoard.getNumRowsRemoved(),50);
      }
      return null;
    }

  }
}
