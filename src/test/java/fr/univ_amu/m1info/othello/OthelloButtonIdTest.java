package fr.univ_amu.m1info.othello;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for OthelloButtonId enum.
 */
class OthelloButtonIdTest {

    @Test
    void resetButtonHasCorrectId() {
        assertEquals("ResetButton", OthelloButtonId.RESET.getId());
    }

    @Test
    void undoButtonHasCorrectId() {
        assertEquals("UndoButton", OthelloButtonId.UNDO.getId());
    }

    @Test
    void allButtonIdsAreUnique() {
        OthelloButtonId[] buttons = OthelloButtonId.values();
        assertEquals(2, buttons.length, "Should have exactly 2 button IDs");

        // Verify all values are different
        assertNotEquals(OthelloButtonId.RESET.getId(), OthelloButtonId.UNDO.getId());
    }

    @Test
    void valueOfWorksCorrectly() {
        assertEquals(OthelloButtonId.RESET, OthelloButtonId.valueOf("RESET"));
        assertEquals(OthelloButtonId.UNDO, OthelloButtonId.valueOf("UNDO"));
    }

    @Test
    void fromIdWorksCorrectly() {
        assertEquals(OthelloButtonId.RESET, OthelloButtonId.fromId("ResetButton"));
        assertEquals(OthelloButtonId.UNDO, OthelloButtonId.fromId("UndoButton"));
    }

    @Test
    void fromIdThrowsExceptionForUnknownId() {
        assertThrows(IllegalArgumentException.class, () -> {
            OthelloButtonId.fromId("UnknownButton");
        });
    }
}


