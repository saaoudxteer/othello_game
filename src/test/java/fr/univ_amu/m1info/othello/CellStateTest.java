package fr.univ_amu.m1info.othello;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CellState enum.
 */
class CellStateTest {

    @Test
    void toPlayerReturnsCorrectPlayer() {
        assertEquals(Player.BLACK, CellState.BLACK.toPlayer());
        assertEquals(Player.WHITE, CellState.WHITE.toPlayer());
        assertNull(CellState.EMPTY.toPlayer(), "EMPTY should return null");
    }

    @Test
    void fromPlayerReturnsCorrectCellState() {
        assertEquals(CellState.BLACK, CellState.fromPlayer(Player.BLACK));
        assertEquals(CellState.WHITE, CellState.fromPlayer(Player.WHITE));
        assertEquals(CellState.EMPTY, CellState.fromPlayer(null), "null player should return EMPTY");
    }

    @Test
    void allCellStatesExist() {
        CellState[] states = CellState.values();
        assertEquals(3, states.length, "Should have exactly 3 cell states");

        assertTrue(java.util.Arrays.asList(states).contains(CellState.EMPTY));
        assertTrue(java.util.Arrays.asList(states).contains(CellState.BLACK));
        assertTrue(java.util.Arrays.asList(states).contains(CellState.WHITE));
    }

    @Test
    void valueOfWorksCorrectly() {
        assertEquals(CellState.EMPTY, CellState.valueOf("EMPTY"));
        assertEquals(CellState.BLACK, CellState.valueOf("BLACK"));
        assertEquals(CellState.WHITE, CellState.valueOf("WHITE"));
    }

    @Test
    void roundTripConversionPreservesValue() {
        // Test BLACK
        Player blackPlayer = CellState.BLACK.toPlayer();
        CellState blackState = CellState.fromPlayer(blackPlayer);
        assertEquals(CellState.BLACK, blackState);

        // Test WHITE
        Player whitePlayer = CellState.WHITE.toPlayer();
        CellState whiteState = CellState.fromPlayer(whitePlayer);
        assertEquals(CellState.WHITE, whiteState);

        // Test EMPTY
        Player emptyPlayer = CellState.EMPTY.toPlayer();
        CellState emptyState = CellState.fromPlayer(emptyPlayer);
        assertEquals(CellState.EMPTY, emptyState);
    }
}

