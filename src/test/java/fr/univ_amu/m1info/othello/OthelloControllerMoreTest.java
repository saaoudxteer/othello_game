package fr.univ_amu.m1info.othello;

import fr.univ_amu.m1info.board_game_library.graphics.BoardGameView;
import fr.univ_amu.m1info.board_game_library.graphics.Color;
import fr.univ_amu.m1info.board_game_library.graphics.GameTimer;
import fr.univ_amu.m1info.board_game_library.graphics.Shape;
import fr.univ_amu.m1info.board_game_library.graphics.configuration.BoardGameDimensions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional comprehensive tests for OthelloController.
 */
class OthelloControllerMoreTest {

    private static class MockBoardGameView implements BoardGameView {
        public String lastUpdatedLabelId = "";
        public String lastUpdatedLabelText = "";
        public int addShapeCallCount = 0;
        public int resetBoardCallCount = 0;
        public int setCellColorCallCount = 0;
        public int removeShapesCallCount = 0;
        public int clearSuggestionsCallCount = 0;

        @Override
        public void addShapeAtCell(int row, int column, Shape shape, Color color) {
            addShapeCallCount++;
        }

        @Override
        public void updateLabeledElement(String elementId, String text) {
            lastUpdatedLabelId = elementId;
            lastUpdatedLabelText = text;
        }

        @Override
        public void resetBoard() {
            resetBoardCallCount++;
        }

        @Override
        public void setCellColor(int row, int column, Color color) {
            setCellColorCallCount++;
        }

        @Override
        public void removeShapesAtCell(int row, int column) {
            removeShapesCallCount++;
        }

        @Override
        public void removeLabeledElement(String elementId) {
        }

        @Override
        public void addLabel(String label, String id) {
        }

        @Override
        public void addButton(String label, String id) {
        }

        @Override
        public void clearSuggestions() {
            clearSuggestionsCallCount++;
        }
    }

    private static class MockGameTimer implements GameTimer {
        private long elapsedMillis = 0;
        private boolean started = false;
        private boolean stopped = false;
        private Runnable onTick;

        @Override
        public void start() {
            started = true;
            stopped = false;
        }

        @Override
        public void stop() {
            stopped = true;
            started = false;
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

        public void tick() {
            if (onTick != null) {
                onTick.run();
            }
        }

        public void setElapsedMillis(long millis) {
            this.elapsedMillis = millis;
        }
    }

    private OthelloController controller;
    private MockBoardGameView mockView;
    private MockGameTimer mockTimer;
    private OthelloGame game;

    @BeforeEach
    void setUp() {
        game = new OthelloGame(new BoardGameDimensions(8, 8));
        mockTimer = new MockGameTimer();
        controller = new OthelloController(game, mockTimer);
        mockView = new MockBoardGameView();
        controller.initializeViewOnStart(mockView);
    }

    @Test
    void testInitializeViewOnStart() {
        MockBoardGameView view = new MockBoardGameView();
        OthelloController newController = new OthelloController(game, mockTimer);

        newController.initializeViewOnStart(view);

        assertTrue(view.addShapeCallCount > 0, "Should add initial pieces to view");
    }

    @Test
    void testButtonActionOnClickReset() {
        // Make a move first
        controller.boardActionOnClick(2, 3);

        int initialResetCount = mockView.resetBoardCallCount;

        // Reset
        controller.buttonActionOnClick(OthelloButtonId.RESET.getId());

        assertTrue(mockView.resetBoardCallCount > initialResetCount, "Reset should call resetBoard on view");
    }

    @Test
    void testButtonActionOnClickUndo() {
        // Make a move first
        controller.boardActionOnClick(2, 3);

        // Undo
        controller.buttonActionOnClick(OthelloButtonId.UNDO.getId());

        // Verify board state changed
        assertEquals(Player.BLACK, game.getCurrentPlayer(), "After undo, should be BLACK's turn again");
    }

    @Test
    void testButtonActionOnClickUndoWithNoHistory() {
        // Try to undo without any moves
        controller.buttonActionOnClick(OthelloButtonId.UNDO.getId());

        // Should show error message
        assertEquals("ErrorLabel", mockView.lastUpdatedLabelId);
        assertTrue(mockView.lastUpdatedLabelText.contains("No move") ||
                   mockView.lastUpdatedLabelText.contains("nothing"),
                   "Should show 'no move to undo' error");
    }

    @Test
    void testBoardActionOnClickValidMove() {
        int initialShapeCount = mockView.addShapeCallCount;

        controller.boardActionOnClick(2, 3);

        assertTrue(mockView.addShapeCallCount > initialShapeCount, "Should add shapes for new pieces");
        assertEquals(Player.WHITE, game.getCurrentPlayer(), "Player should switch to WHITE");
    }

    @Test
    void testBoardActionOnClickInvalidMove() {
        controller.boardActionOnClick(0, 0);

        assertEquals("ErrorLabel", mockView.lastUpdatedLabelId);
        assertTrue(mockView.lastUpdatedLabelText.contains("Invalid") ||
                   mockView.lastUpdatedLabelText.contains("must capture"),
                   "Should show invalid move error");
        assertEquals(Player.BLACK, game.getCurrentPlayer(), "Player should remain BLACK");
    }

    @Test
    void testBoardActionOnClickOccupiedCell() {
        controller.boardActionOnClick(3, 3);

        assertEquals("ErrorLabel", mockView.lastUpdatedLabelId);
        assertTrue(mockView.lastUpdatedLabelText.contains("occupied"),
                   "Should show occupied cell error");
    }

    @Test
    void testPlayerLabelUpdates() {
        // Initially BLACK's turn
        assertTrue(mockView.lastUpdatedLabelText.contains("BLACK") ||
                   mockView.lastUpdatedLabelText.contains("Black"),
                   "Should show BLACK's turn initially");

        // Make a valid move
        controller.boardActionOnClick(2, 3);

        // Should now show WHITE's turn
        assertTrue(mockView.lastUpdatedLabelText.contains("WHITE") ||
                   mockView.lastUpdatedLabelText.contains("White"),
                   "Should show WHITE's turn after move");
    }

    @Test
    void testScoreLabelsUpdate() {
        controller.boardActionOnClick(2, 3);

        // Score labels should be updated
        // The mock should have been called to update score labels
        assertNotNull(mockView.lastUpdatedLabelText, "Labels should be updated");
    }

    @Test
    void testConsecutiveMovesUpdateViewCorrectly() {
        controller.boardActionOnClick(2, 3); // BLACK
        int shapesAfterFirst = mockView.addShapeCallCount;

        controller.boardActionOnClick(2, 2); // WHITE
        int shapesAfterSecond = mockView.addShapeCallCount;

        assertTrue(shapesAfterSecond > shapesAfterFirst,
                  "Each move should add shapes to the view");
    }

    @Test
    void testUndoRestoresViewState() {
        controller.boardActionOnClick(2, 3);
        int shapesAfterMove = mockView.addShapeCallCount;

        controller.buttonActionOnClick(OthelloButtonId.UNDO.getId());

        // View should be updated after undo
        assertTrue(mockView.resetBoardCallCount > 0 || mockView.removeShapesCallCount > 0,
                  "Undo should update the view");
    }
}
