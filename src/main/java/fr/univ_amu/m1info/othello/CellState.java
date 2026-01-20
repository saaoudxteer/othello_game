package fr.univ_amu.m1info.othello;

/**
 * Représente l'état d'une case du plateau d'Othello.
 * On ne stocke plus directement un Player dans la matrice,
 * mais un CellState qui sait se convertir vers/depuis Player.
 */
public enum CellState {
    EMPTY,
    BLACK,
    WHITE;

    /**
     * Indique si la case est vide.
     */
    public boolean isEmpty() {
        return this == EMPTY;
    }

    /**
     * Convertit un joueur vers un état de case.
     */
    public static CellState fromPlayer(Player player) {
        if (player == null) {
            return EMPTY;
        }
        return switch (player) {
            case BLACK -> BLACK;
            case WHITE -> WHITE;
        };
    }

    /**
     * Convertit un état de case vers un joueur,
     * ou renvoie null si la case est vide.
     */
    public Player toPlayer() {
        return switch (this) {
            case BLACK -> Player.BLACK;
            case WHITE -> Player.WHITE;
            case EMPTY -> null;
        };
    }
}

