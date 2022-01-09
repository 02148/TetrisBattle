package Client;

import Client.Logic.Controls;
import Client.Models.*;
import Client.UI.Board;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Random;

public class GameEngine implements Runnable{
  private Controls controller;
  private Tetromino current_tetromino;
  private BoardState boardState;
  private Board nBoard;
  private Random rnd = new Random();

  public GameEngine(Board nBoard) {
    this.controller = new Controls();
    this.current_tetromino = new I_Block();
    this.boardState = new BoardState(200);
    this.nBoard = nBoard;

    boardState.insertTetromino(current_tetromino);
    nBoard.setBoard(boardState);
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
          nBoard.setBoard(boardState);
        } else {
          controller.moveDown(current_tetromino, boardState);
          boardState.removeFilledRows(current_tetromino.posY);
          newTetromino();
        }
      }
    } catch(Exception e){}
  }

  public void newTetromino() {
    switch(rnd.nextInt(7)) {
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

    boardState.insertTetromino(current_tetromino);
    nBoard.setBoard(boardState);
  }

  public void keyDownEvent(KeyEvent keyEvent) {
    controller.keyDownEvent(keyEvent.getCode(), current_tetromino, boardState);

    if(keyEvent.getCode() == KeyCode.SPACE) {
      boardState.dropTetromino(current_tetromino);
      boardState.removeFilledRows(current_tetromino.posY);
      newTetromino();
    }

    nBoard.setBoard(boardState);
  }
}
