package Client;

import Client.Logic.Controls;
import Client.Models.*;
import Client.UI.Board;
import javafx.concurrent.Task;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.lang.instrument.Instrumentation;
import java.util.BitSet;
import java.util.Random;

public class GameEngine implements Runnable{
  private Controls controller;
  private Tetromino current_tetromino;
  private Tetromino ghost_tetromino;
  private BoardState boardState;
  private static Board nBoard;
  private Random rnd = new Random();
  private int lastTetromino = -1;
  private int savedTetromino = -1;
  private boolean allowedToSwitch = true;
  private BitSet savedBoardState = null;
  private static Instrumentation instrumentation;
  private static boolean gameOver = false;
  private boolean stop = false;

  public GameEngine(Board nBoard) {
    this.controller = new Controls();
    this.boardState = new BoardState(200);
    this.nBoard = nBoard;
    current_tetromino = newRandomTetromino();
    boardState.insertTetromino(current_tetromino);
    nBoard.loadBoardState(boardState);
  }

  public Thread toThread() {
    return new Thread(this);
  }

  @Override
  public void run() {
    try {
      while(!stop) {
        Thread.sleep(1000);
        if(boardState.legalPosition(current_tetromino, 0, 1)) {
          controller.moveDown(current_tetromino, boardState);
        } else {
          controller.moveDown(current_tetromino, boardState);
          if(boardState.removeFilledRows(current_tetromino.posY)){
            nBoard.setNumRowsRemoved();
          }
          current_tetromino = newRandomTetromino();
          boardState.insertTetromino(current_tetromino);
          allowedToSwitch = true;
        }

        if(!boardState.legalPosition(current_tetromino, 0, 0 )) {
          System.out.println("YOU DEAD");
          gameOver = true;
        }

        nBoard.loadBoardState(boardState);
      }
    } catch(Exception e){}
  }

  public void stop() {
    this.stop = true;
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

  public void keyDownEvent(KeyEvent keyEvent) {
    controller.keyDownEvent(keyEvent.getCode(), current_tetromino, boardState);

    if(keyEvent.getCode() == KeyCode.SPACE) {
      controller.dropTetromino(current_tetromino, boardState);

      current_tetromino = newRandomTetromino();
      boardState.insertTetromino(current_tetromino);
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
        current_tetromino = newTetromino(tempTetro);
      } else {
        savedTetromino = lastTetromino;
        current_tetromino = newRandomTetromino();
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

    // Everytime a key is pressed, update the shadow
    // This is also done in every gametick
    int lowestY = boardState.getLowestLegalYcoord(current_tetromino);
    if(ghost_tetromino != null && boardState.legalPosition(ghost_tetromino, 0, 0) )
      boardState.removeTetromino(ghost_tetromino);
    ghost_tetromino = newTetromino(lastTetromino);
    ghost_tetromino.color = new Color(ghost_tetromino.color.getRed(), ghost_tetromino.color.getGreen(), ghost_tetromino.color.getBlue(), 0.8);
    ghost_tetromino.color.brighter();
    ghost_tetromino.posX = current_tetromino.posX;
    ghost_tetromino.posY = lowestY;
    ghost_tetromino.state = current_tetromino.state;
    boardState.insertTetromino(ghost_tetromino);

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
