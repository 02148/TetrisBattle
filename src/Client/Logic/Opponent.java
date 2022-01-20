package Client.Logic;

import Client.GameSession.Consumer;
import Client.GameSession.ConsumerPackageHandler;
import Client.Models.BoardState;
import Client.UI.Board;
import javafx.scene.input.KeyEvent;

public class Opponent {
    private Board nBoard;
    private BoardState boardState;
    private Controls controller;
    private String opponentUUID;
    private ConsumerPackageHandler consumerPackageHandler;
    private final int size = 12;
    private final int boardSize = 200;

    public Opponent( String userUUID) {
        try {
            this.opponentUUID =  userUUID;
            this.nBoard = new Board(size, true);
            this.boardState = new BoardState(boardSize);
            this.consumerPackageHandler = new ConsumerPackageHandler(boardSize, boardState, nBoard);
            this.controller = new Controls(nBoard, boardState, true);
        } catch (Exception e) {}
    }

    public Board getBoardView() {
        return this.nBoard;
    }

    public ConsumerPackageHandler getConsumerPackageHandler() {
        return consumerPackageHandler;
    }
}
