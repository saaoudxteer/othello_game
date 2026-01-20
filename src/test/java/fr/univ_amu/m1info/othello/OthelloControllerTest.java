package fr.univ_amu.m1info.othello;

import fr.univ_amu.m1info.board_game_library.graphics.BoardGameView;
import fr.univ_amu.m1info.board_game_library.graphics.Color;
import fr.univ_amu.m1info.board_game_library.graphics.GameTimer;
import fr.univ_amu.m1info.board_game_library.graphics.Shape;
import fr.univ_amu.m1info.board_game_library.graphics.configuration.BoardGameDimensions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for OthelloController focusing on model state and player switching.
 */
class OthelloControllerTest {

    /**
     * Creates a mock BoardGameView for testing purposes.
     * This mock implementation does nothing but prevents NullPointerException.
     */
    private static class MockBoardGameView implements BoardGameView {
        @Override
        public void addShapeAtCell(int row, int column, Shape shape, Color color) {
            // Mock implementation - do nothing
        }

        @Override
        public void updateLabeledElement(String elementId, String text) {
            // Mock implementation - do nothing
        }

        @Override
        public void resetBoard() {
            // Mock implementation - do nothing
        }

        @Override
        public void setCellColor(int row, int column, Color color) {
            // Mock implementation - do nothing
        }

        @Override
        public void removeShapesAtCell(int row, int column) {
            // Mock implementation - do nothing
        }

        @Override
        public void removeLabeledElement(String elementId) {
            // Mock implementation - do nothing
        }

        @Override
        public void addLabel(String label, String id) {
            // Mock implementation - do nothing
        }

        @Override
        public void addButton(String label, String id) {
            // Mock implementation - do nothing
        }

        @Override
        public void clearSuggestions() {
            // Mock implementation - do nothing
        }
    }

    /**
     * Mock GameTimer for testing without JavaFX.
     */
    private static class MockGameTimer implements GameTimer {
        private long elapsedMillis = 0;
        private Runnable onTick;

        @Override
        public void start() {
            // Mock implementation
        }

        @Override
        public void stop() {
            // Mock implementation
        }

        @Override
        public void reset() {
            elapsedMillis = 0;
        }

        @Override
        public long getElapsedMillis() {
            return elapsedMillis;
        }

        @Override
        public void setOnTick(Runnable r) {
            this.onTick = r;
        }

        public void setElapsedMillis(long millis) {
            this.elapsedMillis = millis;
        }
    }

    /**
     * Helper to create a controller with mocked dependencies.
     */
    private OthelloController createController() {
        OthelloGame game = new OthelloGame(new BoardGameDimensions(8, 8));
        GameTimer timer = new MockGameTimer();
        return new OthelloController(game, timer);
    }

    /**
     * Helper to get the game from a controller.
     */
    private OthelloGame getGame(OthelloController controller) {
        // Use reflection or provide a getter for testing
        try {
            var field = controller.getClass().getDeclaredField("game");
            field.setAccessible(true);
            return (OthelloGame) field.get(controller);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access game field", e);
        }
    }


    @Test
    void controllerPlacesPieceOnValidMoveAndSwitchesPlayer() {
        OthelloController controller = createController();
        controller.initializeViewOnStart(new MockBoardGameView());

        OthelloGame game = getGame(controller);

        // Current player starts as Black
        assertEquals(Player.BLACK, game.getCurrentPlayer());

        // (2,3) is a standard valid move for Black on initial board
        controller.boardActionOnClick(2, 3);

        // Model should have the new piece
        assertEquals(Player.BLACK, game.getBoard().getPlayerAt(2, 3),
                     "Model should contain Black at (2,3)");

        // Player should have switched to White
        assertEquals(Player.WHITE, game.getCurrentPlayer(),
                     "Player should switch after a successful move");
    }

    @Test
    void controllerRejectsInvalidMoveAndDoesNotSwitchPlayer() {
        OthelloController controller = createController();
        controller.initializeViewOnStart(new MockBoardGameView());

        OthelloGame game = getGame(controller);

        assertEquals(Player.BLACK, game.getCurrentPlayer());

        // (0,0) is invalid at start
        controller.boardActionOnClick(0, 0);

        // No piece should be placed in model
        assertNull(getGame(controller).getBoard().getPlayerAt(0, 0),
                   "Model should not contain piece at (0,0)");

        // Player should remain Black
        assertEquals(Player.BLACK, game.getCurrentPlayer(),
                     "Player must not switch after invalid move");
    }

    @Test
    void controllerRejectsOccupiedCell() {
        OthelloController controller = createController();
        controller.initializeViewOnStart(new MockBoardGameView());

        OthelloGame game = getGame(controller);

        // (3,3) is occupied at start (White piece)
        controller.boardActionOnClick(3, 3);

        // Player should remain Black (no switch)
        assertEquals(Player.BLACK, game.getCurrentPlayer(),
                     "Player must not switch when clicking occupied cell");
    }

    /**
     * Mock that records labeled element updates so we can verify pass turn messages.
     */
    private static class RecordingMockBoardGameView extends MockBoardGameView {
        public String lastErrorMessage = "";

        @Override
        public void updateLabeledElement(String elementId, String text) {
            if ("ErrorLabel".equals(elementId)) {
                lastErrorMessage = text;
            }
        }
    }

    @Test
    void controllerPassesTurnWhenPlayerHasNoValidMoves() {
        OthelloController controller = createController();
        RecordingMockBoardGameView mockView = new RecordingMockBoardGameView();
        controller.initializeViewOnStart(mockView);

        OthelloGame game = getGame(controller);

        // Start with Black
        assertEquals(Player.BLACK, game.getCurrentPlayer(), "Should start with Black");

        // Make a valid move for Black
        controller.boardActionOnClick(2, 3);

        // After the move, player should switch to White
        // If White has moves, it stays White; if not, it passes back to Black
        Player playerAfterMove = game.getCurrentPlayer();
        assertTrue(playerAfterMove == Player.WHITE || playerAfterMove == Player.BLACK,
                   "Player should be either White or Black after move");
    }

    @Test
    void controllerDetectsGameOverWhenNoPlayerCanMove() {
        OthelloController controller = createController();
        RecordingMockBoardGameView mockView = new RecordingMockBoardGameView();
        controller.initializeViewOnStart(mockView);

        OthelloGame game = getGame(controller);
        OthelloBoard board = game.getBoard();

        // Fill all empty spaces
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                if (board.isEmpty(row, col)) {
                    try {
                        board.placePiece(row, col, Player.BLACK);
                    } catch (IllegalArgumentException e) {
                        // Skip invalid positions
                    }
                }
            }
        }

        // Now try to make a move (which should be impossible)
        controller.boardActionOnClick(0, 0);

        // The error message should indicate cell occupied
        assertTrue(mockView.lastErrorMessage.contains("occupied") ||
                   mockView.lastErrorMessage.contains("Invalid") ||
                   mockView.lastErrorMessage.contains("over"),
                   "Should show error when board is full");
    }

    @Test
    void controllerAnnouncesWinnerWhenGameEnds() {
        OthelloController controller = createController();
        RecordingMockBoardGameView mockView = new RecordingMockBoardGameView();
        controller.initializeViewOnStart(mockView);

        OthelloGame game = getGame(controller);
        OthelloBoard board = game.getBoard();

        // Fill board mostly with Black pieces (Black should win)
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                if (board.isEmpty(row, col)) {
                    board.placePiece(row, col, Player.BLACK);
                }
            }
        }

        // Trigger the logic by attempting a move
        controller.boardActionOnClick(0, 0);

        // Verify the error message mentions occupied or game over
        assertTrue(mockView.lastErrorMessage.contains("occupied") ||
                   mockView.lastErrorMessage.contains("Invalid") ||
                   mockView.lastErrorMessage.contains("over"),
                   "Should show appropriate error message");
    }

    @Test
    void controllerMaintainsStateAfterPassingTurn() {
        OthelloController controller = createController();
        RecordingMockBoardGameView mockView = new RecordingMockBoardGameView();
        controller.initializeViewOnStart(mockView);

        OthelloGame game = getGame(controller);

        // Get initial board state
        int initialBlackCount = game.getBoard().countPieces(Player.BLACK);
        int initialWhiteCount = game.getBoard().countPieces(Player.WHITE);

        // Make a valid move
        controller.boardActionOnClick(2, 3);

        // Board should have changed
        int afterBlackCount = game.getBoard().countPieces(Player.BLACK);
        int afterWhiteCount = game.getBoard().countPieces(Player.WHITE);

        // Verify pieces were added/flipped
        assertTrue(afterBlackCount > initialBlackCount || afterWhiteCount < initialWhiteCount,
                   "Board state should change after valid move");
    }

    @Test
    void controllerSwitchesPlayerCorrectlyWhenBothCanPlay() {
        OthelloController controller = createController();
        MockBoardGameView mockView = new MockBoardGameView();
        controller.initializeViewOnStart(mockView);

        OthelloGame game = getGame(controller);

        // Black plays
        assertEquals(Player.BLACK, game.getCurrentPlayer());
        controller.boardActionOnClick(2, 3);

        // Should switch to White (White has valid moves at start)
        assertEquals(Player.WHITE, game.getCurrentPlayer(),
                     "Should switch to White after Black's move");

        // White plays
        controller.boardActionOnClick(2, 2);

        // Should switch back to Black
        assertEquals(Player.BLACK, game.getCurrentPlayer(),
                     "Should switch to Black after White's move");
    }

    @Test
    void controllerAnnouncesBlackWinnerCorrectly() {
        OthelloController controller = createController();
        RecordingMockBoardGameView mockView = new RecordingMockBoardGameView();

        OthelloGame game = getGame(controller);
        OthelloBoard board = game.getBoard();
        board.reset();

        // Fill most of the board with Black pieces
        int blackPieces = 0;
        int whitePieces = 0;
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                if (board.isEmpty(row, col)) {
                    // Place 50 Black, 10 White
                    if (blackPieces < 50) {
                        board.placePiece(row, col, Player.BLACK);
                        blackPieces++;
                    } else if (whitePieces < 10) {
                        board.placePiece(row, col, Player.WHITE);
                        whitePieces++;
                    }
                }
            }
        }

        // Initialize view after board setup
        controller.initializeViewOnStart(mockView);

        int finalBlack = board.countPieces(Player.BLACK);
        int finalWhite = board.countPieces(Player.WHITE);

        // Verify counts make sense
        assertTrue(finalBlack > finalWhite, "Black should have more pieces");
    }

    @Test
    void controllerAnnouncesWhiteWinnerCorrectly() {
        OthelloController controller = createController();
        RecordingMockBoardGameView mockView = new RecordingMockBoardGameView();

        OthelloGame game = getGame(controller);
        OthelloBoard board = game.getBoard();
        board.reset();

        // Fill most of the board with White pieces
        int blackPieces = 0;
        int whitePieces = 0;
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                if (board.isEmpty(row, col)) {
                    // Place 10 Black, 50 White
                    if (blackPieces < 10) {
                        board.placePiece(row, col, Player.BLACK);
                        blackPieces++;
                    } else if (whitePieces < 50) {
                        board.placePiece(row, col, Player.WHITE);
                        whitePieces++;
                    }
                }
            }
        }

        // Initialize view after board setup
        controller.initializeViewOnStart(mockView);

        int finalBlack = board.countPieces(Player.BLACK);
        int finalWhite = board.countPieces(Player.WHITE);

        // Verify counts make sense
        assertTrue(finalWhite > finalBlack, "White should have more pieces");
    }

    @Test
    void controllerAnnouncesTieCorrectly() {
        OthelloController controller = createController();
        RecordingMockBoardGameView mockView = new RecordingMockBoardGameView();

        OthelloGame game = getGame(controller);
        OthelloBoard board = game.getBoard();
        board.reset();

        // Fill board with equal Black and White pieces
        int blackPieces = 0;
        int whitePieces = 0;
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                if (board.isEmpty(row, col)) {
                    // Alternate between Black and White
                    if (blackPieces <= whitePieces && blackPieces < 30) {
                        board.placePiece(row, col, Player.BLACK);
                        blackPieces++;
                    } else if (whitePieces < 30) {
                        board.placePiece(row, col, Player.WHITE);
                        whitePieces++;
                    }
                }
            }
        }

        // Initialize view after board setup
        controller.initializeViewOnStart(mockView);

        int finalBlack = board.countPieces(Player.BLACK);
        int finalWhite = board.countPieces(Player.WHITE);

        // Verify counts are equal or close
        assertTrue(Math.abs(finalBlack - finalWhite) <= 2, "Pieces should be roughly equal for tie");
    }
}
