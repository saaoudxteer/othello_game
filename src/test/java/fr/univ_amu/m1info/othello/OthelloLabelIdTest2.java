package fr.univ_amu.m1info.othello;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional tests for OthelloLabelId enum.
 */
class OthelloLabelIdTest2 {

    @Test
    void testCurrentPlayerLabelId() {
        assertEquals("CurrentPlayerLabel", OthelloLabelId.CURRENT_PLAYER.getId());
    }

    @Test
    void testErrorLabelId() {
        assertEquals("ErrorLabel", OthelloLabelId.ERROR.getId());
    }

    @Test
    void testTimerLabelId() {
        assertEquals("TimerLabel", OthelloLabelId.TIMER.getId());
    }

    @Test
    void testScoreLabelId() {
        assertEquals("ScoreLabel", OthelloLabelId.SCORE.getId());
    }

    @Test
    void testAllLabelIdsAreUnique() {
        OthelloLabelId[] labels = OthelloLabelId.values();

        assertEquals(4, labels.length, "Should have exactly 4 label IDs");

        // Verify all are different
        assertNotEquals(OthelloLabelId.CURRENT_PLAYER.getId(), OthelloLabelId.ERROR.getId());
        assertNotEquals(OthelloLabelId.CURRENT_PLAYER.getId(), OthelloLabelId.TIMER.getId());
        assertNotEquals(OthelloLabelId.ERROR.getId(), OthelloLabelId.TIMER.getId());
    }

    @Test
    void testValueOfWorksCorrectly() {
        assertEquals(OthelloLabelId.CURRENT_PLAYER, OthelloLabelId.valueOf("CURRENT_PLAYER"));
        assertEquals(OthelloLabelId.ERROR, OthelloLabelId.valueOf("ERROR"));
        assertEquals(OthelloLabelId.TIMER, OthelloLabelId.valueOf("TIMER"));
        assertEquals(OthelloLabelId.SCORE, OthelloLabelId.valueOf("SCORE"));
    }

    @Test
    void testEnumOrdering() {
        OthelloLabelId[] labels = OthelloLabelId.values();

        assertTrue(labels.length > 0, "Should have at least one label");

        // Verify we can iterate through all labels
        for (OthelloLabelId label : labels) {
            assertNotNull(label.getId(), "Each label should have a non-null ID");
        }
    }
}
