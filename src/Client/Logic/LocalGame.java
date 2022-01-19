package Client.Logic;

import Client.GameSession.DeltaPkgProducer;
import Client.GameSession.FullPkgProducer;
import Client.GameSession.ProducerPackageHandler;
import Client.Models.BoardState;
import Client.UI.Board;
import javafx.concurrent.Task;
import javafx.scene.input.KeyEvent;

public class LocalGame implements Runnable {
    private Controls controller;
    private ProducerPackageHandler packageHandler;
    private BoardState boardState;
    private static Board nBoard;

    private FullPkgProducer fullPkgProducer;
    private DeltaPkgProducer deltaPkgProducer;

    private static boolean gameOver = false;
    private boolean stop = false;

    private final int size = 25;
    private final int boardSize = 200;

    public LocalGame(int posX, int posY, String gameUUID, String playerUUID) {
        nBoard = new Board(posX,posY,size);
        this.boardState = new BoardState(boardSize);

            try {
              this.fullPkgProducer = new FullPkgProducer("tcp://localhost:1337/" + gameUUID+ "?keep",
                      playerUUID,
                      this.boardState);

              (new Thread(this.fullPkgProducer)).start(); // TODO anonymous thread 🤨

              this.deltaPkgProducer = new DeltaPkgProducer("tcp://localhost:1337/" + gameUUID + "?keep",
                      playerUUID,
                      this.boardState);

              this.packageHandler = new ProducerPackageHandler(boardSize, boardState, nBoard, deltaPkgProducer, fullPkgProducer);


            } catch(Exception e) {
              e.printStackTrace();
            }



        this.controller = new Controls(nBoard, boardState, false);
        this.controller.setPackageHandlerFull(this.packageHandler);
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
      updateTitle("Game Over");
      return null;
    }
  }
}
