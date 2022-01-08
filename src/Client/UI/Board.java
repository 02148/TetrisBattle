package Client.UI;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;



public class Board extends GridPane {
    private int posX, posY, width, height;

     public Board(int posX, int posY, int width,int height) {

        this.setWidth(400);
        this.setHeight(800);
        this.setLayoutX(posX);
        this.setLayoutY(posY);

        for(int y = 0; y < 20; y++) {
            for (int x = 0; x < 10; x++) {
                Canvas canvas = new Canvas(20,20);
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.setFill(Color.BLACK);
                gc.fillRect(0,0,20,20);
                gc.setFill(Color.BEIGE);

                if(x == 0) {
                    if(y == 19) {
                        gc.fillRect(1,1,19,19);
                    } else {
                        gc.fillRect(1,1,19,19);
                    }
                } else if(x == 9) {
                    if (y == 19) {

                    } else {
                        gc.fillRect(1,1,18,19);
                    }
                } else {
                    gc.fillRect(1,1,19,19);
                }


                this.add(canvas, x, y);
            }
        }


        this.width = width;
        this.height = height;

    }
}
