package Client.Models;


import javafx.scene.paint.Color;

import java.util.BitSet;

public class BoardState {
  private Mino[] board;

  public BoardState(int size) { // Size is equal to the amount of cells in the tetris grid
    this.board = new Mino[size];
  }

  public Mino[] getBoard() {
    return board;
  }

  public void setBoard(Mino[] board) {
    this.board = board;
  }

  public void insertTetromino(Tetromino tetromino) {
    removeOrInsertTetromino(tetromino, true, false);
  }

  public void removeTetromino(Tetromino tetromino) {
    removeOrInsertTetromino(tetromino, false, false);
  }

  public void placeTetromino(Tetromino tetromino) {
    removeOrInsertTetromino(tetromino, true, true);
  }

  private void removeOrInsertTetromino(Tetromino tetromino, boolean insertOrRemove, boolean isPlaced) { // true => insert
    int[] currentState = tetromino.getCurrentRotation();
    int posX = tetromino.posX;
    int posY = tetromino.posY;

    for(int x = 0; x < 4; x++) {
      for(int y = 0; y < 4; y++) {
        int index = y*4+x;
        int boardIndex = (posY + y)*10 + (posX+x);
        if(currentState[index] == 1 && boardIndex >= 0 && boardIndex < 200) {
          board[boardIndex] = insertOrRemove ? new Mino(posX+x, posY+y, tetromino.color, isPlaced) : null;
        }
      }
    }
  }

  public boolean legalPosition(Tetromino tetromino, int offsetX, int offsetY) {
    return legalPosition(tetromino.getCurrentRotation(), tetromino.posX+offsetX, tetromino.posY+offsetY);
  }

  public boolean legalPosition(int[] state, int posX, int posY) {
    for(int x = 0; x < 4; x++) {
      for(int y = 0; y < 4; y++) {
        int index = y*4+x;
        int boardIndex = (posY + y)*10 + (posX+x);
        boolean indexIsOutOfBounds = (posY+y) >= 20 || (posY+y<0) || (posX+x) >= 10 || (posX+x) < 0; // Is the position within the board?
        boolean indexIsOutOfBoundsButNotTop = (posY+y) >= 20 || (posX+x) >= 10 || (posX+x) < 0; // Is the position within the board, but ignoring the top (Used when spawning, since the tetrominos can spawn with their upper half over the top, where the user should be able to move it)
        boolean overlapsAnotherBlock = state[index] == 1 && !indexIsOutOfBounds && this.board[boardIndex] != null && this.board[boardIndex].isPlaced; // Is there a mino in the way?
        boolean tetrominoGoesOutOfBound = state[index] == 1 && indexIsOutOfBoundsButNotTop; // Is the tetromino about to go out of the board?
        if(overlapsAnotherBlock || tetrominoGoesOutOfBound) {
          return false;
        }
      }
    }
    return true;
  }

  // Returns the index of which wall kick rotation is legal. -1 means no legal rotation was found.
  public int getLegalRotation(int state[], int[][] wallKickData, int posX, int posY ) {
    for(int i = 0; i < wallKickData.length; i++) {
      if(legalPosition(state, posX+wallKickData[i][0], posY+wallKickData[i][1])) {
        return i;
      }
    }
    return -1;
  }

  // When using space to drop the tetromino
  public void dropTetromino(Tetromino tetromino) {
    int[] state = tetromino.getCurrentRotation();
    int posX = tetromino.posX;
    int posY = tetromino.posY;

    removeTetromino(tetromino);

    for(int i = posY+1; i <= 20; i++) {
      if(!legalPosition(state, posX, i)) {
        tetromino.posY = i-1;
        placeTetromino(tetromino);
        return;
      }
    }
  }

  // Removes the rows that are filled. It will check row at posY and the following three rows, since a tetromino can, at most, influence 4 rows.
  public boolean removeFilledRows(int posY) {
    boolean rowRemoved = false;
    for(int y = posY; y < posY+4 && y < 20; y++) {
      if(y < 0) continue;

      for(int x = 0; x < 10; x++) {
        int index = y*10+x;

        if(board[index] == null) {
          break;
        } else if(x == 9) {
          removeRow(y);
          rowRemoved = true;
        }

      }
    }
    return  rowRemoved;
  }

  public void removeRow(int y) {
    for(int index = y*10+9; index >= 0; index--) {
      if(index > 9) {
        board[index] = board[index-10];
      } else {
        board[index] = null;
      }
    }
  }

  public String toString() {
    StringBuilder s = new StringBuilder();
    for(int index = 0; index < 200; index++) {
      if(index % 10 == 0 && index != 0) {
        s.append("\n");
      }

      String os = System.getProperty("os.name");
      if(board[index] == null) {
        if (os.equals("Mac OS X"))
          s.append("⬜");
        else
          s.append("🟨");
      } else {
        if (os.equals("Windows 10"))
        {
          s.append("🟥");
        } else {
          if (board[index].color.equals(Color.BLUE)) {
            s.append("🟦");
          } else if(board[index].color.equals(Color.CYAN)) {
            s.append("🟪");
          } else if(board[index].color.equals(Color.ORANGE)) {
            s.append("🟧");
          } else if(board[index].color.equals(Color.YELLOW)) {
            s.append("🟨");
          } else if(board[index].color.equals(Color.GREEN)) {
            s.append("🟩");
          } else if(board[index].color.equals(Color.PINK)) {
            s.append("🟫");
          } else if(board[index].color.equals(Color.RED)) {
            s.append("🟥");
          } else {
            s.append("⬛");
          }
        }
      }
      s.append(" ");
    }
    return s.toString();
  }

  public BitSet toBitArray() {
    BitSet bitArray = new BitSet(200*3);

    for(int i = 0; i < 200; i++) {
      if(board[i] == null) {
        bitArray.set(i*3, false); bitArray.set(i*3+1, false); bitArray.set(i*3+2, false);
      } else if (board[i].color.equals(Color.BLUE)) {
        bitArray.set(i*3, false); bitArray.set(i*3+1, false); bitArray.set(i*3+2, true);
      } else if(board[i].color.equals(Color.CYAN)) {
        bitArray.set(i*3, false); bitArray.set(i*3+1, true); bitArray.set(i*3+2, false);
      } else if(board[i].color.equals(Color.ORANGE)) {
        bitArray.set(i*3, false); bitArray.set(i*3+1, true); bitArray.set(i*3+2, true);
      } else if(board[i].color.equals(Color.YELLOW)) {
        bitArray.set(i*3, true); bitArray.set(i*3+1, false); bitArray.set(i*3+2, false);
      } else if(board[i].color.equals(Color.GREEN)) {
        bitArray.set(i*3, true); bitArray.set(i*3+1, false); bitArray.set(i*3+2, true);
      } else if(board[i].color.equals(Color.PINK)) {
        bitArray.set(i*3, true); bitArray.set(i*3+1, true); bitArray.set(i*3+2, false);
      } else if(board[i].color.equals(Color.RED)) {
        bitArray.set(i*3, true); bitArray.set(i*3+1, true); bitArray.set(i*3+2, true);
      }
    }

    return bitArray;
  }

  public void setBoardStateFromBitArray(BitSet bitArray) {
    for(int index = 0; index < bitArray.length(); index += 3) {
      int indexInBoard = index/3;
      boolean leftBit = bitArray.get(index);
      boolean middleBit = bitArray.get(index+1);
      boolean rightBit = bitArray.get(index+2);

      if(!leftBit && !middleBit && !rightBit) {
        board[indexInBoard] = null;
      } else if (!leftBit && !middleBit && rightBit) {
        board[indexInBoard] = new Mino(indexInBoard, Color.BLUE, true);
      } else if(!leftBit && middleBit && !rightBit) {
        board[indexInBoard] = new Mino(indexInBoard, Color.CYAN, true);
      } else if(!leftBit && middleBit && rightBit) {
        board[indexInBoard] = new Mino(indexInBoard, Color.ORANGE, true);
      } else if(leftBit && !middleBit && !rightBit) {
        board[indexInBoard] = new Mino(indexInBoard, Color.YELLOW, true);
      } else if(leftBit && !middleBit && rightBit) {
        board[indexInBoard] = new Mino(indexInBoard, Color.GREEN, true);
      } else if(leftBit && middleBit && !rightBit) {
        board[indexInBoard] = new Mino(indexInBoard, Color.PINK, true);
      } else if(leftBit && middleBit && rightBit) {
        board[indexInBoard] = new Mino(indexInBoard, Color.RED, true);
      }
    }
  }
}
