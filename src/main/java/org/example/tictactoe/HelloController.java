package org.example.tictactoe;

import java.net.URL;
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
    private ResourceBundle resources;

    @FXML
    private URL location;

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



    @FXML
    void SetHardLevel(ActionEvent event) {
        difficulty = "hard";
        System.out.println("AI Hard move");
    }

    @FXML
    void SetMediumLevel(ActionEvent event) {
        difficulty = "medium";
        System.out.println("AI Medium move");
    }
    // AI Medium Level: Win if possible, otherwise block opponent, else random move

    @FXML
    void SetUser(ActionEvent event) {
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
                startButton.setText("Restart Game");  // Change start button text to "Restart Game"
                startButton.setVisible(true);  // Show the start button for restarting the game
                difficultyBox.setVisible(true); // Show difficulty selection
                isGameActive = false;  // Game is over

            } else if (checkDraw()) {
                statusLabel.setText("Draw!");
                startButton.setText("Restart Game");  // Change start button text to "Restart Game"
                startButton.setVisible(true);  // Show the start button for restarting the game
                difficultyBox.setVisible(true); // Show difficulty selection
                isGameActive = false;  // Game is over

            } else {
                currentPlayer = currentPlayer == 'X' ? 'O' : 'X';
                statusLabel.setText("Player " + currentPlayer + "'s turn.");
                performAIMove(difficulty);
            }
        }

    }

    @FXML
    void StartGame(ActionEvent event) {
        if (difficulty == null || difficulty.isEmpty()) {
            if (!"user".equals(difficulty)) {
                System.out.println("Please select a difficulty level.");
                return;
            }
        }

        gridPane.setVisible(true);
        startButton.setVisible(false);
        chooseDifficulty.setVisible(false);
        difficultyBox.setVisible(false);
        statusLabel.setText("Player " + currentPlayer + "'s turn.");
        resetGame(); // Use the resetGame() method to initialize the game
    }

    @FXML
    private void initGame() {
        // Reset the buttons on the grid
        for (Node n : gridPane.getChildren()) {
            if (n instanceof Button) {
                ((Button) n).setText("");
                ((Button) n).setDisable(false);
            }
        }

        // Reset the game board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                game[i][j] = '\0';
            }
        }

        // Set the starting player
        currentPlayer = 'X';

        // Update the status label
        statusLabel.setText("Game started! Player " + currentPlayer + "'s turn.");
        startButton.setVisible(false);  // Hide the start button during the game
        isGameActive = true;  // Game is active
    }


    // AI Easy Level: Random move
    @FXML
    void setEasyLevel(ActionEvent event) {
        difficulty = "easy";
        System.out.println("AI easy move");
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
                move = findBestMoveHard(game, 'O');
            } else if ("medium".equals(difficulty)) {
                move = findBestMoveMedium(game, 'O', 'X');
            } else {
                move = findBestMoveEasy(game);
            }
            int aiRow = move[0];
            int aiCol = move[1];

            if (game[aiRow][aiCol] == '\0') {  // Ensure selected cell is empty
                Node aiNode = findAIMove(aiRow, aiCol);

                if (aiNode != null) {
                    makeMove(aiNode);

                    if (checkWin()) {
                        statusLabel.setText("Player " + currentPlayer + " wins!");
                        isGameActive = false;  // Game is over
                        startButton.setText("Restart Game");  // Change start button text to "Restart Game"
                        startButton.setVisible(true);  // Show the start button for restarting the game
                        difficultyBox.setVisible(true); // Hide difficulty selection after AI wins
                    } else if (checkDraw()) {
                        statusLabel.setText("Draw!");
                        isGameActive = false;  // Game is over
                        startButton.setText("Restart Game");  // Change start button text to "Restart Game"
                        startButton.setVisible(true);  // Show the start button for restarting the game
                        difficultyBox.setVisible(true); // Hide difficulty selection after draw
                    } else {
                        currentPlayer = currentPlayer == 'X' ? 'O' : 'X';
                        statusLabel.setText("Player " + currentPlayer + "'s turn.");
                    }
                }
            }
        }
    }

    private Node findAIMove(int row, int col) {
        for (javafx.scene.Node n : gridPane.getChildren()) {
            if (n instanceof Button && GridPane.getRowIndex(n) != null && GridPane.getColumnIndex(n) != null
                    && GridPane.getRowIndex(n).equals(row) && GridPane.getColumnIndex(n).equals(col)) {
                return n;
            }
        }
        return null;
    }



    private void makeMove(Node node) {
            Integer row = GridPane.getRowIndex(node);
            Integer col = GridPane.getColumnIndex(node);

            // Check if row or col is null
            if (row == null || col == null) {
                System.out.println("Row or Column is null");
                return;
            }

            // Find the corresponding button and set its text
            for (javafx.scene.Node n : gridPane.getChildren()) {
                if (n instanceof Button && GridPane.getRowIndex(n) != null && GridPane.getColumnIndex(n) != null
                        && GridPane.getRowIndex(n).equals(row) && GridPane.getColumnIndex(n).equals(col)) {
                    ((Button) n).setText(String.valueOf(currentPlayer));
                    ((Button) n).setDisable(true); // Disable button after move
                    game[row][col] = currentPlayer; // Update game state
                    break;
                }
            }
        }

    void resetGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                game[i][j] = '\0';
            }
        }
        currentPlayer = 'X';
        statusLabel.setText("Player " + currentPlayer + "'s turn.");
        difficultyBox.setVisible(true); // Show difficulty selection
        startButton.setText("Restart Game");  // Change start button text to "Restart Game"
        startButton.setVisible(true);  // Ensure the restart button is visible
        isGameActive = true;  // Game is active again after reset
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

        // Find a winning move for the given player
        int[] findBestMoveMedium(char[][] game, char player, char opponent) {

            // Check for a winning move
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (game[i][j] == '\0') {
                        game[i][j] = player;
                        if (checkWin()) {
                            game[i][j] = '\0';
                            return new int[]{i, j};
                        }
                        game[i][j] = '\0';
                    }
                }
            }

// Block opponent's winning move
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (game[i][j] == '\0') {
                        game[i][j] = opponent;
                        if (checkWin()) {
                            game[i][j] = '\0';
                            return new int[]{i, j};
                        }
                        game[i][j] = '\0';
                    }
                }
            }

            return findBestMoveEasy(game);
        }


        // Method for the evaluation purpose of the Minimax algorithm
        int[] findBestMoveHard(char[][] game, char player) {
            int bestValue = Integer.MIN_VALUE;
            int[] bestMove = new int[]{-1,-1};

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (game[i][j] == '\0') {
                        game[i][j] = player;
                        int moveValue = minimax(game, 0, false, player);
                        game[i][j] = '\0';
                        if (moveValue > bestValue) {
                            bestMove = new int[]{i, j};
                            bestValue = moveValue;
                        }
                    }
                }
            }
            return bestMove;
        }

        // Method for Minimax Algorithm. Its using score method for the evaluation purpose
        int minimax(char[][] game, int depth, boolean isMaximizing, char player) {

            char opponent = (player == 'X') ? 'O' : 'X';

            if (rowWin() || columnWin() || diagonalWin()) {
                return isMaximizing ? -10 + depth : 10 - depth;
            }
            if (checkDraw()) {
                return 0;
            }

            int bestValue;
            if (isMaximizing) {
                bestValue = Integer.MIN_VALUE;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (game[i][j] == '\0') {
                            game[i][j] = player;
                            int value = minimax(game, depth + 1, false, player);
                            game[i][j] = '\0';
                            bestValue = Math.max(bestValue, value);
                        }
                    }
                }
            } else {
                bestValue = Integer.MAX_VALUE;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (game[i][j] == '\0') {
                            game[i][j] = opponent;
                            int value = minimax(game, depth + 1, true, player);
                            game[i][j] = '\0';
                            bestValue = Math.min(bestValue, value);
                        }
                    }
                }
            }
            return bestValue;
        }
    }

