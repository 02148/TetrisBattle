package Client.Logic;

import Client.GameSession.Consumer;
import Client.GameSession.PackageHandlerConsumer;
import Client.Models.BoardState;
import Client.UI.Board;

public class Opponent {
    private Board nBoard;
    private BoardState boardState;
    private Controls controller;
    private PackageHandlerConsumer packageHandlerConsumer;
    private final int size = 12;
    private final int boardSize = 200;

    public Opponent( String userUUID) {
        try {
            this.nBoard = new Board(size);
            this.boardState = new BoardState(boardSize);
            this.packageHandlerConsumer = new PackageHandlerConsumer(boardSize, boardState, nBoard);

            Consumer consumerFull = new Consumer(userUUID, packageHandlerConsumer, "full"); // haps haps full
            Consumer consumerDelta = new Consumer(userUUID, packageHandlerConsumer, "delta"); // haps haps delta

            this.controller = new Controls(nBoard, boardState, true);

            (new Thread(consumerFull)).start();
            (new Thread(consumerDelta)).start();
        } catch (Exception e) {}
    }

    public Board getBoardView() {
        return this.nBoard;
    }


}
