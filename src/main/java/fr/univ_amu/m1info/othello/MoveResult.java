package fr.univ_amu.m1info.othello;

/**
 * Represents the result of attempting to play a move in Othello.
 */
public record MoveResult(
    boolean valid,
    int flippedCount,
    GameStatus status,
    Player nextPlayer
) {
    /**
     * Creates a result for an invalid move.
     */
    public static MoveResult invalid() {
        return new MoveResult(false, 0, GameStatus.IN_PROGRESS, null);
    }

    /**
     * Creates a result for a valid move.
     */
    public static MoveResult valid(int flippedCount, GameStatus status, Player nextPlayer) {
        return new MoveResult(true, flippedCount, status, nextPlayer);
    }
}

