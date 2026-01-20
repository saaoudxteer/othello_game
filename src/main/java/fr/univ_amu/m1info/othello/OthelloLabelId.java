package fr.univ_amu.m1info.othello;

/**
 * Enum for Othello label identifiers.
 */
public enum OthelloLabelId {
    CURRENT_PLAYER("CurrentPlayerLabel"),
    SCORE("ScoreLabel"),
    ERROR("ErrorLabel"),
    TIMER("TimerLabel");

    private final String id;

    OthelloLabelId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

