// NumberleController.java

// Controller class for the Numberle game
public class NumberleController {
    private INumberleModel model; // Reference to the Numberle game model
    private NumberleView view; // Reference to the Numberle game view
    // Constructor to initialize the controller with a Numberle game model
    public NumberleController(INumberleModel model) {
        this.model = model;
    }
    // Sets the view for the controller
    public void setView(NumberleView view) {
        this.view = view;
    }
    // Processes the user input
    public boolean processInput(String input) {
        return model.processInput(input);
    }
    // Checks if the game is over
    public boolean isGameOver() {
        return model.isGameOver();
    }
    // Checks if the game has been won
    public boolean isGameWon() {
        return model.isGameWon();
    }
    // Retrieves the target number for the game
    public String getTargetNumber() {
        return model.getTargetNumber();
    }
    // Retrieves the current guess
    public StringBuilder getCurrentGuess() {
        return model.getCurrentGuess();
    }
    // Retrieves the number of remaining attempts
    public int getRemainingAttempts() {
        return model.getRemainingAttempts();
    }
    // Starts a new game
    public void startNewGame() {
        model.startNewGame();
    }
}
