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

  public Mino(int index, Color color, boolean isPlaced) {
      this.posX = index%10;
      this.posY = index/10;
      this.color = color;
      this.isPlaced = isPlaced;
  }

  public byte toColorCode() {
    String c;

    if (this.color.equals(Color.BLUE))
      c = "0";
    else if(this.color.equals(Color.CYAN))
     c = "1";
    else if(this.color.equals(Color.ORANGE))
     c = "2";
    else if(this.color.equals(Color.YELLOW))
     c = "3";
    else if(this.color.equals(Color.GREEN))
     c = "4";
    else if(this.color.equals(Color.PINK))
     c = "5";
    else if(this.color.equals(Color.RED))
     c = "6";
    else c = "7";

    return Byte.parseByte(c);
  }
}
