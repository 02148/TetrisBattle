package Client.Models;

import javafx.scene.paint.Color;

public class Mino {
  public int posX, posY;
  public Color color;
  public boolean isPlaced;
  public Mino(int x, int y, Color color, boolean isPlaced) {
    this.posX = x;
    this.posY = y;
    this.color = color;
    this.isPlaced = isPlaced;
  }
}
