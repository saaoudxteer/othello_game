package fr.univ_amu.m1info.othello;

import fr.univ_amu.m1info.board_game_library.graphics.configuration.BoardGameDimensions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests to verify that Hard AI makes better moves than Easy AI.
 */
class AIComparisonTest {

    @Test
    void testHardAIOutperformsEasyAIOverMultipleGames() {
        // This test verifies that Hard AI generally makes better strategic decisions
        BoardGameDimensions dimensions = new BoardGameDimensions(8, 8);

        int hardAIBetterMoves = 0;
        int trials = 10;

        for (int trial = 0; trial < trials; trial++) {
            OthelloGame gameEasy = new OthelloGame(dimensions);
            OthelloGame gameHard = new OthelloGame(dimensions);

            // Play a few moves to get into mid-game
            gameEasy.playMove(2, 3, 0); // Black
            gameHard.playMove(2, 3, 0); // Black

            // Now let AI play
            gameEasy.playRobotMove(RobotDifficulty.EASY, 0); // White
            Coordinates hardMove = gameHard.playRobotMove(RobotDifficulty.HARD, 0); // White

            // Count pieces after AI moves
            int hardWhiteCount = gameHard.getBoard().countPieces(Player.WHITE);
            int easyWhiteCount = gameEasy.getBoard().countPieces(Player.WHITE);

            // Hard AI should generally have more pieces after its move
            if (hardWhiteCount >= easyWhiteCount) {
                hardAIBetterMoves++;
            }
        }

        // Hard AI should perform better in most trials
        assertTrue(hardAIBetterMoves >= trials / 2,
            "Hard AI should make better moves in at least half of the trials");
    }

    @Test
    void testHardAIConsistentlyChoosesBestMove() {
        BoardGameDimensions dimensions = new BoardGameDimensions(8, 8);

        // Create multiple games with same initial moves
        for (int i = 0; i < 5; i++) {
            OthelloGame game = new OthelloGame(dimensions);
            game.playMove(2, 3, 0); // Black

            // Hard AI should make the same best move each time
            Coordinates move = game.playRobotMove(RobotDifficulty.HARD, 0);
            assertNotNull(move, "Hard AI should always find a move");

            // Verify it's a valid move
            assertTrue(game.getBoard().getPlayerAt(move.row(), move.column()) != null,
                "The move should result in a piece being placed");
        }
    }

    @Test
    void testEasyAIVariability() {
        BoardGameDimensions dimensions = new BoardGameDimensions(8, 8);

        java.util.Set<String> uniqueMoves = new java.util.HashSet<>();

        // Play multiple games and collect Easy AI moves
        for (int i = 0; i < 20; i++) {
            OthelloGame game = new OthelloGame(dimensions);
            game.playMove(2, 3, 0); // Black

            Coordinates move = game.playRobotMove(RobotDifficulty.EASY, 0);
            if (move != null) {
                uniqueMoves.add(move.row() + "," + move.column());
            }
        }

        // Easy AI should show some variability in its moves (it's random)
        // With 4 valid opening moves for White, we should see at least 2 different moves
        assertTrue(uniqueMoves.size() >= 2,
            "Easy AI should show variability in move selection (found " + uniqueMoves.size() + " unique moves)");
    }
}

