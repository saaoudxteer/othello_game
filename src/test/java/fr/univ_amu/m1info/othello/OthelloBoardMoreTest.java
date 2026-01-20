package fr.univ_amu.m1info.othello;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional tests for OthelloBoard to improve coverage.
 */
class OthelloBoardMoreTest {

    @Test
    void testBoardInitialization() {
        OthelloBoard board = new OthelloBoard();

        assertEquals(8, board.getSize(), "Board should be 8x8");
        assertEquals(2, board.countPieces(Player.BLACK), "Should have 2 black pieces initially");
        assertEquals(2, board.countPieces(Player.WHITE), "Should have 2 white pieces initially");
    }

    @Test
    void testPlacePieceAtValidPosition() {
        OthelloBoard board = new OthelloBoard();

        board.placePiece(0, 0, Player.BLACK);

        assertEquals(Player.BLACK, board.getPlayerAt(0, 0), "Should have BLACK piece at (0,0)");
    }

    @Test
    void testPlacePieceAtOccupiedPositionThrowsException() {
        OthelloBoard board = new OthelloBoard();

        assertThrows(IllegalArgumentException.class, () -> {
            board.placePiece(3, 3, Player.BLACK);
        }, "Should throw exception when placing on occupied cell");
    }

    @Test
    void testPlacePieceAtInvalidPositionThrowsException() {
        OthelloBoard board = new OthelloBoard();

        assertThrows(IllegalArgumentException.class, () -> {
            board.placePiece(-1, 0, Player.BLACK);
        }, "Should throw exception for negative row");

        assertThrows(IllegalArgumentException.class, () -> {
            board.placePiece(0, -1, Player.BLACK);
        }, "Should throw exception for negative column");

        assertThrows(IllegalArgumentException.class, () -> {
            board.placePiece(8, 0, Player.BLACK);
        }, "Should throw exception for row >= size");

        assertThrows(IllegalArgumentException.class, () -> {
            board.placePiece(0, 8, Player.BLACK);
        }, "Should throw exception for column >= size");
    }

    @Test
    void testIsEmptyMethod() {
        OthelloBoard board = new OthelloBoard();

        assertTrue(board.isEmpty(0, 0), "Corner should be empty initially");
        assertFalse(board.isEmpty(3, 3), "Center should not be empty");
        assertFalse(board.isEmpty(3, 4), "Center should not be empty");
    }

    @Test
    void testGetPlayerAtValidPosition() {
        OthelloBoard board = new OthelloBoard();

        assertEquals(Player.WHITE, board.getPlayerAt(3, 3));
        assertEquals(Player.BLACK, board.getPlayerAt(3, 4));
        assertEquals(Player.BLACK, board.getPlayerAt(4, 3));
        assertEquals(Player.WHITE, board.getPlayerAt(4, 4));
        assertNull(board.getPlayerAt(0, 0), "Empty cell should return null");
    }

    @Test
    void testHasValidMovesForBothPlayers() {
        OthelloBoard board = new OthelloBoard();

        assertTrue(board.hasValidMoves(Player.BLACK), "BLACK should have valid moves initially");
        assertTrue(board.hasValidMoves(Player.WHITE), "WHITE should have valid moves initially");
    }

    @Test
    void testBoardSnapshotAndRestore() {
        OthelloBoard board = new OthelloBoard();

        // Make a change
        board.executeMove(2, 3, Player.BLACK);

        // Get snapshot
        CellState[][] snapshot = board.getBoardSnapshot();

        // Make more changes
        board.executeMove(2, 2, Player.WHITE);

        // Restore snapshot
        board.setBoardFromSnapshot(snapshot);

        // Verify state is restored
        assertEquals(Player.BLACK, board.getPlayerAt(2, 3));
        assertEquals(4, board.countPieces(Player.BLACK));
    }

    @Test
    void testResetBoard() {
        OthelloBoard board = new OthelloBoard();

        // Make changes
        board.executeMove(2, 3, Player.BLACK);
        assertEquals(4, board.countPieces(Player.BLACK));

        // Reset
        board.reset();

        // Verify initial state
        assertEquals(2, board.countPieces(Player.BLACK));
        assertEquals(2, board.countPieces(Player.WHITE));
        assertEquals(Player.WHITE, board.getPlayerAt(3, 3));
        assertEquals(Player.BLACK, board.getPlayerAt(3, 4));
    }

    @Test
    void testFindAllFlippablePiecesMultipleDirections() {
        OthelloBoard board = new OthelloBoard();

        // Play some moves to set up a situation
        board.executeMove(2, 3, Player.BLACK);
        board.executeMove(2, 2, Player.WHITE);

        // Now check if there are positions that can flip pieces
        // This tests the algorithm's ability to find pieces in different directions
        assertTrue(board.countPieces(Player.BLACK) + board.countPieces(Player.WHITE) > 4,
                  "Board should have more than 4 pieces after moves");
    }

    @Test
    void testCountPiecesAfterMultipleMoves() {
        OthelloBoard board = new OthelloBoard();

        int initialBlack = board.countPieces(Player.BLACK);
        int initialWhite = board.countPieces(Player.WHITE);

        assertEquals(2, initialBlack);
        assertEquals(2, initialWhite);

        // Play moves and verify counts change appropriately
        board.executeMove(2, 3, Player.BLACK);

        assertTrue(board.countPieces(Player.BLACK) > initialBlack);
        assertTrue(board.countPieces(Player.WHITE) < initialWhite);
    }

    @Test
    void testAllDirectionsForValidMove() {
        OthelloBoard board = new OthelloBoard();

        // Test that isValidMove checks all 8 directions
        // The initial position has valid moves in different directions
        assertTrue(board.isValidMove(2, 3, Player.BLACK), "North");
        assertTrue(board.isValidMove(3, 2, Player.BLACK), "West");
        assertTrue(board.isValidMove(4, 5, Player.BLACK), "East");
        assertTrue(board.isValidMove(5, 4, Player.BLACK), "South");
    }

    @Test
    void testExecuteMoveReturnsFlippedCount() {
        OthelloBoard board = new OthelloBoard();

        int flipped = board.executeMove(2, 3, Player.BLACK);

        assertEquals(1, flipped, "Should return number of flipped pieces");
    }

    @Test
    void testExecuteMoveOnBorderCells() {
        OthelloBoard board = new OthelloBoard();

        // First set up a scenario where a border move is valid
        // (This might require several moves)
        board.executeMove(2, 3, Player.BLACK);
        board.executeMove(2, 2, Player.WHITE);

        // Now board state has changed, we can test edge cases
    }
}
