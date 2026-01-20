package fr.univ_amu.m1info.othello;

import fr.univ_amu.m1info.board_game_library.graphics.configuration.BoardGameDimensions;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Represents an Othello game with pure business logic.
 * Manages game state, move execution, and history.
 */
public class OthelloGame {
    private final OthelloBoard board;
    private Player currentPlayer;
    private final Deque<MoveSnapshot> history;
    private GameStatus status;
    private int totalMoves;

    /**
     * Creates a new Othello game with the specified dimensions.
     *
     * @param dimensions the board dimensions
     */
    public OthelloGame(BoardGameDimensions dimensions) {
        this.board = new OthelloBoard();
        this.currentPlayer = Player.BLACK;
        this.history = new ArrayDeque<>();
        this.status = GameStatus.IN_PROGRESS;
        this.totalMoves = 0;
    }

    /**
     * Resets the game to its initial state.
     */
    public void reset() {
        board.reset();
        currentPlayer = Player.BLACK;
        history.clear();
        status = GameStatus.IN_PROGRESS;
        totalMoves = 0;
    }

    /**
     * Attempts to play a move at the specified position.
     *
     * @param row the row coordinate
     * @param col the column coordinate
     * @return the result of the move attempt
     */
    public MoveResult playMove(int row, int col, long elapsedMillis) {
        // Validate move
        if (!board.isValidMove(row, col, currentPlayer)) {
            return MoveResult.invalid();
        }

        // Save snapshot before move
        history.push(new MoveSnapshot(
            board.getBoardSnapshot(),
            currentPlayer,
            elapsedMillis
        ));

        // Execute move
        int flippedCount = board.executeMove(row, col, currentPlayer);
        totalMoves++;

        // Switch player and check game state
        currentPlayer = currentPlayer.getOpponent();
        updateGameStatus();

        return MoveResult.valid(flippedCount, status, currentPlayer);
    }

    /**
     * Undoes the last move.
     *
     * @return true if undo was successful, false if no move to undo
     */
    public boolean undo() {
        if (history.isEmpty()) {
            return false;
        }

        MoveSnapshot snapshot = history.pop();
        board.setBoardFromSnapshot(snapshot.boardSnapshot());
        currentPlayer = snapshot.currentPlayer();
        status = GameStatus.IN_PROGRESS;

        return true;
    }

    /**
     * Gets the elapsed time from the last snapshot (for undo).
     *
     * @return elapsed time in milliseconds, or 0 if no history
     */
    public long getLastSnapshotElapsedMillis() {
        if (history.isEmpty()) {
            return 0;
        }
        return history.peek().elapsedMillis();
    }

    /**
     * Gets all valid moves for the specified player.
     *
     * @param player the player
     * @return list of valid move coordinates
     */
    public List<Coordinates> getValidMoves(Player player) {
        List<Coordinates> validMoves = new java.util.ArrayList<>();
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                if (board.isValidMove(row, col, player)) {
                    validMoves.add(new Coordinates(row, col));
                }
            }
        }
        return validMoves;
    }

    /**
     * Checks if the specified player has any valid moves.
     *
     * @param player the player
     * @return true if player has valid moves
     */
    public boolean hasValidMoves(Player player) {
        return board.hasValidMoves(player);
    }

    /**
     * Gets the game board.
     *
     * @return the board
     */
    public OthelloBoard getBoard() {
        return board;
    }

    /**
     * Gets the current player.
     *
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Gets the game status.
     *
     * @return the game status
     */
    public GameStatus getStatus() {
        return status;
    }

    /**
     * Updates the game status based on player moves availability.
     */
    private void updateGameStatus() {
        // Check if current player can move
        if (!board.hasValidMoves(currentPlayer)) {
            // Current player can't move, switch back
            currentPlayer = currentPlayer.getOpponent();

            // Check if opponent can move
            if (!board.hasValidMoves(currentPlayer)) {
                // Neither player can move - game over
                int blackCount = board.countPieces(Player.BLACK);
                int whiteCount = board.countPieces(Player.WHITE);

                if (blackCount == whiteCount) {
                    status = GameStatus.DRAW;
                } else {
                    status = GameStatus.FINISHED;
                }
            }
        }
    }

    /**
     * Gets the total number of moves played in the game.
     *
     * @return the total number of moves
     */
    public int getTotalMoves() {
        return totalMoves;
    }

    /**
     * Gets the winner of the game (only valid if status is FINISHED).
     *
     * @return the winning player, or null if game is not finished or is a draw
     */
    public Player getWinner() {
        if (status != GameStatus.FINISHED) {
            return null;
        }

        int blackCount = board.countPieces(Player.BLACK);
        int whiteCount = board.countPieces(Player.WHITE);

        if (blackCount > whiteCount) {
            return Player.BLACK;
        } else if (whiteCount > blackCount) {
            return Player.WHITE;
        }

        return null;
    }

    /**
     * Joue un coup aléatoire pour le joueur courant (utile pour un robot).
     *
     * @param elapsedMillis temps écoulé (pour l'historique/undo)
     * @return la coordonnée jouée, ou null si aucun coup n'est possible
     */
    public Coordinates playRandomMove(long elapsedMillis) {
        java.util.List<Coordinates> validMoves = getValidMoves(currentPlayer);
        if (validMoves.isEmpty()) {
            return null;
        }
        java.util.Random random = new java.util.Random();
        Coordinates choice = validMoves.get(random.nextInt(validMoves.size()));
        // utilise playMove pour appliquer le coup et gérer l'historique
        playMove(choice.row(), choice.column(), elapsedMillis);
        return choice;
    }

    /**
     * Joue le meilleur coup pour le joueur courant (celui qui retourne le plus de pièces).
     * Utile pour un robot en mode difficile.
     *
     * @param elapsedMillis temps écoulé (pour l'historique/undo)
     * @return la coordonnée jouée, ou null si aucun coup n'est possible
     */
    public Coordinates playBestMove(long elapsedMillis) {
        java.util.List<Coordinates> validMoves = getValidMoves(currentPlayer);
        if (validMoves.isEmpty()) {
            return null;
        }

        // Trouver le coup qui retourne le plus de pièces
        Coordinates bestMove = null;
        int maxFlips = -1;

        for (Coordinates move : validMoves) {
            int flipsCount = board.findAllFlippablePieces(move.row(), move.column(), currentPlayer).size();
            if (flipsCount > maxFlips) {
                maxFlips = flipsCount;
                bestMove = move;
            }
        }

        // Jouer le meilleur coup
        if (bestMove != null) {
            playMove(bestMove.row(), bestMove.column(), elapsedMillis);
        }

        return bestMove;
    }

    /**
     * Joue un coup pour le robot en fonction du niveau de difficulté.
     *
     * @param difficulty le niveau de difficulté (EASY ou HARD)
     * @param elapsedMillis temps écoulé (pour l'historique/undo)
     * @return la coordonnée jouée, ou null si aucun coup n'est possible
     */
    public Coordinates playRobotMove(RobotDifficulty difficulty, long elapsedMillis) {
        return switch (difficulty) {
            case EASY -> playRandomMove(elapsedMillis);
            case HARD -> playBestMove(elapsedMillis);
        };
    }
}
