package org.example.tictactoe;

import java.util.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class HelloController {

    @FXML
    private GridPane gridPane;

    @FXML
    private Button startButton;

    @FXML
    private HBox difficultyBox; // Assuming you put difficulty buttons in an HBox

    @FXML
    private Label statusLabel;

    @FXML
    private Label chooseDifficulty;


    private char currentPlayer = 'X';
    private final char[][] game = new char[3][3];
    private String difficulty = "user";  // Default to user mode
    private boolean isGameActive = true;  // Flag to indicate if the game is active

    // AI Easy Level: Random move
    @FXML
    void setEasyLevel() {
        difficulty = "easy";
        System.out.println("AI easy move");
    }

    @FXML
    void SetMediumLevel() {
        difficulty = "medium";
        System.out.println("AI Medium move");
    }
    // AI Medium Level: Win if possible, otherwise block opponent, else random move

    @FXML
    void SetHardLevel() {
        difficulty = "hard";
        System.out.println("AI Hard move");
    }


    @FXML
    void SetUser() {
        difficulty = "user"; // Set to user mode
    }

    @FXML
    void btnClick(ActionEvent event) {
        if (!isGameActive) return;

        Button btn = (Button) event.getSource();
        Integer rowIndex = GridPane.getRowIndex(btn);
        Integer columnIndex = GridPane.getColumnIndex(btn);

        // Default to 0 if rowIndex or columnIndex is null
        rowIndex = (rowIndex == null) ? 0 : rowIndex;
        columnIndex = (columnIndex == null) ? 0 : columnIndex;

        if (game[rowIndex][columnIndex] == '\0') { // Ensure only 'X' can make a move
            game[rowIndex][columnIndex] = currentPlayer;
            btn.setText(String.valueOf(currentPlayer));
            btn.setDisable(true);

            // Check for a winner or a draw
            if (checkWin()) {
                statusLabel.setText("Player " + currentPlayer + " wins!");
                endGame();

            } else if (checkDraw()) {
                statusLabel.setText("Draw!");
                endGame();

            } else {
                currentPlayer = currentPlayer == 'X' ? 'O' : 'X';
                statusLabel.setText("Player " + currentPlayer + "'s turn.");
                performAIMove(difficulty);
            }
        }

    }

    @FXML
    void StartGame() {
        difficulty = Objects.requireNonNull(difficulty, "Difficulty level is not selected.");
        if (!"easy".equals(difficulty) && !"medium".equals(difficulty) && !"hard".equals(difficulty) && !"user".equals(difficulty)) {
            System.out.println("Invalid difficulty level selected.");
            return;
        }
        resetGame();

        gridPane.setVisible(true);
        startButton.setVisible(false);
        chooseDifficulty.setVisible(false);
        difficultyBox.setVisible(false);
        statusLabel.setText("Player " + currentPlayer + "'s turn.");

    }


    @FXML
    void initialize() {
        if (gridPane == null) {
            System.err.println("GridPane is null! Check your FXML file.");
        } else {
            System.out.println("GridPane successfully initialized.");
        }
        gridPane.setVisible(false); // Initially hide the grid
        chooseDifficulty.setVisible(true);
        difficultyBox.setVisible(true); // Show difficulty selection
        startButton.setVisible(true); // Show the start button
        statusLabel.setText("Select difficulty and start the game."); // Initial status
        chooseDifficulty.setText("Choose Difficulty:");
    }

    private void performAIMove(String difficulty) {
        if (currentPlayer == 'O' && !"user".equals(difficulty)) {
            int[] move;
            if ("hard".equals(difficulty)) {
                move = findBestMoveHard(game);
            } else if ("medium".equals(difficulty)) {
                move = findBestMoveMedium(game);
            } else {
                move = findBestMoveEasy(game);
            }
            int aiRow = move[0];
            int aiCol = move[1];

            if (aiRow != -1 && aiCol != -1 && game[aiRow][aiCol] == '\0') {
                makeMove(aiRow, aiCol);
                updateBoardAfterAIMove();
            } else {
                System.out.println("No valid move found for AI.");
            }
        }
    }

    private void updateBoardAfterAIMove() {
        for (Node n : gridPane.getChildren()) {
            if (n instanceof Button) {
                Integer rowIndex = GridPane.getRowIndex(n);
                Integer columnIndex = GridPane.getColumnIndex(n);

                rowIndex = (rowIndex == null) ? 0 : rowIndex;
                columnIndex = (columnIndex == null) ? 0 : columnIndex;

                if (game[rowIndex][columnIndex] != '\0' && !n.isDisabled()) {
                    ((Button) n).setText(String.valueOf(game[rowIndex][columnIndex]));
                    n.setDisable(true);
                }
            }
        }

        if (checkWin()) {
            statusLabel.setText("Player " + currentPlayer + " wins!");
            endGame();
        } else if (checkDraw()) {
            statusLabel.setText("Draw!");
            endGame();
        } else {
            currentPlayer = currentPlayer == 'X' ? 'O' : 'X';
            statusLabel.setText("Player " + currentPlayer + "'s turn.");
        }
    }

    private void makeMove(int row, int col) {
        game[row][col] = 'O';
    }


    void resetGame() {
        // Clear the buttons on the grid
        for (Node n : gridPane.getChildren()) {
            if (n instanceof Button) {
                ((Button) n).setText("");
                n.setDisable(false);
            }
        }

        // Reset the internal game state
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                game[i][j] = '\0';
            }
        }

        currentPlayer = 'X';
        statusLabel.setText("Player " + currentPlayer + "'s turn.");
        difficultyBox.setVisible(false); // Show difficulty selection
        startButton.setText("Restart Game");  // Change start button text to "Restart Game"
        startButton.setVisible(false);  // Ensure the restart button is visible
        isGameActive = true;  // Game is active again after reset
    }

    private void endGame() {
        startButton.setText("Restart Game");
        startButton.setVisible(true);
        difficultyBox.setVisible(true);
        isGameActive = false;
    }


    // Check if a player has won the game
        boolean checkWin() {
            return rowWin() || columnWin() || diagonalWin();
        }


        // Check for a row win
        boolean rowWin() {
            return game[0][0] == currentPlayer && game[0][1] == currentPlayer && game[0][2] == currentPlayer ||
                    game[1][0] == currentPlayer && game[1][1] == currentPlayer && game[1][2] == currentPlayer ||
                    game[2][0] == currentPlayer && game[2][1] == currentPlayer && game[2][2] == currentPlayer;
        }

        // Check for a column win
        boolean columnWin() {
            return game[0][0] == currentPlayer && game[1][0] == currentPlayer && game[2][0] == currentPlayer ||
                    game[0][1] == currentPlayer && game[1][1] == currentPlayer && game[2][1] == currentPlayer ||
                    game[0][2] == currentPlayer && game[1][2] == currentPlayer && game[2][2] == currentPlayer;
        }

        // Check for a diagonal win
        boolean diagonalWin() {
            return game[0][0] == currentPlayer && game[1][1] == currentPlayer && game[2][2] == currentPlayer ||
                    game[0][2] == currentPlayer && game[1][1] == currentPlayer && game[2][0] == currentPlayer;
        }

        // Check for a draw
        boolean checkDraw() {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (game[i][j] == '\0') {
                        return false;
                    }
                }
            }
            return true;
        }

        private int[] findBestMoveEasy(char[][] game) {
            Random random = new Random();
            int rowIndex, columnIndex;
            do {
                rowIndex = random.nextInt(3);
                columnIndex = random.nextInt(3);
            } while (game[rowIndex][columnIndex] != '\0');
            return new int[]{rowIndex, columnIndex};
        }

    int[] findBestMoveMedium(char[][] game) {
        int[] winningMove = findWinningMove(game, 'O');
        if (winningMove != null) {
            return winningMove;
        }
        int[] blockingMove = findWinningMove(game, 'X');
        if (blockingMove != null) {
            return blockingMove;
        }
        return findBestMoveEasy(game);
    }

    int[] findWinningMove(char[][] game, char player) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (game[i][j] == '\0') {
                    game[i][j] = player;
                    if (checkWinForPlayer(game, player)) {
                        game[i][j] = '\0'; // Revert the move
                        return new int[]{i, j};
                    }
                    game[i][j] = '\0'; // Revert the move
                }
            }
        }
        return null;
    }

    boolean checkWinForPlayer(char[][] game, char player) {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (game[i][0] == player && game[i][1] == player && game[i][2] == player) {
                return true;
            }
        }
        // Check columns
        for (int i = 0; i < 3; i++) {
            if (game[0][i] == player && game[1][i] == player && game[2][i] == player) {
                return true;
            }
        }
        // Check diagonals
        return (game[0][0] == player && game[1][1] == player && game[2][2] == player) ||
                (game[0][2] == player && game[1][1] == player && game[2][0] == player);
    }

        // Method for the evaluation purpose of the Minimax algorithm
        int[] findBestMoveHard(char[][] game) {
            int bestVal = Integer.MIN_VALUE;
            int[] bestMove = {-1, -1};

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (game[i][j] == '\0') {
                        game[i][j] = 'O';
                        int moveVal = minimax(game, 0, false);
                        game[i][j] = '\0';

                        if (moveVal > bestVal) {
                            bestMove[0] = i;
                            bestMove[1] = j;
                            bestVal = moveVal;
                        }
                    }
                }
            }
            return bestMove;
        }

    int minimax(char[][] board, int depth, boolean isMax) {
        int score = evaluate(board);

        if (score == 10 || score == -10 || checkDraw()) return score;

        if (isMax) {
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == '\0') {
                        board[i][j] = 'O';
                        best = Math.max(best, minimax(board, depth + 1, false));
                        board[i][j] = '\0';
                    }
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == '\0') {
                        board[i][j] = 'X';
                        best = Math.min(best, minimax(board, depth + 1, true));
                        board[i][j] = '\0';
                    }
                }
            }
            return best;
        }
    }

    int evaluate(char[][] b) {
        for (int row = 0; row < 3; row++) {
            if (b[row][0] == b[row][1] && b[row][1] == b[row][2]) {
                if (b[row][0] == 'O') return 10;
                else if (b[row][0] == 'X') return -10;
            }
        }

        for (int col = 0; col < 3; col++) {
            if (b[0][col] == b[1][col] && b[1][col] == b[2][col]) {
                if (b[0][col] == 'O') return 10;
                else if (b[0][col] == 'X') return -10;
            }
        }

        if (b[0][0] == b[1][1] && b[1][1] == b[2][2]) {
            if (b[0][0] == 'O') return 10;
            else if (b[0][0] == 'X') return -10;
        }

        if (b[0][2] == b[1][1] && b[1][1] == b[2][0]) {
            if (b[0][2] == 'O') return 10;
            else if (b[0][2] == 'X') return -10;
        }

        return 0;
    }

}