package fr.univ_amu.m1info.othello;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for MoveResult record.
 */
class MoveResultTest {

    @Test
    void testValidMoveResult() {
        MoveResult result = MoveResult.valid(3, GameStatus.IN_PROGRESS, Player.WHITE);

        assertTrue(result.valid(), "Should be valid");
        assertEquals(3, result.flippedCount(), "Should have 3 flipped pieces");
        assertEquals(GameStatus.IN_PROGRESS, result.status(), "Should be in progress");
        assertEquals(Player.WHITE, result.nextPlayer(), "Next player should be WHITE");
    }

    @Test
    void testInvalidMoveResult() {
        MoveResult result = MoveResult.invalid();

        assertFalse(result.valid(), "Should be invalid");
        assertEquals(0, result.flippedCount(), "Should have 0 flipped pieces");
        assertEquals(GameStatus.IN_PROGRESS, result.status(), "Should be in progress");
        assertNull(result.nextPlayer(), "Next player should be null for invalid move");
    }

    @Test
    void testMoveResultWithGameOver() {
        MoveResult result = MoveResult.valid(2, GameStatus.FINISHED, Player.BLACK);

        assertTrue(result.valid());
        assertEquals(GameStatus.FINISHED, result.status(), "Game should be finished");
    }

    @Test
    void testMoveResultWithDraw() {
        MoveResult result = MoveResult.valid(1, GameStatus.DRAW, Player.WHITE);

        assertTrue(result.valid());
        assertEquals(GameStatus.DRAW, result.status(), "Game should be a draw");
    }

    @Test
    void testMoveResultWithZeroFlips() {
        // Even though this might not be realistic, test the edge case
        MoveResult result = MoveResult.valid(0, GameStatus.IN_PROGRESS, Player.BLACK);

        assertTrue(result.valid());
        assertEquals(0, result.flippedCount(), "Can have 0 flipped pieces");
    }

    @Test
    void testMoveResultWithManyFlips() {
        MoveResult result = MoveResult.valid(15, GameStatus.IN_PROGRESS, Player.WHITE);

        assertTrue(result.valid());
        assertEquals(15, result.flippedCount(), "Should handle large flip count");
    }

    @Test
    void testInvalidMoveHasNullNextPlayer() {
        MoveResult result = MoveResult.invalid();

        assertNull(result.nextPlayer(), "Invalid move should not have a next player");
    }

    @Test
    void testValidMoveWithBothPlayers() {
        MoveResult blackResult = MoveResult.valid(2, GameStatus.IN_PROGRESS, Player.BLACK);
        MoveResult whiteResult = MoveResult.valid(2, GameStatus.IN_PROGRESS, Player.WHITE);

        assertEquals(Player.BLACK, blackResult.nextPlayer());
        assertEquals(Player.WHITE, whiteResult.nextPlayer());
    }

    @Test
    void testMoveResultEquality() {
        MoveResult result1 = MoveResult.valid(3, GameStatus.IN_PROGRESS, Player.WHITE);
        MoveResult result2 = MoveResult.valid(3, GameStatus.IN_PROGRESS, Player.WHITE);

        assertEquals(result1, result2, "Same move results should be equal");
    }

    @Test
    void testMoveResultToString() {
        MoveResult result = MoveResult.valid(3, GameStatus.IN_PROGRESS, Player.WHITE);

        String str = result.toString();
        assertNotNull(str);
        assertTrue(str.length() > 0, "toString should produce non-empty string");
    }
}

