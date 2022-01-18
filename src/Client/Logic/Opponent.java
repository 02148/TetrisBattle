package Client.Logic;

import Client.GameSession.Consumer;
import Client.GameSession.ConsumerPackageHandler;
import Client.Models.BoardState;
import Client.UI.Board;

public class Opponent {
    private Board nBoard;
    private BoardState boardState;
    private Controls controller;
    private ConsumerPackageHandler consumerPackageHandler;
    private String opponentUUID;
    private final int size = 10;
    private final int boardSize = 200;

    public Opponent(int posX, int posY, String opponentUUID) {
        try {
            this.opponentUUID = opponentUUID;
            this.nBoard = new Board(posX,posY,size);
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
