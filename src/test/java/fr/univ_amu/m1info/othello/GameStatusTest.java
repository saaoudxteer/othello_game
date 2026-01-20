package fr.univ_amu.m1info.othello;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for GameStatus enum.
 */
class GameStatusTest {

    @Test
    void allGameStatusesExist() {
        GameStatus[] statuses = GameStatus.values();
        assertEquals(3, statuses.length, "Should have exactly 3 game statuses");

        assertTrue(java.util.Arrays.asList(statuses).contains(GameStatus.IN_PROGRESS));
        assertTrue(java.util.Arrays.asList(statuses).contains(GameStatus.FINISHED));
        assertTrue(java.util.Arrays.asList(statuses).contains(GameStatus.DRAW));
    }

    @Test
    void valueOfWorksCorrectly() {
        assertEquals(GameStatus.IN_PROGRESS, GameStatus.valueOf("IN_PROGRESS"));
        assertEquals(GameStatus.FINISHED, GameStatus.valueOf("FINISHED"));
        assertEquals(GameStatus.DRAW, GameStatus.valueOf("DRAW"));
    }

    @Test
    void allStatusesAreUnique() {
        assertNotEquals(GameStatus.IN_PROGRESS, GameStatus.FINISHED);
        assertNotEquals(GameStatus.IN_PROGRESS, GameStatus.DRAW);
        assertNotEquals(GameStatus.FINISHED, GameStatus.DRAW);
    }
}

