package fr.univ_amu.m1info.othello;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for the Player enum.
 */
class PlayerTest {
    @Test
    @DisplayName("Black player should have white as opponent")
    void testBlackOpponentIsWhite() {
        assertEquals(Player.WHITE, Player.BLACK.getOpponent());
    }
    @Test
    @DisplayName("White player should have black as opponent")
    void testWhiteOpponentIsBlack() {
        assertEquals(Player.BLACK, Player.WHITE.getOpponent());
    }
    @Test
    @DisplayName("Getting opponent twice should return the original player")
    void testDoubleOpponentReturnsOriginal() {
        assertEquals(Player.BLACK, Player.BLACK.getOpponent().getOpponent());
        assertEquals(Player.WHITE, Player.WHITE.getOpponent().getOpponent());
    }
}
