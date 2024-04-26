import java.util.Scanner;
/**
 * Command Line Interface (CLI) application for playing the Numberle game.
 */
public class CLIApp {
    public static void main(String[] args) {
        // Create a scanner object for user input
        Scanner scanner = new Scanner(System.in);
        // Initialize the Numberle game model with specified settings
        INumberleModel model = new NumberleModel(true, false, true);
        // Start a new game
        model.startNewGame();
        // Retrieve the target number for the current game
        String target = model.getTargetNumber();
        // Display the target number if configured to do so for testing purposes
        if (model.isShowTarget()) {
            System.out.println("For testing purposes, target number: " + target);
        }
        // Main game loop: continue until the game is over or won
        while (!model.isGameOver() && !model.isGameWon()) {
            // Display the number of remaining attempts
            System.out.println("Remaining Attempts: " + model.getRemainingAttempts());
            // Prompt the user to enter their guess
            System.out.println("Please enter your guess: ");
            String guess = scanner.nextLine();
            // Process the user's input guess and check if it's valid
            boolean isValid = model.processInput(guess);
            // If the guess is valid, display the result
            if (isValid) {
                // Generate state array representing the comparison result of the guess to the target
                String[] states = model.getState(target, guess);
                // Display the guess and its comparison result
                for (char state : guess.toCharArray()) {
                    System.out.print(state + " ");
                }
                System.out.println();
                for (String state : states) {
                    System.out.print(state.charAt(0) + " ");
                }
                System.out.println();
                // Check if the game is won
                if (model.isGameWon()) {
                    break;
                }
            } else {
                // If the guess is invalid, display an error message if configured to do so
                if (model.isShowErrorMessage()) {
                    System.out.println("Invalid input.");
                }
            }
        }
        // Game outcome: display win or lose message
        if (model.isGameWon()) {
            System.out.println("You win!");
        } else {
            System.out.println("You lose!");
            // Display the target number when the game is lost
            System.out.println("The target expression is: " + target);
        }
    }
}
