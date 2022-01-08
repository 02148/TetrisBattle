package Client.Models;

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
        boolean indexIsOutOfBounds = (posY+y) >= 20 || (posY+y<0) || (posX+x) >= 10 || (posX+x) < 0;
        boolean overlapsAnotherBlock = state[index] == 1 && !indexIsOutOfBounds && this.board[boardIndex] != null && this.board[boardIndex].isPlaced;
        boolean tetrominoGoesOutOfBound = state[index] == 1 && indexIsOutOfBounds;
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
}
