package fr.univ_amu.m1info.othello;

import fr.univ_amu.m1info.board_game_library.graphics.*;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.List;

/**
 * Controller for the Othello game.
 * Handles user interactions with the game board and buttons.
 * Acts as an adapter between the UI (BoardGameView) and the domain (OthelloGame).
 */
public class OthelloController implements BoardGameController {

    private BoardGameView view;
    private final OthelloGame game;
    private final GameTimer timer;
    private long timeOffset = 0L;
    private Coordinates lastRobotMove = null; // mémorise la dernière case jouée par le robot

    // Flag indiquant que le robot est en attente de jouer (durant la PauseTransition)
    private boolean robotThinking = false;
    // Référence à la PauseTransition en cours afin de pouvoir l'annuler
    private PauseTransition robotDelay = null;

    /**
     * Indique si le mode IA (robot) est activé.
     * Si false : joueur vs joueur. Si true : Noir humain vs Blanc robot.
     */
    private boolean aiEnabled = false;

    /**
     * Niveau de difficulté du robot (EASY ou HARD).
     * Utilisé uniquement si aiEnabled est true.
     */
    private RobotDifficulty robotDifficulty = RobotDifficulty.EASY;

    /**
     * Nom du mode de jeu actuel pour l'affichage.
     */
    private String currentGameMode = "Player vs Player";


    /**
     * Creates a new Othello controller.
     *
     * @param game the Othello game instance
     * @param timer the game timer instance
     */
    public OthelloController(OthelloGame game, GameTimer timer) {
        this.game = game;
        this.timer = timer;
    }

    @Override
    public void initializeViewOnStart(BoardGameView view) {
        this.view = view;
        view.addLabel(OthelloLabelId.TIMER.getId(), "00:00");
        updateViewFromGameState();
        timer.start();
    }

    @Override
    public void startUITimer() {
        timer.setOnTick(() -> {
            String elapsedText = formatTime(timer.getElapsedMillis() + timeOffset);
            view.updateLabeledElement(OthelloLabelId.TIMER.getId(), elapsedText);
        });
        timer.start();
    }

    public void stopUITimer() {
        timer.stop();
    }


    @Override
    public void boardActionOnClick(int row, int column) {
        // If robot is thinking, ignore clicks
        if (robotThinking) {
            if (view != null) {
                view.updateLabeledElement(OthelloLabelId.ERROR.getId(), "⚠️ Wait for the robot to play...");
            }
            return;
        }

        // Check if game is over
        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            view.updateLabeledElement(OthelloLabelId.ERROR.getId(), "❌ Game is over!");
            return;
        }

        // Check if cell is empty
        if (!game.getBoard().isEmpty(row, column)) {
            view.updateLabeledElement(OthelloLabelId.ERROR.getId(), "❌ Cell occupied!");
            return;
        }

        MoveResult result = game.playMove(row, column, timer.getElapsedMillis() + timeOffset);

        if (!result.valid()) {
            view.updateLabeledElement(OthelloLabelId.ERROR.getId(), "❌ Invalid move!");
            return;
        }

        // Clear previous robot highlight when human plays
        lastRobotMove = null;

        view.updateLabeledElement(OthelloLabelId.ERROR.getId(), "");
        updateViewFromGameState();

        // Check for game over
        if (result.status() != GameStatus.IN_PROGRESS) {
            handleGameOver();
            return;
        }

        // Si IA activée et c'est au tour du robot (WHITE), attendre 1s puis jouer
        if (aiEnabled && game.getCurrentPlayer() == Player.WHITE && game.getStatus() == GameStatus.IN_PROGRESS) {
            // cancel any previous pending delay just in case
            if (robotDelay != null) {
                robotDelay.stop();
                robotDelay = null;
            }
            robotThinking = true;
            robotDelay = new PauseTransition(Duration.seconds(1));
            robotDelay.setOnFinished(evt -> {
                // perform robot move with current difficulty level
                Coordinates robotChoice = game.playRobotMove(robotDifficulty, timer.getElapsedMillis() + timeOffset);
                robotThinking = false;
                robotDelay = null;
                if (robotChoice != null) {
                    lastRobotMove = robotChoice;
                    updateViewFromGameState();
                    if (game.getStatus() != GameStatus.IN_PROGRESS) {
                        handleGameOver();
                    }
                }
            });
            robotDelay.play();
        }
    }


    @Override
    public void buttonActionOnClick(String buttonId) {
        // D'abord, gérer les boutons de mode de jeu, qui n'appartiennent pas à OthelloButtonId
        if ("MODE_PVP".equals(buttonId)) {
            // cancel pending robot delay
            if (robotDelay != null) {
                robotDelay.stop();
                robotDelay = null;
            }
            robotThinking = false;
            setAiEnabled(false);
            currentGameMode = "Player vs Player";
            lastRobotMove = null;
            handleReset(); // on repart d'une nouvelle partie en mode JCJ
            if (view != null) {
                view.updateLabeledElement(OthelloLabelId.ERROR.getId(), "Mode: Player vs Player");
            }
            return;
        }
        if ("MODE_PVAI_EASY".equals(buttonId)) {
            if (robotDelay != null) {
                robotDelay.stop();
                robotDelay = null;
            }
            robotThinking = false;
            setAiEnabled(true);
            setRobotDifficulty(RobotDifficulty.EASY);
            currentGameMode = "Player vs Easy AI";
            lastRobotMove = null;
            handleReset(); // nouvelle partie en mode joueur vs robot facile
            if (view != null) {
                view.updateLabeledElement(OthelloLabelId.ERROR.getId(), "Mode: Player vs Easy AI");
            }
            return;
        }
        if ("MODE_PVAI_HARD".equals(buttonId)) {
            if (robotDelay != null) {
                robotDelay.stop();
                robotDelay = null;
            }
            robotThinking = false;
            setAiEnabled(true);
            setRobotDifficulty(RobotDifficulty.HARD);
            currentGameMode = "Player vs Hard AI";
            lastRobotMove = null;
            handleReset(); // nouvelle partie en mode joueur vs robot difficile
            if (view != null) {
                view.updateLabeledElement(OthelloLabelId.ERROR.getId(), "Mode: Player vs Hard AI");
            }
            return;
        }

        // Ensuite, gérer les boutons standards RESET / UNDO via OthelloButtonId
        try {
            OthelloButtonId button = OthelloButtonId.fromId(buttonId);
            switch (button) {
                case RESET -> {
                    if (robotDelay != null) {
                        robotDelay.stop();
                        robotDelay = null;
                    }
                    robotThinking = false;
                    handleReset();
                }
                case UNDO -> {
                    if (robotDelay != null) {
                        robotDelay.stop();
                        robotDelay = null;
                        robotThinking = false;
                    }
                    handleUndo();
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Unknown button id: " + buttonId, e);
        }
    }

    /**
     * Handles the reset button action.
     */
    private void handleReset() {
        game.reset();
        timer.reset();
        timeOffset = 0L;
        lastRobotMove = null;
        view.updateLabeledElement(OthelloLabelId.TIMER.getId(), "00:00");
        updateViewFromGameState();
        stopUITimer();
        startUITimer();
    }

    /**
     * Handles the undo button action.
     */
    private void handleUndo() {
        // En mode IA, on veut annuler le tour complet de l'humain (robot + humain)
        if (aiEnabled) {
            // annuler le dernier coup (généralement robot)
            if (!game.undo()) {
                view.updateLabeledElement(OthelloLabelId.ERROR.getId(), "⚠️ No move to undo.");
                return;
            }
            // annuler le coup précédent (humain)
            if (!game.undo()) {
                // aucun autre coup, ok
            }
            lastRobotMove = null;
        } else {
            if (!game.undo()) {
                view.updateLabeledElement(OthelloLabelId.ERROR.getId(), "⚠️ No move to undo.");
                return;
            }
        }

        // Recalculer l'affichage du timer à partir du dernier snapshot (ou 0 si plus d'historique)
        long snapshotTime = game.getLastSnapshotElapsedMillis();
        timer.stop();
        timer.reset();
        timer.start();
        timeOffset = snapshotTime - timer.getElapsedMillis();
        view.updateLabeledElement(OthelloLabelId.ERROR.getId(), "");
        view.updateLabeledElement(OthelloLabelId.TIMER.getId(), formatTime(snapshotTime));
        updateViewFromGameState();
    }

    /**
     * Active ou désactive le mode robot.
     */
    public void setAiEnabled(boolean aiEnabled) {
        this.aiEnabled = aiEnabled;
    }

    /**
     * Définit le niveau de difficulté du robot.
     */
    public void setRobotDifficulty(RobotDifficulty difficulty) {
        this.robotDifficulty = difficulty;
    }

    /**
     * Obtient le niveau de difficulté actuel du robot.
     */
    public RobotDifficulty getRobotDifficulty() {
        return this.robotDifficulty;
    }

    /**
     * Updates the entire view to match the current game state.
     */
    private void updateViewFromGameState() {
        // Clear and redraw board
        view.resetBoard();
        setDefaultBoardColors();

        // Draw all pieces
        OthelloBoard board = game.getBoard();
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                Player player = board.getPlayerAt(row, col);
                if (player != null) {
                    Color pieceColor = player == Player.BLACK ? Color.BLACK : Color.WHITE;
                    view.addShapeAtCell(row, col, Shape.CIRCLE, pieceColor);
                }
            }
        }

        // Colorer en rouge la dernière case jouée par le robot
        if (lastRobotMove != null) {
            view.setCellColor(lastRobotMove.row(), lastRobotMove.column(), Color.RED);
            // redraw piece if it exists
            Player p = board.getPlayerAt(lastRobotMove.row(), lastRobotMove.column());
            if (p != null) {
                Color pieceColor = p == Player.BLACK ? Color.BLACK : Color.WHITE;
                view.addShapeAtCell(lastRobotMove.row(), lastRobotMove.column(), Shape.CIRCLE, pieceColor);
            }
        }

        // Update labels
        updateCurrentPlayerLabel();
        updateScoreLabel();

        // Show suggestions
        showSuggestions();
    }

    /**
     * Shows suggestion overlays for all valid moves of the current player.
     */
    private void showSuggestions() {
        view.clearSuggestions();

        List<Coordinates> validMoves = game.getValidMoves(game.getCurrentPlayer());
        for (Coordinates coord : validMoves) {
            view.addShapeAtCell(coord.row(), coord.column(), Shape.SUGGESTION, Color.BLACK);
        }
    }

    /**
     * Handles game over scenario.
     */
    private void handleGameOver() {
        // Capture time BEFORE stopping the timer
        String timeElapsed = formatTime(timer.getElapsedMillis() + timeOffset);
        
        stopUITimer();

        OthelloBoard board = game.getBoard();
        int blackScore = board.countPieces(Player.BLACK);
        int whiteScore = board.countPieces(Player.WHITE);
        int totalMoves = game.getTotalMoves();
        
        // Show modern game over dialog
        GameOverDialog dialog = new GameOverDialog(
            game.getStatus(),
            game.getWinner(),
            blackScore,
            whiteScore,
            totalMoves,
            timeElapsed,
            currentGameMode
        );
        dialog.showAndWait();
    }

    /**
     * Updates the current player label in the view.
     */
    private void updateCurrentPlayerLabel() {
        Player current = game.getCurrentPlayer();
        if (aiEnabled) {
            // si mode JvR : quand c'est au joueur humain (BLACK), afficher "Your turn"
            if (current == Player.BLACK) {
                view.updateLabeledElement(OthelloLabelId.CURRENT_PLAYER.getId(), "Your turn");
                return;
            }
            // si mode JvR : quand c'est au robot (WHITE), afficher "Robot turn"
            if (current == Player.WHITE) {
                view.updateLabeledElement(OthelloLabelId.CURRENT_PLAYER.getId(), "Robot turn");
                return;
            }
        }
        String playerName = current == Player.BLACK ? "Black" : "White";
        view.updateLabeledElement(OthelloLabelId.CURRENT_PLAYER.getId(), "Current Player: " + playerName);
    }

    /**
     * Updates the score label in the view with current piece counts.
     */
    private void updateScoreLabel() {
        OthelloBoard board = game.getBoard();
        int blackScore = board.countPieces(Player.BLACK);
        int whiteScore = board.countPieces(Player.WHITE);
        view.updateLabeledElement(OthelloLabelId.SCORE.getId(),
            "Score: Black " + blackScore + " - White " + whiteScore);
    }

    /**
     * Sets the default color pattern for the board cells.
     */
    private void setDefaultBoardColors() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boolean isEvenCell = (row + col) % 2 == 0;
                Color cellColor = isEvenCell ? Color.GREEN : Color.DARKGREEN;
                view.setCellColor(row, col, cellColor);
            }
        }
    }

    /**
     * Formats a duration in milliseconds into a human readable string.
     *
     * @param millis duration in milliseconds
     * @return formatted time string in the format "MM:SS"
     */
    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
