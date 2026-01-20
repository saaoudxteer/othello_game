package fr.univ_amu.m1info.othello;

import fr.univ_amu.m1info.board_game_library.graphics.*;
import fr.univ_amu.m1info.board_game_library.graphics.configuration.*;
import fr.univ_amu.m1info.board_game_library.graphics.javafx.timer.JavaFXGameTimer;

import java.util.List;

/**
 * Main application class for launching the Othello game.
 */
public class OthelloApplication {

    public static void main(String[] args) {
        // Create board dimensions
        BoardGameDimensions dimensions = new BoardGameDimensions(8, 8);

        // Create the board configuration for 8x8 Othello board
        BoardGameConfiguration configuration = new BoardGameConfiguration(
                "Othello Game",
                dimensions,
                List.of(
                        new LabeledElementConfiguration("Reset Game", OthelloButtonId.RESET.getId(), LabeledElementKind.BUTTON),
                        new LabeledElementConfiguration("Undo", OthelloButtonId.UNDO.getId(), LabeledElementKind.BUTTON),
                        new LabeledElementConfiguration("Player vs Player", "MODE_PVP", LabeledElementKind.BUTTON),
                        new LabeledElementConfiguration("Easy AI", "MODE_PVAI_EASY", LabeledElementKind.BUTTON),
                        new LabeledElementConfiguration("Hard AI", "MODE_PVAI_HARD", LabeledElementKind.BUTTON),
                        new LabeledElementConfiguration("Current Player: Black", OthelloLabelId.CURRENT_PLAYER.getId(), LabeledElementKind.TEXT),
                        new LabeledElementConfiguration("Score: Black 2 - White 2", OthelloLabelId.SCORE.getId(), LabeledElementKind.TEXT),
                        new LabeledElementConfiguration("", OthelloLabelId.ERROR.getId(), LabeledElementKind.TEXT)
                )
        );

        // Create the game (domain logic)
        OthelloGame game = new OthelloGame(dimensions);

        // Create the timer
        GameTimer timer = new JavaFXGameTimer();

        // Create the controller (adapter between UI and domain)
        OthelloController controller = new OthelloController(game, timer);

        // Par défaut, lançons le jeu en mode Joueur vs Joueur
        controller.setAiEnabled(false);

        BoardGameController genericController = controller;

        // Launch the application
        BoardGameApplicationLauncher launcher = JavaFXBoardGameApplicationLauncher.getInstance();
        launcher.launchApplication(configuration, genericController);
    }
}