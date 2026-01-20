package fr.univ_amu.m1info.othello;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Modern game over dialog displaying game statistics and results.
 * Shows winner, final scores, move count, and elapsed time.
 */
public class GameOverDialog {
    
    private final Stage dialog;
    private final GameStatus status;
    private final Player winner;
    private final int blackScore;
    private final int whiteScore;
    private final int totalMoves;
    private final String elapsedTime;
    private final String gameMode;

    /**
     * Creates a new game over dialog with the specified game statistics.
     *
     * @param status the final game status
     * @param winner the winner (null if draw)
     * @param blackScore the black player's final score
     * @param whiteScore the white player's final score
     * @param totalMoves the total number of moves played
     * @param elapsedTime the formatted elapsed time string
     * @param gameMode the game mode (e.g., "Player vs Player", "Player vs Easy AI")
     */
    public GameOverDialog(GameStatus status, Player winner, int blackScore, int whiteScore, 
                          int totalMoves, String elapsedTime, String gameMode) {
        this.status = status;
        this.winner = winner;
        this.blackScore = blackScore;
        this.whiteScore = whiteScore;
        this.totalMoves = totalMoves;
        this.elapsedTime = elapsedTime;
        this.gameMode = gameMode;
        
        this.dialog = new Stage();
        initializeDialog();
    }

    /**
     * Initializes and configures the dialog window.
     */
    private void initializeDialog() {
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle("Game Over");
        
        VBox mainContainer = createMainContainer();
        
        Scene scene = new Scene(mainContainer, 450, 420);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
    }

    /**
     * Creates the main container with all dialog content.
     */
    private VBox createMainContainer() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(30));
        container.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;" +
            "-fx-border-color: #3498db;" +
            "-fx-border-width: 2;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 5);"
        );

        // Title
        Label titleLabel = createTitleLabel();
        
        // Result message
        Label resultLabel = createResultLabel();
        
        // Stats container
        VBox statsContainer = createStatsContainer();
        
        // Close button
        Button closeButton = createCloseButton();
        
        container.getChildren().addAll(titleLabel, resultLabel, statsContainer, closeButton);
        
        return container;
    }

    /**
     * Creates the title label.
     */
    private Label createTitleLabel() {
        Label title = new Label("🏁 GAME OVER");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#ecf0f1"));
        title.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 5, 0, 0, 2);");
        return title;
    }

    /**
     * Creates the result label showing the winner or draw.
     */
    private Label createResultLabel() {
        String resultText;
        String resultColor;
        
        if (status == GameStatus.DRAW) {
            resultText = "It's a Draw!";
            resultColor = "#95a5a6";
        } else {
            String winnerName = winner == Player.BLACK ? "Black" : "White";
            resultText = winnerName + " Wins!";
            resultColor = winner == Player.BLACK ? "#2c3e50" : "#ecf0f1";
        }
        
        Label result = new Label(resultText);
        result.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        result.setTextFill(Color.web(resultColor));
        result.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1);" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 10 20 10 20;"
        );
        
        return result;
    }

    /**
     * Creates the statistics container with all game stats.
     */
    private VBox createStatsContainer() {
        VBox statsBox = new VBox(15);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(20));
        statsBox.setStyle(
            "-fx-background-color: rgba(0,0,0,0.2);" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: rgba(52, 152, 219, 0.5);" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;"
        );

        // Score section
        HBox scoreBox = createScoreBox();
        
        // Additional stats
        Label modeLabel = createStatLabel("Mode: " + gameMode);
        Label movesLabel = createStatLabel("Total Moves: " + totalMoves);
        Label timeLabel = createStatLabel("Time Elapsed: " + elapsedTime);
        
        statsBox.getChildren().addAll(scoreBox, createSeparator(), modeLabel, movesLabel, timeLabel);
        
        return statsBox;
    }

    /**
     * Creates the score display box.
     */
    private HBox createScoreBox() {
        HBox scoreBox = new HBox(30);
        scoreBox.setAlignment(Pos.CENTER);
        
        // Black score
        VBox blackBox = createPlayerScoreBox("Black", blackScore, "#000000");
        
        // VS separator
        Label vsLabel = new Label("VS");
        vsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        vsLabel.setTextFill(Color.web("#95a5a6"));
        
        // White score
        VBox whiteBox = createPlayerScoreBox("White", whiteScore, "#ecf0f1");
        
        scoreBox.getChildren().addAll(blackBox, vsLabel, whiteBox);
        
        return scoreBox;
    }

    /**
     * Creates a player score box.
     */
    private VBox createPlayerScoreBox(String playerName, int score, String color) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        
        Label nameLabel = new Label(playerName);
        nameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        nameLabel.setTextFill(Color.web("#bdc3c7"));
        
        Label scoreLabel = new Label(String.valueOf(score));
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        scoreLabel.setTextFill(Color.web(color));
        
        box.getChildren().addAll(nameLabel, scoreLabel);
        
        return box;
    }

    /**
     * Creates a stat label with consistent styling.
     */
    private Label createStatLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        label.setTextFill(Color.web("#ecf0f1"));
        return label;
    }

    /**
     * Creates a visual separator line.
     */
    private Region createSeparator() {
        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setMaxWidth(300);
        separator.setStyle("-fx-background-color: rgba(52, 152, 219, 0.3);");
        return separator;
    }

    /**
     * Creates the close button.
     */
    private Button createCloseButton() {
        Button closeButton = new Button("Close");
        closeButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        closeButton.setPrefWidth(150);
        closeButton.setPrefHeight(40);
        closeButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #3498db, #2980b9);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);"
        );
        
        closeButton.setOnMouseEntered(e -> closeButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #5dade2, #3498db);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 3);" +
            "-fx-scale-x: 1.05;" +
            "-fx-scale-y: 1.05;"
        ));
        
        closeButton.setOnMouseExited(e -> closeButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #3498db, #2980b9);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);"
        ));
        
        closeButton.setOnAction(e -> dialog.close());
        
        return closeButton;
    }

    /**
     * Shows the dialog and waits for it to be closed.
     */
    public void showAndWait() {
        dialog.showAndWait();
    }
}
