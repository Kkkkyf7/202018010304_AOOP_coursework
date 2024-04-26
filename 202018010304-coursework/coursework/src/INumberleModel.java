/**
 * The INumberleModel interface defines the contract for the Numberle game model, providing a comprehensive set of methods
 * that encapsulate the core functionalities required for the game's operation. This interface serves as a blueprint for
 * implementing the logic and behavior of the Numberle game, ensuring consistency and compatibility across different
 * implementations.
 */
public interface INumberleModel {
    // Maximum number of attempts allowed in the game
    int MAX_ATTEMPTS = 6;

    // Initializes the game state
    void initialize();

    // Process player input and update the game state accordingly
    boolean processInput(String input);

    // Checks if the game is over
    boolean isGameOver();

    // Checks if the game get win
    boolean isGameWon();

    // Retrieves the target number for the current game
    String getTargetNumber();

    // Generates a state array representing the result of comparing a guess to the target
    String[] getState(String target, String guess);

    // Gets the current guess in progress
    StringBuilder getCurrentGuess();

    // Return remaining attempts number
    int getRemainingAttempts();

    // Starts a new game by reinitializing the game state
    void startNewGame();

    // Determine whether error messages should be shown
    boolean isShowErrorMessage();

    // Retrieves the maximum number of attempts allowed in the game
    int getMaxAttempts();

    // Determine whether the target number should be displayed for testing
    boolean isShowTarget();

    // Determine if the target number selection need to be random
    boolean isRandom();
}
