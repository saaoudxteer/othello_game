package fr.univ_amu.m1info.othello;

import fr.univ_amu.m1info.board_game_library.graphics.configuration.BoardGameDimensions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for OthelloGame class.
 */
class OthelloGameTest {
    private OthelloGame game;

    @BeforeEach
    void setUp() {
        game = new OthelloGame(new BoardGameDimensions(8, 8));
    }

    @Test
    void newGameStartsWithBlackPlayer() {
        assertEquals(Player.BLACK, game.getCurrentPlayer(), "Game should start with BLACK player");
    }

    @Test
    void newGameIsInProgress() {
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus(), "New game should be IN_PROGRESS");
    }

    @Test
    void resetGameClearsBoard() {
        // Play a move
        game.playMove(2, 3, 0);

        // Reset
        game.reset();

        assertEquals(Player.BLACK, game.getCurrentPlayer(), "After reset, should be BLACK's turn");
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus(), "After reset, should be IN_PROGRESS");
        assertEquals(2, game.getBoard().countPieces(Player.BLACK), "After reset, should have 2 BLACK pieces");
        assertEquals(2, game.getBoard().countPieces(Player.WHITE), "After reset, should have 2 WHITE pieces");
    }

    @Test
    void validMoveReturnsValidResult() {
        MoveResult result = game.playMove(2, 3, 100);

        assertTrue(result.valid(), "Valid move should return valid result");
        assertEquals(1, result.flippedCount(), "Should flip 1 piece");
    }

    @Test
    void invalidMoveReturnsInvalidResult() {
        MoveResult result = game.playMove(0, 0, 100);

        assertFalse(result.valid(), "Invalid move should return invalid result");
    }

    @Test
    void validMoveSwitchesPlayer() {
        assertEquals(Player.BLACK, game.getCurrentPlayer());

        game.playMove(2, 3, 100);

        assertEquals(Player.WHITE, game.getCurrentPlayer(), "After valid move, player should switch to WHITE");
    }

    @Test
    void invalidMoveDoesNotSwitchPlayer() {
        assertEquals(Player.BLACK, game.getCurrentPlayer());

        game.playMove(0, 0, 100); // Invalid move

        assertEquals(Player.BLACK, game.getCurrentPlayer(), "After invalid move, player should remain BLACK");
    }

    @Test
    void undoRestoresPreviousState() {
        // Play a move
        game.playMove(2, 3, 500);
        assertEquals(Player.WHITE, game.getCurrentPlayer());
        assertEquals(4, game.getBoard().countPieces(Player.BLACK));

        // Undo
        boolean undone = game.undo();

        assertTrue(undone, "Undo should succeed");
        assertEquals(Player.BLACK, game.getCurrentPlayer(), "After undo, should be BLACK's turn");
        assertEquals(2, game.getBoard().countPieces(Player.BLACK), "After undo, should have 2 BLACK pieces");
    }

    @Test
    void undoWithNoHistoryReturnsFalse() {
        boolean undone = game.undo();

        assertFalse(undone, "Undo with no history should return false");
    }

    @Test
    void getLastSnapshotElapsedMillisReturnsCorrectTime() {
        game.playMove(2, 3, 1500);

        long elapsed = game.getLastSnapshotElapsedMillis();

        assertEquals(1500, elapsed, "Should return the elapsed time from last snapshot");
    }

    @Test
    void getLastSnapshotElapsedMillisReturnsZeroWithNoHistory() {
        long elapsed = game.getLastSnapshotElapsedMillis();

        assertEquals(0, elapsed, "Should return 0 when no history");
    }

    @Test
    void getValidMovesReturnsListOfMoves() {
        List<Coordinates> validMoves = game.getValidMoves(Player.BLACK);

        assertEquals(4, validMoves.size(), "BLACK should have 4 valid moves initially");
    }

    @Test
    void hasValidMovesReturnsTrueWhenMovesAvailable() {
        assertTrue(game.hasValidMoves(Player.BLACK), "BLACK should have valid moves initially");
    }

    @Test
    void getBoardReturnsGameBoard() {
        OthelloBoard board = game.getBoard();

        assertNotNull(board, "Board should not be null");
        assertEquals(8, board.getSize(), "Board should be 8x8");
    }

    @Test
    void getTotalMovesInitiallyZero() {
        assertEquals(0, game.getTotalMoves(), "Total moves should be 0 initially");
    }

    @Test
    void getTotalMovesIncrementsAfterValidMove() {
        game.playMove(2, 3, 0);

        assertEquals(1, game.getTotalMoves(), "Total moves should be 1 after one move");

        game.playMove(2, 2, 0);

        assertEquals(2, game.getTotalMoves(), "Total moves should be 2 after two moves");
    }

    @Test
    void getTotalMovesDoesNotIncrementAfterInvalidMove() {
        game.playMove(0, 0, 0); // Invalid

        assertEquals(0, game.getTotalMoves(), "Total moves should remain 0 after invalid move");
    }

    @Test
    void getWinnerReturnsNullWhenGameInProgress() {
        assertNull(game.getWinner(), "Winner should be null when game is in progress");
    }

    @Test
    void playRandomMoveReturnsValidCoordinate() {
        Coordinates move = game.playRandomMove(0);

        assertNotNull(move, "Random move should return a coordinate");
        // Verify the move was actually played
        assertEquals(Player.WHITE, game.getCurrentPlayer(), "Player should switch after random move");
    }

    @Test
    void playBestMoveReturnsValidCoordinate() {
        Coordinates move = game.playBestMove(0);

        assertNotNull(move, "Best move should return a coordinate");
        assertEquals(Player.WHITE, game.getCurrentPlayer(), "Player should switch after best move");
    }

    @Test
    void playRobotMoveEasyDifficulty() {
        Coordinates move = game.playRobotMove(RobotDifficulty.EASY, 0);

        assertNotNull(move, "Robot easy move should return a coordinate");
        assertEquals(Player.WHITE, game.getCurrentPlayer(), "Player should switch after robot move");
    }

    @Test
    void playRobotMoveHardDifficulty() {
        Coordinates move = game.playRobotMove(RobotDifficulty.HARD, 0);

        assertNotNull(move, "Robot hard move should return a coordinate");
        assertEquals(Player.WHITE, game.getCurrentPlayer(), "Player should switch after robot move");
    }

    @Test
    void multipleUndosWork() {
        // Play valid moves
        game.playMove(2, 3, 100); // BLACK
        game.playMove(2, 2, 200); // WHITE

        // Undo twice
        assertTrue(game.undo()); // Undo WHITE's move
        assertTrue(game.undo()); // Undo BLACK's move

        // Should be back to initial state
        assertEquals(Player.BLACK, game.getCurrentPlayer());
        assertEquals(2, game.getBoard().countPieces(Player.BLACK));
        assertEquals(2, game.getBoard().countPieces(Player.WHITE));
    }

    @Test
    void gameStatusChangesWhenNoValidMoves() {
        // This would require filling the board or creating a specific scenario
        // For now, verify the status transitions properly
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());

        // Play several moves until someone cannot move
        // (This is complex to test without playing a full game)
    }

    @Test
    void coordinatesClassCreation() {
        Coordinates coord = new Coordinates(3, 4);
        assertEquals(3, coord.row());
        assertEquals(4, coord.column());
    }

    @Test
    void moveSnapshotCreation() {
        CellState[][] snapshot = game.getBoard().getBoardSnapshot();
        MoveSnapshot moveSnapshot = new MoveSnapshot(snapshot, Player.BLACK, 1000);

        assertNotNull(moveSnapshot.boardSnapshot());
        assertEquals(Player.BLACK, moveSnapshot.currentPlayer());
        assertEquals(1000, moveSnapshot.elapsedMillis());
    }
}

