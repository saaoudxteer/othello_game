package fr.univ_amu.m1info.othello;

import fr.univ_amu.m1info.othello.Coordinates;

/**
 * Represents the Othello game board and manages the game state.
 * This class handles piece placement, game logic, and board state.
 */
public class OthelloBoard {
    private static final int BOARD_SIZE = 8;
    private final CellState[][] board;

    /**
     * Creates a new Othello board with the standard starting configuration.
     */
    public OthelloBoard() {
        board = new CellState[BOARD_SIZE][BOARD_SIZE];
        reset();
    }

    /**
     * Initializes the board with the standard Othello starting position:
     * 4 pieces in the center (2 black, 2 white).
     */
    private void initializeBoard() {
        // Standard Othello starting position
        board[3][3] = CellState.WHITE;
        board[3][4] = CellState.BLACK;
        board[4][3] = CellState.BLACK;
        board[4][4] = CellState.WHITE;
    }

    /**
     * Resets the board to its initial state.
     */
    public void reset() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = CellState.EMPTY;
            }
        }
        initializeBoard();
    }

    /**
     * Checks if a cell is empty (no piece placed).
     *
     * @param row the row of the cell.
     * @param column the column of the cell.
     * @return true if the cell is empty, false otherwise.
     */
    public boolean isEmpty(int row, int column) {
        return isValidPosition(row, column) && board[row][column].isEmpty();
    }

    /**
     * Gets the player who has a piece at the specified position.
     *
     * @param row the row of the cell.
     * @param column the column of the cell.
     * @return the player at this position, or null if empty.
     */
    public Player getPlayerAt(int row, int column) {
        if (!isValidPosition(row, column)) {
            return null;
        }
        return board[row][column].toPlayer();
    }

    /**
     * Places a piece for the specified player at the given position.
     *
     * @param row the row where to place the piece.
     * @param column the column where to place the piece.
     * @param player the player placing the piece.
     * @throws IllegalArgumentException if the position is invalid or not empty.
     */
    public void placePiece(int row, int column, Player player) {
        if (!isValidPosition(row, column)) {
            throw new IllegalArgumentException("Invalid position: (" + row + ", " + column + ")");
        }
        if (!isEmpty(row, column)) {
            throw new IllegalArgumentException("Cell is not empty: (" + row + ", " + column + ")");
        }
        board[row][column] = CellState.fromPlayer(player);
    }

    /**
     * Checks if the given position is within the board boundaries.
     *
     * @param row the row to check.
     * @param column the column to check.
     * @return true if the position is valid, false otherwise.
     */
    private boolean isValidPosition(int row, int column) {
        return row >= 0 && row < BOARD_SIZE && column >= 0 && column < BOARD_SIZE;
    }

    /**
     * Counts the number of pieces for a specific player.
     *
     * @param player the player to count pieces for.
     * @return the number of pieces belonging to the player.
     */
    public int countPieces(Player player) {
        int count = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col].toPlayer() == player) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Gets the size of the board.
     *
     * @return the board size (8 for standard Othello).
     */
    public int getSize() {
        return BOARD_SIZE;
    }

    /**
     * Checks if placing a piece at the specified position is a valid move for the given player.
     * @param row the row of the cell to check.
     * @param column the column of the cell to check.
     * @param player the player making the move.
     * @return true if the move is valid, false otherwise.
     */
    public boolean isValidMove(int row, int column, Player player) {

        if (!isEmpty(row, column)) {
            return false;
        }

        // directions : N, NE, E, SE, S, SW, W, NW
        int[] dr = {-1, -1, 0, 1, 1, 1, 0, -1};
        int[] dc = { 0, 1, 1, 1, 0, -1, -1, -1};

        for (int d = 0; d < dr.length; d++) {

            int r = row + dr[d];
            int c = column + dc[d];

            // must have at least one opponent piece adjacent in this direction
            if (getPlayerAt(r, c) != player.getOpponent()) {
                continue;
            }

            // keep going until we find player's piece (valid) or empty/null/border (invalid)
            r += dr[d];
            c += dc[d];

            while (isValidPosition(r, c)) {

                Player p = getPlayerAt(r, c);
                if (p == null) break; // empty -> not a capture in this direction
                if (p == player) return true; // found bracketing piece -> valid move
                // otherwise it's still opponent, continue
                r += dr[d];
                c += dc[d];
            }
        }

        return false;
    }

    // 5A: Identify lines where opponent pieces are bracketed
    /**
     * Finds all opponent pieces that would be flipped in a specific direction
     * from the given position.
     *
     * @param row the starting row position.
     * @param column the starting column position.
     * @param rowDelta the row direction (-1, 0, or 1).
     * @param colDelta the column direction (-1, 0, or 1).
     * @param player the player making the move.
     * @return an array of positions [row, col] that should be flipped, or empty array if none.
     */
    private java.util.List<Coordinates> findFlippablePiecesInDirection(int row, int column, int rowDelta, int colDelta, Player player) {
        java.util.List<Coordinates> flippable = new java.util.ArrayList<>();

        int r = row + rowDelta;
        int c = column + colDelta;

        // Collect opponent pieces
        while (isValidPosition(r, c) && getPlayerAt(r, c) == player.getOpponent()) {
            flippable.add(new Coordinates(r, c));
            r += rowDelta;
            c += colDelta;
        }

        // Check if we found a bracketing piece (player's own piece)
        if (isValidPosition(r, c) && getPlayerAt(r, c) == player && !flippable.isEmpty()) {
            return flippable;
        }

        // No valid bracket found
        return java.util.Collections.emptyList();
    }

    /**
     * Finds all opponent pieces that would be flipped if the player places a piece
     * at the specified position.
     *
     * @param row the row where the piece would be placed.
     * @param column the column where the piece would be placed.
     * @param player the player making the move.
     * @return a list of all positions [row, col] that would be flipped.
     */
    public java.util.List<Coordinates> findAllFlippablePieces(int row, int column, Player player) {
        java.util.List<Coordinates> allFlippable = new java.util.ArrayList<>();

        // Check all 8 directions: N, NE, E, SE, S, SW, W, NW
        int[] dr = {-1, -1, 0, 1, 1, 1, 0, -1};
        int[] dc = { 0, 1, 1, 1, 0, -1, -1, -1};

        for (int d = 0; d < dr.length; d++) {
            allFlippable.addAll(findFlippablePiecesInDirection(row, column, dr[d], dc[d], player));
        }

        return allFlippable;
    }

    // 5B: Flip the color of bracketed pieces
    /**
     * Flips the piece at the specified position to the given player's color.
     *
     * @param row the row of the piece to flip.
     * @param column the column of the piece to flip.
     * @param player the player color to flip to.
     * @throws IllegalArgumentException if the position is invalid or empty.
     */
    public void flipPiece(int row, int column, Player player) {
        if (!isValidPosition(row, column)) {
            throw new IllegalArgumentException("Invalid position: (" + row + ", " + column + ")");
        }
        if (board[row][column].isEmpty()) {
            throw new IllegalArgumentException("Cannot flip empty cell: (" + row + ", " + column + ")");
        }
        board[row][column] = CellState.fromPlayer(player);
    }

    // 5C: Execute a move and update the board
    /**
     * Executes a complete move: places the piece and flips all bracketed opponent pieces.
     * This is the main method to use for making a move in the game.
     *
     * @param row the row where to place the piece.
     * @param column the column where to place the piece.
     * @param player the player making the move.
     * @return the number of pieces that were flipped.
     * @throws IllegalArgumentException if the move is invalid.
     */
    public int executeMove(int row, int column, Player player) {
        if (!isValidMove(row, column, player)) {
            throw new IllegalArgumentException("Invalid move");
        }

        // Find all pieces to flip
        java.util.List<Coordinates> piecesToFlip = findAllFlippablePieces(row, column, player);

        // Place the piece
        placePiece(row, column, player);

        // Flip all bracketed pieces
        for (Coordinates coord : piecesToFlip) {
            flipPiece(coord.row(), coord.column(), player);
        }

        return piecesToFlip.size();
    }

    // US 12A: Check if a player has any valid moves
    /**
     * Checks if the specified player has at least one valid move available.
     *
     * @param player the player to check for valid moves.
     * @return true if the player has at least one valid move, false otherwise.
     */
    public boolean hasValidMoves(Player player) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (isValidMove(row, col, player)) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Returns a deep copy snapshot of the current board as CellState[][]
     */
    public CellState[][] getBoardSnapshot() {
        CellState[][] snap = new CellState[BOARD_SIZE][BOARD_SIZE];
        for (int r = 0; r < BOARD_SIZE; r++) {
            System.arraycopy(board[r], 0, snap[r], 0, BOARD_SIZE);
        }
        return snap;
    }

    /**
     * Restores the board from a snapshot.
     */
    public void setBoardFromSnapshot(CellState[][] snapshot) {
        if (snapshot == null || snapshot.length != BOARD_SIZE || snapshot[0].length != BOARD_SIZE) {
            throw new IllegalArgumentException("Invalid snapshot size");
        }
        for (int r = 0; r < BOARD_SIZE; r++) {
            System.arraycopy(snapshot[r], 0, board[r], 0, BOARD_SIZE);
        }
    }

}
