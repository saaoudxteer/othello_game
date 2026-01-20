package fr.univ_amu.m1info.othello;

/**
 * Enum for Othello button identifiers.
 */
public enum OthelloButtonId {
    RESET("ResetButton"),
    UNDO("UndoButton");

    private final String id;

    OthelloButtonId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static OthelloButtonId fromId(String id) {
        for (OthelloButtonId buttonId : values()) {
            if (buttonId.id.equals(id)) {
                return buttonId;
            }
        }
        throw new IllegalArgumentException("Unknown button id: " + id);
    }
}

