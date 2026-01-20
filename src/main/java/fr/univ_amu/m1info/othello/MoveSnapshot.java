package fr.univ_amu.m1info.othello;

/**
 * Represents a snapshot of game state for undo functionality.
 */
public record MoveSnapshot(
    CellState[][] boardSnapshot,
    Player currentPlayer,
    long elapsedMillis
) {
}

