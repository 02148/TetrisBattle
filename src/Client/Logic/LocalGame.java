package Client.Logic;

import Client.GameSession.*;
import Client.Models.BoardState;
import Client.UI.Board;
import common.Constants;
import javafx.concurrent.Task;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;

public class LocalGame implements Runnable {
    private Controls controller;
    private ProducerPackageHandler packageHandler;
    private BoardState boardState;
    private static Board nBoard;

    private FullPkgProducer fullPkgProducer;
    private DeltaPkgProducer deltaPkgProducer;
    private AttackProducer attackProducer;
    private AttackConsumer attackConsumer;

    private static boolean gameOver = false;
    private boolean stop = false;

    private final int size = 25;
    private final int boardSize = 200;

    public LocalGame(int posX, int posY, String gameUUID, String playerUUID, List<String> opponents) {
        nBoard = new Board(posX,posY,size);
        this.boardState = new BoardState(boardSize);
        this.controller = new Controls(nBoard, boardState, false);

            try {
              this.fullPkgProducer = new FullPkgProducer("tcp://" + Constants.IP_address+ ":1337/" + gameUUID+ "?keep",
                      playerUUID,
                      this.boardState);

              (new Thread(this.fullPkgProducer)).start(); // TODO anonymous thread 🤨

              this.deltaPkgProducer = new DeltaPkgProducer("tcp://" + Constants.IP_address+ ":1337/" + gameUUID + "?keep",
                      playerUUID,
                      this.boardState);

              this.attackProducer = new AttackProducer(gameUUID, playerUUID, this.controller, opponents);
              this.attackConsumer = new AttackConsumer(playerUUID, gameUUID, this.boardState);
              (new Thread(this.attackProducer)).start();
              (new Thread(this.attackConsumer)).start();



              this.packageHandler = new ProducerPackageHandler(boardSize, boardState, nBoard, deltaPkgProducer, fullPkgProducer);


            } catch(Exception e) {
              e.printStackTrace();
            }


        this.controller.setPackageHandlerFull(this.packageHandler);
        this.controller.setAttackProducer(this.attackProducer);
        this.boardState.addPackageHandler(this.packageHandler);
    }

    public Board getViewModel() {
        return nBoard;
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
