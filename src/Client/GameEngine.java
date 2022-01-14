package Client;

import Client.GameSession.PackageHandler;
import Client.Logic.Controls;
import Client.Models.*;
import Client.UI.Board;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;

public class GameEngine implements Runnable{
  private Controls controller;
  private PackageHandler packageHandler;
  private BoardState boardState;
  private static Board nBoard;

  private static boolean gameOver = false;
  private boolean stop = false;

  public GameEngine(Board nBoard) {
    this.packageHandler = new PackageHandler(200);
    this.boardState = new BoardState(200);
    this.boardState.addPackageHandler(this.packageHandler);
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

  public static class TaskRunLines extends Task<Void> {
    @Override
    protected Void call() throws Exception {
      while(!gameOver){
        updateProgress(nBoard.getNumRowsRemoved(),50);
        if(nBoard.getNumRowsRemovedLevel() > 9){
          nBoard.setLevel(1);
          nBoard.resetNumRowsRemovedLevel();
          updateMessage(Integer.toString(nBoard.getLevel()));
        }
      }
      return null;
    }
  }
}
