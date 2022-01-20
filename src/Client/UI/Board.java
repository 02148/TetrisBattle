package Client.UI;

import Client.Models.*;
import Client.Utility.Utils;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import org.jspace.Template;


public class Board extends Pane {
    private int posX, posY, size, previewSize;
    private GridPane grid, gridForSavedTetromino;
    private GridPane[] upcomingTetros = new GridPane[4];
    private Canvas border;
    private int numRowsRemoved = 0;
    private int numRowsRemovedLevel = 0;
    private int level = 1;

    public Board(int posX, int posY, int size) {
        super();
        this.posX = posX;
        this.posY = posY;
        this.size = size;
        this.previewSize = size - 8;

        grid = new GridPane();
        grid.setLayoutX(posX);
        grid.setLayoutY(posY);

        for(int y = 0; y < 20; y++) {
            for (int x = 0; x < 10; x++) {
                Canvas canvas = new Canvas(size,size);
                updateBlock(x, y, Color.WHITE, canvas.getGraphicsContext2D());
                grid.add(canvas, x, y);
            }
        }

        this.getChildren().add(grid);
        initateSavedTetrominoFunctionality();
        //createBorder();
    }

    public Board(int size) {
        super();
        this.size = size;
        grid = new GridPane();


        for(int y = 0; y < 20; y++) {
            for (int x = 0; x < 10; x++) {
                Canvas canvas = new Canvas(size,size);
                updateBlock(x, y, Color.WHITE, canvas.getGraphicsContext2D());
                grid.add(canvas, x, y);
            }
        }

        this.getChildren().add(grid);
        initateSavedTetrominoFunctionality();
        //createBorder();
    }

    public void initateSavedTetrominoFunctionality() {
        this.gridForSavedTetromino = createSavedTetrominoGridPane(posX-size*2.5, posY-size*2.5);

        this.upcomingTetros[0] = createSavedTetrominoGridPane(posX+size*10.3, posY+size*15);
        this.upcomingTetros[1] = createSavedTetrominoGridPane(posX+size*10.3, posY+size*10);
        this.upcomingTetros[2] = createSavedTetrominoGridPane(posX+size*10.3, posY+size*5);
        this.upcomingTetros[3] = createSavedTetrominoGridPane(posX+size*10.3, posY+size*0);

        this.getChildren().add(gridForSavedTetromino);
        this.getChildren().add(this.upcomingTetros[0]);
        this.getChildren().add(this.upcomingTetros[1]);
        this.getChildren().add(this.upcomingTetros[2]);
        this.getChildren().add(this.upcomingTetros[3]);
    }

    public void createBorder() {
        this.border = new Canvas(size*20, size*30);
        this.border.setLayoutX(posX-5);
        this.border.setLayoutY(posY-5);
        this.border.getGraphicsContext2D().setFill(Color.RED);
        this.getChildren().add(this.border);
    }

    public void addBorder() {
        int lineWidth = 2;
        this.border.getGraphicsContext2D().fillRect(0,0,size*10+10,lineWidth);
        this.border.getGraphicsContext2D().fillRect(0,0,lineWidth,size*20+10);
        this.border.getGraphicsContext2D().fillRect(size*10+(10-lineWidth),0,lineWidth,size*20+10);
        this.border.getGraphicsContext2D().fillRect(0,size*20+(10-lineWidth),size*10+10,lineWidth);
    }


    public void removeBorder() {
        int lineWidth = 2;
        this.border.getGraphicsContext2D().clearRect(0,0,size*10+10,lineWidth);
        this.border.getGraphicsContext2D().clearRect(0,0,lineWidth,size*20+10);
        this.border.getGraphicsContext2D().clearRect(size*10+(10-lineWidth),0,lineWidth,size*20+10);
        this.border.getGraphicsContext2D().clearRect(0,size*20+(10-lineWidth),size*10+10,lineWidth);
    }

    public GridPane createSavedTetrominoGridPane(double posX, double posY) {
        GridPane savedTetromino = new GridPane();
        savedTetromino.setLayoutX(posX);
        savedTetromino.setLayoutY(posY);
        for(int y = 0; y < 4; y++) {
            for(int x = 0; x < 4; x++) {
                Canvas canvas = new Canvas(previewSize, previewSize);
                makeCanvasTransparent(canvas.getGraphicsContext2D());
                savedTetromino.add(canvas, x ,y);
            }
        }
        return savedTetromino;
    }

    public void createSavedBlock(Tetromino tetromino) {
        updateBlockPreview(gridForSavedTetromino, tetromino);
    }

    public void updateBlockPreview(GridPane gp, Tetromino tetromino) {
        Platform.runLater(() -> {
            int[] basicRotation = tetromino.getRotation(0);
            for(int y = 0; y < 4; y++) {
                for(int x = 0; x < 4; x++) {
                    if(basicRotation[y*4+x] == 1)
                        paintCanvasWithColor(tetromino.color, ((Canvas)gp.getChildren().get(y*4+x)).getGraphicsContext2D());
                    else
                        makeCanvasTransparent(((Canvas)gp.getChildren().get(y*4+x)).getGraphicsContext2D());
                }
            }
        });
    }

    public void paintCanvasWithColor(Color color, GraphicsContext gc) {
        gc.setFill(color);
        gc.fillRect(1,1,previewSize-2,previewSize-2);
    }

    public void makeCanvasTransparent(GraphicsContext gc) {
        gc.clearRect(1,1,previewSize-2,previewSize-2);
    }



    public void updateUpcomingBlock(int[] nextBlocks) {
        for(int i = 0; i < upcomingTetros.length; i++) {
            updateBlockPreview(upcomingTetros[i], Utils.newTetromino(nextBlocks[i]));
        }
    }


    private void updateBlock(int x, int y, Color color, GraphicsContext gc) {
        Platform.runLater(() -> {
            gc.setFill(Color.rgb(194, 233, 244));
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
        });
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
                setBlockColor(i, Color.WHITE);
            }
        }
    }
    public int getNumRowsRemoved(){
        return numRowsRemoved;
    }

    public void setNumRowsRemoved(int n){
        numRowsRemoved += n;
    }

    public int getNumRowsRemovedLevel(){
        return numRowsRemovedLevel;
    }

    public void setNumRowsRemovedLevel(int n){
        numRowsRemovedLevel += n;
    }

    public void resetNumRowsRemovedLevel(){
        numRowsRemovedLevel = 0;
    }

    public int getLevel(){
        return level;
    }

    public void setLevel(int n){
        level += n;
    }
}
