package Client.UI;

import Client.Models.BoardState;
import Client.Models.Mino;
import Client.Models.Position;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;



public class Board extends Pane {
    private int posX, posY, size;
    private GridPane grid;
    private int numRowsRemoved = 0;

    public Board(int posX, int posY, int size) {
        super();
        this.posX = posX;
        this.posY = posY;
        this.size = size;

        grid = new GridPane();
        grid.setLayoutX(posX);
        grid.setLayoutY(posY);

        for(int y = 0; y < 20; y++) {
            for (int x = 0; x < 10; x++) {
                Canvas canvas = new Canvas(size,size);
                updateBlock(x, y, Color.BEIGE, canvas.getGraphicsContext2D());
                grid.add(canvas, x, y);
            }
        }

        this.getChildren().add(grid);
    }

    private void updateBlock(int x, int y, Color color, GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,size,size);
        gc.setFill(color);

        if(x == 9 && y == 19) {
            gc.fillRect(1,1,size-2,size-2);
        } else if(y == 19) {
            gc.fillRect(1,1,size-1,size-2);
        } else if(x == 9) {
            gc.fillRect(1,1,size-2,size-1);
        } else {
            gc.fillRect(1,1,size-1,size-1);
        }
    }

    public void setBlockColor(int x, int y, Color color) {
        int index = y*10+x;
        Canvas block = (Canvas) grid.getChildren().get(index);
        updateBlock(x, y, color, block.getGraphicsContext2D());
    }

    public void setBlockColor(int index, Color color) {
        int y = index / 10;
        int x = index % 10;
        setBlockColor(x, y, color);
    }

    public void setBlockColor(Position pos, Color color) {
        setBlockColor(pos.x, pos.y, color);
    }

    public void loadBoardState(BoardState boardState) {
        Mino[] newBoard = boardState.getBoard();
        for(int i = 0; i < newBoard.length; i++) {
            if(newBoard[i] != null) {
                setBlockColor(i, newBoard[i].color);
            } else {
                setBlockColor(i, Color.BEIGE);
            }
        }
    }
    public int getNumRowsRemoved(){
        return numRowsRemoved;
    }
    public void setNumRowsRemoved(){
        numRowsRemoved++;
    }
}
