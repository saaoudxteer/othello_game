package fr.univ_amu.m1info.othello;

/**
 * Enum representing the two players in an Othello game.
 */
public enum Player {
    /** The black player, who plays first in standard Othello. */
    BLACK,

    /** The white player, who plays second in standard Othello. */
    WHITE;

    /**
     * Returns the opponent of this player.
     *
     * @return the opposite player.
     */
    public Player getOpponent() {
        return this == BLACK ? WHITE : BLACK;
    }
}

