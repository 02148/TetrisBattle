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
  private BoardState boardState;
  private static Board nBoard;

  private static boolean gameOver = false;
  private boolean stop = false;

  public GameEngine(Board nBoard) {
    this.boardState = new BoardState(200);
    this.nBoard = nBoard;
    this.controller = new Controls(nBoard, boardState);
  }

  public Thread toThread() {
    return new Thread(this);
  }

  @Override
  public void run() {
    try {
      while(!stop && !gameOver) {
        Thread.sleep(1000);

        controller.gameTick();

        if(controller.isDead()) {
          System.out.println("YOU DEAD");
          gameOver = true;
        }
      }
    } catch(Exception e){}
  }

  public void stop() {
    this.stop = true;
  }

  public void keyDownEvent(KeyEvent keyEvent) {
    if(!gameOver && !stop)
      controller.keyDownEvent(keyEvent.getCode());
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
