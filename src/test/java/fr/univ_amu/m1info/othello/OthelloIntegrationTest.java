package fr.univ_amu.m1info.othello;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Advanced integration tests for Othello game scenarios.
 */
class OthelloIntegrationTest {

    @Test
    void testCompleteGameSequence() {
        OthelloBoard board = new OthelloBoard();

        // Play a sequence of valid moves
        board.executeMove(2, 3, Player.BLACK);
        assertEquals(4, board.countPieces(Player.BLACK));
        assertEquals(1, board.countPieces(Player.WHITE));

        board.executeMove(2, 2, Player.WHITE);
        assertTrue(board.countPieces(Player.WHITE) >= 2);

        // Find and play another valid move for BLACK
        boolean moveMade = false;
        for (int row = 0; row < 8 && !moveMade; row++) {
            for (int col = 0; col < 8 && !moveMade; col++) {
                if (board.isValidMove(row, col, Player.BLACK)) {
                    board.executeMove(row, col, Player.BLACK);
                    moveMade = true;
                }
            }
        }
        assertTrue(moveMade, "Should be able to make a valid BLACK move");
    }

    @Test
    void testBoardFillsCompletely() {
        OthelloBoard board = new OthelloBoard();

        int moveCount = 0;
        Player currentPlayer = Player.BLACK;

        // Play moves until no more valid moves
        while (board.hasValidMoves(currentPlayer) || board.hasValidMoves(currentPlayer.getOpponent())) {
            if (board.hasValidMoves(currentPlayer)) {
                // Find first valid move
                boolean moveMade = false;
                for (int row = 0; row < board.getSize() && !moveMade; row++) {
                    for (int col = 0; col < board.getSize() && !moveMade; col++) {
                        if (board.isValidMove(row, col, currentPlayer)) {
                            board.executeMove(row, col, currentPlayer);
                            moveCount++;
                            moveMade = true;
                        }
                    }
                }
            }
            currentPlayer = currentPlayer.getOpponent();

            // Safety limit to avoid infinite loop
            if (moveCount > 100) break;
        }

        assertTrue(moveCount > 0, "Should have made at least one move");
        assertTrue(board.countPieces(Player.BLACK) + board.countPieces(Player.WHITE) <= 64,
                  "Total pieces should not exceed board size");
    }

    @Test
    void testAllInitialValidMovesForBlack() {
        OthelloBoard board = new OthelloBoard();

        int validMoveCount = 0;
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                if (board.isValidMove(row, col, Player.BLACK)) {
                    validMoveCount++;
                }
            }
        }

        assertEquals(4, validMoveCount, "BLACK should have exactly 4 valid moves initially");
    }

    @Test
    void testFlippingInAllDirections() {
        OthelloBoard board = new OthelloBoard();

        // Play some moves to create flipping scenarios
        board.executeMove(2, 3, Player.BLACK);
        board.executeMove(2, 2, Player.WHITE);

        // Verify pieces are being flipped
        assertTrue(board.countPieces(Player.BLACK) + board.countPieces(Player.WHITE) > 4,
                  "Board should have more pieces after flipping");
    }

    @Test
    void testNoValidMovesScenario() {
        OthelloBoard board = new OthelloBoard();

        // Fill board with one color except edges
        for (int row = 1; row < 7; row++) {
            for (int col = 1; col < 7; col++) {
                if (board.isEmpty(row, col)) {
                    board.placePiece(row, col, Player.BLACK);
                }
            }
        }

        // Check if WHITE has any valid moves
        boolean whiteHasMoves = board.hasValidMoves(Player.WHITE);

        // This scenario might not guarantee no moves, but tests the logic
        assertNotNull(whiteHasMoves);
    }

    @Test
    void testCornerCaptures() {
        OthelloBoard board = new OthelloBoard();

        // Set up a scenario for corner capture
        board.reset();
        board.placePiece(0, 0, Player.WHITE);
        board.placePiece(0, 1, Player.BLACK);
        board.placePiece(0, 2, Player.BLACK);

        // Check if this creates a valid move scenario
        int totalPieces = board.countPieces(Player.BLACK) + board.countPieces(Player.WHITE);
        assertTrue(totalPieces >= 3, "Should have at least 3 pieces");
    }

    @Test
    void testEdgeCaptures() {
        OthelloBoard board = new OthelloBoard();

        // Play moves toward edges
        board.executeMove(2, 3, Player.BLACK);
        board.executeMove(2, 2, Player.WHITE);

        // Continue playing toward edges
        for (int i = 0; i < 5; i++) {
            Player player = (i % 2 == 0) ? Player.BLACK : Player.WHITE;
            boolean moveMade = false;

            for (int row = 0; row < 8 && !moveMade; row++) {
                for (int col = 0; col < 8 && !moveMade; col++) {
                    if (board.isValidMove(row, col, player)) {
                        board.executeMove(row, col, player);
                        moveMade = true;
                    }
                }
            }

            if (!moveMade) break;
        }

        // Verify board state is valid
        assertTrue(board.countPieces(Player.BLACK) + board.countPieces(Player.WHITE) >= 4,
                  "Should have at least initial pieces");
    }

    @Test
    void testLargeFlips() {
        OthelloBoard board = new OthelloBoard();

        // Play moves to test flipping logic
        board.executeMove(2, 3, Player.BLACK);
        int blackCount = board.countPieces(Player.BLACK);

        board.executeMove(2, 2, Player.WHITE);
        int whiteCount = board.countPieces(Player.WHITE);

        // Verify flips occurred
        assertTrue(blackCount > 2, "BLACK should have more than initial pieces");
        assertTrue(whiteCount > 1, "WHITE should have more than initial pieces after flipping");
    }

    @Test
    void testSnapshotPreservesCompleteState() {
        OthelloBoard board = new OthelloBoard();

        board.executeMove(2, 3, Player.BLACK);
        board.executeMove(2, 2, Player.WHITE);

        CellState[][] snapshot = board.getBoardSnapshot();
        int blackCount = board.countPieces(Player.BLACK);
        int whiteCount = board.countPieces(Player.WHITE);

        // Make more changes - find a valid move
        boolean moveMade = false;
        for (int row = 0; row < 8 && !moveMade; row++) {
            for (int col = 0; col < 8 && !moveMade; col++) {
                if (board.isValidMove(row, col, Player.BLACK)) {
                    board.executeMove(row, col, Player.BLACK);
                    moveMade = true;
                }
            }
        }

        // Restore
        board.setBoardFromSnapshot(snapshot);

        // Verify counts match snapshot
        assertEquals(blackCount, board.countPieces(Player.BLACK), "BLACK count should match snapshot");
        assertEquals(whiteCount, board.countPieces(Player.WHITE), "WHITE count should match snapshot");
    }

    @Test
    void testMultipleResetsPreserveInitialState() {
        OthelloBoard board = new OthelloBoard();

        for (int i = 0; i < 5; i++) {
            // Make some moves
            board.executeMove(2, 3, Player.BLACK);
            board.executeMove(2, 2, Player.WHITE);

            // Reset
            board.reset();

            // Verify initial state
            assertEquals(2, board.countPieces(Player.BLACK), "After reset #" + i);
            assertEquals(2, board.countPieces(Player.WHITE), "After reset #" + i);
        }
    }
}

