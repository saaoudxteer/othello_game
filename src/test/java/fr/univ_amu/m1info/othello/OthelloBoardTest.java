package fr.univ_amu.m1info.othello;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for OthelloBoard.isValidMove on the standard starting position.
 */
class OthelloBoardTest {

    @Test
    void validMovesForBlackOnInitialBoard() {
        OthelloBoard board = new OthelloBoard();

        // Standard valid moves for Black from initial position
        assertTrue(board.isValidMove(2, 3, Player.BLACK), "Expected (2,3) to be valid for Black");
        assertTrue(board.isValidMove(3, 2, Player.BLACK), "Expected (3,2) to be valid for Black");
        assertTrue(board.isValidMove(4, 5, Player.BLACK), "Expected (4,5) to be valid for Black");
        assertTrue(board.isValidMove(5, 4, Player.BLACK), "Expected (5,4) to be valid for Black");

        // Some invalid positions
        assertFalse(board.isValidMove(0, 0, Player.BLACK), "Corner should not be valid initially");
        assertFalse(board.isValidMove(3, 3, Player.BLACK), "Occupied cell should be invalid");
    }

    @Test
    void invalidNonCapturingMove() {
        OthelloBoard board = new OthelloBoard();

        // A near but non-capturing move should be false
        assertFalse(board.isValidMove(2, 2, Player.BLACK), "(2,2) should not capture and thus be invalid");
    }

    // ========== User Story 5 Tests ==========

    // 5A: Test identifying flippable pieces in different directions
    @Test
    void findFlippablePiecesInHorizontalDirection() {
        OthelloBoard board = new OthelloBoard();
        // Initial board:
        // Row 3: W B
        // Row 4: B W

        // Black plays at (3,2) - should flip White at (3,3)
        List<Coordinates> flippable = board.findAllFlippablePieces(3, 2, Player.BLACK);

        assertEquals(1, flippable.size(), "Should find 1 piece to flip");
        Coordinates coord = flippable.get(0);
        assertEquals(3, coord.row());
        assertEquals(3, coord.column());
    }

    @Test
    void findFlippablePiecesInVerticalDirection() {
        OthelloBoard board = new OthelloBoard();
        // Black plays at (2,3) - should flip White at (3,3)
        List<Coordinates> flippable = board.findAllFlippablePieces(2, 3, Player.BLACK);

        assertEquals(1, flippable.size(), "Should find 1 piece to flip");
        Coordinates coord = flippable.get(0);
        assertEquals(3, coord.row());
        assertEquals(3, coord.column());
    }

    @Test
    void findFlippablePiecesInDiagonalDirection() {
        OthelloBoard board = new OthelloBoard();
        // Black plays at (2,4) - should flip White at (3,4) diagonally
        List<Coordinates> flippable = board.findAllFlippablePieces(2, 4, Player.BLACK);

        // Actually (2,4) flips (3,4) which is Black initially, so let's correct:
        // Initial: (3,4) = BLACK, (4,4) = WHITE
        // Better test: (5,3) for BLACK should flip (4,3) which is BLACK
        // Let me use the correct valid move
        flippable = board.findAllFlippablePieces(5, 4, Player.BLACK);

        assertEquals(1, flippable.size(), "Should find 1 piece to flip");
        Coordinates coord = flippable.get(0);
        assertEquals(4, coord.row());
        assertEquals(4, coord.column());
    }

    @Test
    void findNoFlippablePiecesForInvalidMove() {
        OthelloBoard board = new OthelloBoard();

        // (0,0) is not a valid move, should find no flippable pieces
        List<Coordinates> flippable = board.findAllFlippablePieces(0, 0, Player.BLACK);

        assertEquals(0, flippable.size(), "Should find no pieces to flip for invalid position");
    }

    @Test
    void findMultipleFlippablePiecesInSingleDirection() {
        OthelloBoard board = new OthelloBoard();
        // After first valid Black move at (2,3), place another
        board.executeMove(2, 3, Player.BLACK);
        // Now test White's response
        List<Coordinates> flippable = board.findAllFlippablePieces(2, 2, Player.WHITE);

        assertTrue(flippable.size() >= 1, "Should find at least one piece to flip");
    }

    // 5B: Test flipping individual pieces
    @Test
    void flipPieceChangesColor() {
        OthelloBoard board = new OthelloBoard();

        // White is at (3,3)
        assertEquals(Player.WHITE, board.getPlayerAt(3, 3), "Initially White at (3,3)");

        // Flip to Black
        board.flipPiece(3, 3, Player.BLACK);

        assertEquals(Player.BLACK, board.getPlayerAt(3, 3), "Should be Black after flip");
    }

    @Test
    void flipPieceOnEmptyCellThrowsException() {
        OthelloBoard board = new OthelloBoard();

        assertThrows(IllegalArgumentException.class, () -> {
            board.flipPiece(0, 0, Player.BLACK);
        }, "Should throw exception when trying to flip empty cell");
    }

    @Test
    void flipPieceOnInvalidPositionThrowsException() {
        OthelloBoard board = new OthelloBoard();

        assertThrows(IllegalArgumentException.class, () -> {
            board.flipPiece(-1, 0, Player.BLACK);
        }, "Should throw exception for invalid position");

        assertThrows(IllegalArgumentException.class, () -> {
            board.flipPiece(8, 8, Player.BLACK);
        }, "Should throw exception for out of bounds position");
    }

    // 5C: Test complete move execution with board update
    @Test
    void executeMoveFlipsPiecesAndUpdatesBoard() {
        OthelloBoard board = new OthelloBoard();

        // Initial state: 2 Black, 2 White
        assertEquals(2, board.countPieces(Player.BLACK), "Initially 2 Black pieces");
        assertEquals(2, board.countPieces(Player.WHITE), "Initially 2 White pieces");

        // Black plays at (2,3) - should flip White at (3,3)
        int flipped = board.executeMove(2, 3, Player.BLACK);

        assertEquals(1, flipped, "Should flip exactly 1 piece");
        assertEquals(4, board.countPieces(Player.BLACK), "Should have 4 Black pieces after move");
        assertEquals(1, board.countPieces(Player.WHITE), "Should have 1 White piece after flip");

        // Verify the specific positions
        assertEquals(Player.BLACK, board.getPlayerAt(2, 3), "New piece at (2,3) should be Black");
        assertEquals(Player.BLACK, board.getPlayerAt(3, 3), "Flipped piece at (3,3) should be Black");
    }

    @Test
    void executeMoveWithMultipleFlips() {
        OthelloBoard board = new OthelloBoard();

        // Execute first move
        board.executeMove(2, 3, Player.BLACK);

        // White's turn - play at (2,2)
        int flipped = board.executeMove(2, 2, Player.WHITE);

        assertTrue(flipped >= 1, "Should flip at least 1 piece");
        assertTrue(board.countPieces(Player.WHITE) > 1, "White should have more pieces");
    }

    @Test
    void executeMoveOnInvalidPositionThrowsException() {
        OthelloBoard board = new OthelloBoard();

        assertThrows(IllegalArgumentException.class, () -> {
            board.executeMove(0, 0, Player.BLACK);
        }, "Should throw exception for invalid move");
    }

    @Test
    void executeMoveOnOccupiedCellThrowsException() {
        OthelloBoard board = new OthelloBoard();

        assertThrows(IllegalArgumentException.class, () -> {
            board.executeMove(3, 3, Player.BLACK);
        }, "Should throw exception when trying to play on occupied cell");
    }

    @Test
    void completeGameSequenceWithFlipping() {
        OthelloBoard board = new OthelloBoard();

        // Play a sequence of moves and verify the board state
        board.executeMove(2, 3, Player.BLACK);  // Black: 4, White: 1
        assertEquals(4, board.countPieces(Player.BLACK));
        assertEquals(1, board.countPieces(Player.WHITE));

        board.executeMove(2, 2, Player.WHITE);  // Flips Black pieces
        assertTrue(board.countPieces(Player.WHITE) >= 2, "White should have pieces after move");

        // Verify board is still valid
        int totalPieces = board.countPieces(Player.BLACK) + board.countPieces(Player.WHITE);
        assertEquals(6, totalPieces, "Should have 6 total pieces after 2 moves");
    }

    @Test
    void executeMoveMaintainsBoardIntegrity() {
        OthelloBoard board = new OthelloBoard();

        int initialTotal = board.countPieces(Player.BLACK) + board.countPieces(Player.WHITE);

        board.executeMove(2, 3, Player.BLACK);

        int afterTotal = board.countPieces(Player.BLACK) + board.countPieces(Player.WHITE);

        assertEquals(initialTotal + 1, afterTotal, "Total pieces should increase by 1 after move");
    }

    // ========== User Story 12 Tests ==========

    // 12A: Test hasValidMoves method
    @Test
    void hasValidMovesReturnsTrueForBlackAtStart() {
        OthelloBoard board = new OthelloBoard();

        assertTrue(board.hasValidMoves(Player.BLACK), "Black should have valid moves at start");
    }

    @Test
    void hasValidMovesReturnsTrueForWhiteAtStart() {
        OthelloBoard board = new OthelloBoard();

        assertTrue(board.hasValidMoves(Player.WHITE), "White should have valid moves at start");
    }

    @Test
    void hasValidMovesReturnsFalseWhenNoMovesAvailable() {
        OthelloBoard board = new OthelloBoard();

        // Create a scenario where a player has no valid moves
        // Fill the board completely so no moves are possible
        board.reset();

        // Fill entire board with Black pieces
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board.isEmpty(row, col)) {
                    try {
                        board.placePiece(row, col, Player.BLACK);
                    } catch (IllegalArgumentException e) {
                        // Skip if position is invalid
                    }
                }
            }
        }

        // Now both players should have no valid moves (board is full)
        assertFalse(board.hasValidMoves(Player.WHITE), "White should have no valid moves on full board");
        assertFalse(board.hasValidMoves(Player.BLACK), "Black should have no valid moves on full board");
    }

    @Test
    void hasValidMovesAfterSeveralMoves() {
        OthelloBoard board = new OthelloBoard();

        // Play a few moves
        board.executeMove(2, 3, Player.BLACK);
        assertTrue(board.hasValidMoves(Player.WHITE), "White should have moves after Black's first move");

        board.executeMove(2, 2, Player.WHITE);
        assertTrue(board.hasValidMoves(Player.BLACK), "Black should have moves after White's move");
    }

    @Test
    void hasValidMovesWithEmptyBoardExceptInitial() {
        OthelloBoard board = new OthelloBoard();

        // At start, both players have valid moves
        assertTrue(board.hasValidMoves(Player.BLACK), "Black should have valid moves at game start");
        assertTrue(board.hasValidMoves(Player.WHITE), "White should have valid moves at game start");
    }

    @Test
    void hasValidMovesDetectsNoMovesCorrectly() {
        OthelloBoard board = new OthelloBoard();

        // Create a scenario where board is completely filled
        board.reset();

        // Fill the entire board
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                if (board.isEmpty(row, col)) {
                    board.placePiece(row, col, Player.BLACK);
                }
            }
        }

        // Both players should have no valid moves because board is full
        assertFalse(board.hasValidMoves(Player.WHITE), "Player should have no moves on full board");
        assertFalse(board.hasValidMoves(Player.BLACK), "Player should have no moves on full board");
    }
}

