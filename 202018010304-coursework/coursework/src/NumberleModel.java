// NumberleModel.java
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.*;

/**
 * Implements the logic for the Numberle game, adhering to the INumberleModel interface.
 * This class manages the game state, including the target number, current guess, remaining attempts,
 * and game outcome. It also handles the logic for processing player inputs and determining the game's
 * progress and result.
 */
public class NumberleModel extends Observable implements INumberleModel {

    private String targetNumber; //Current target expression that players aim to guess.
    private StringBuilder currentGuess; // StringBuilder that temporarily stores the current guess of player.
    private int remainingAttempts; //The number of remaining attempts guess for the player.
    private boolean gameWon; // A boolean flag to express whether the player get win.
    private boolean showErrorMessage,showTarget,isRandom; //Flags used to configure the display and behavior of the game.

    /**
     * isShowErrorMessage() is to indicate if it needs an error message display to users.
     * @return true if error messages are required to displayed, otherwise return false.
     */
    public boolean isShowErrorMessage() {
        return showErrorMessage;
    }

    /**
     * isShowErrorMessage() is to decide whether the target equation is shown to the player at the start of the game.
     * @return true if error messages are required to displayed, otherwise return false.
     */
    public boolean isShowTarget() {
        return showTarget;
    }

    /**
     * Specify whether the target equation should be chosen randomly from the available list
     * Equation. When set to true, each game session will randomly select a target equation.
     * If false, the target equation is predictably chosen,
     * @return true if error messages are required to displayed, otherwise return false.
     */
    public boolean isRandom() {
        return isRandom;
    }

    /**
     * Constructor of the NumberleModel with specified game settings.
     */
    public NumberleModel(boolean showErrorMessage, boolean showTarget, boolean isRandom){
        this.showErrorMessage = showErrorMessage; //indicate if the game will display error messages for invalid inputs.
        this.showTarget = showTarget; //indicate if the target equation will be shown at the start of the game.
        this.isRandom = isRandom; //indicate if the target equation will be chosen randomly from the list of equations.
    }
    /**
     * Reads equations from a file and returns them as a list of strings.
     *
     * @return A list of equations read from the file.
     */
    private List<String> getEquationsFromFile() {
        List<String> equations = new ArrayList<>();
        try {
            String filePath = "equations.txt"; // Path to the file containing equations
            Scanner scanner = null;
            try {
                scanner = new Scanner(new FileReader(filePath)); // Attempt to open the file
            } catch (FileNotFoundException e) {
                // If the file is not found at the default location, try looking in the "src" directory
                filePath = "src/" + filePath;
                scanner = new Scanner(new FileReader(filePath));
            }
            while (scanner.hasNext()) {
                // Read each line from the file
                String line = scanner.nextLine();
                // Remove any newline characters from the line
                line = line.replace("\n", "");
                // Skip empty lines
                if (line.length() == 0) continue;
                // Add the non-empty line (equation) to the list
                equations.add(line);
            }
        } catch (FileNotFoundException e) {
            // Throw a runtime exception if the file is still not found
            throw new RuntimeException(e);
        }
        return equations;
    }

    public enum State {
        CORRECT, EXIST, WRONG
    }

    /**
     * Compares the guessed string with the target string character by character and determines the state of each character.
     *
     * @param target The target string to be guessed.
     * @param guess The guessed string.
     * @return An array of strings representing the state of each character in the guessed string compared to the target string.
     *         Each element of the array indicates whether the guessed character is correct, exists elsewhere in the target, or is wrong.
     */
    public String[] getState(String target, String guess) {
        // Initialize an array to store the state of each character in the guessed string
        String[] states = new String[target.length()];

        // Iterate over each character in the target string
        for (int i = 0; i < target.length(); i++) {
            char targetChar = target.charAt(i);
            char guessChar = guess.charAt(i);

            // Compare the character in the target string with the corresponding character in the guessed string
            if (targetChar == guessChar) {
                // If the characters match, set the state to "CORRECT"
                states[i] = State.CORRECT.name();
            } else if (target.contains(String.valueOf(guessChar))) {
                // If the guessed character exists elsewhere in the target string, set the state to "EXIST"
                states[i] = State.EXIST.name();
            } else {
                // If the guessed character is wrong, set the state to "WRONG"
                states[i] = State.WRONG.name();
            }
        }

        // Return the array containing the states of each character in the guessed string
        return states;
    }


    /**
     * Initializes the game by selecting a target equation and resetting game parameters.
     * If isRandom is set to true, it randomly selects a target equation from the available list.
     * Otherwise, it selects the first equation from the list.
     * It also resets the current guess, remaining attempts, and sets the gameWon flag to false.
     */
    @Override
    public void initialize() {
        // Create a new Random object
        Random rand = new Random();
        // Get the list of equations from the file
        List<String> equations = getEquationsFromFile();
        // Select the target equation based on the isRandom attribute
        if (isRandom) {
            // If isRandom is true, randomly select a target equation
            targetNumber = equations.get(rand.nextInt(equations.size()));
        } else {
            // If isRandom is false, select the first equation from the list
            targetNumber = equations.get(0);
        }
        // Initialize the current guess with spaces
        currentGuess = new StringBuilder("       ");
        // Reset the remaining attempts to the maximum allowed
        remainingAttempts = MAX_ATTEMPTS;
        //  Set the gameWon flag to false
        gameWon = false;
        // Notify observers that the game state has changed
        setChanged();
        notifyObservers();
    }

    /**
     * Evaluates the given expression and returns the result.
     * @param expression The expression to be evaluated as a String.
     * @return The result of the expression evaluation as an integer.
     * @throws RuntimeException if the expression is null, empty, or contains invalid characters,
     *                         or if division by zero is attempted.
     */
    private static int evaluate(String expression) {
        // Validate the expression
        assert expression != null; // Ensure expression is not null
        if (expression.isEmpty()) {
            throw new RuntimeException("Expression is empty"); // Throw exception if expression is empty
        }
        // Ensure the first and last characters are digits
        if (!Character.isDigit(expression.charAt(0)) || !Character.isDigit(expression.charAt(expression.length() - 1))) {
            throw new RuntimeException("Invalid expression: Expression must start and end with digits");
        }

        // Initialize lists to store numbers and operators
        ArrayList<String> numbers = new ArrayList<>();
        ArrayList<String> operators = new ArrayList<>();

        // Parse the expression and extract numbers and operators
        String num = "";
        boolean lastNotSymbol = false;
        for (char ch : expression.toCharArray()) {
            if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                // If an operator is encountered, add the preceding number to the list
                if (!lastNotSymbol) {
                    throw new RuntimeException("Invalid expression: Two operators without a number in between");
                }
                lastNotSymbol = false;
                operators.add(ch + "");
                if (!num.isEmpty()) {
                    numbers.add(num);
                    num = "";
                }
            } else {
                // If a digit is encountered, append it to the current number
                lastNotSymbol = true;
                num += ch;
            }
        }
        // Add the last number to the list
        assert !num.isEmpty();
        if (!num.isEmpty()) {
            numbers.add(num);
        }

        // Process multiplication and division operations
        //Process multiplication and division operations until none are left
        while (operators.contains("*") || operators.contains("/")) {
            // Iterate over each operator in the list
            for (int i = 0; i < operators.size(); i++) {
                String oper = operators.get(i);
                // Check if the current operator is '*' or '/'
                if (oper.equals("*") || oper.equals("/")) {
                    // Retrieve the left and right values for the operation
                    int leftValue = Integer.valueOf(numbers.get(i));
                    int rightValue = Integer.valueOf(numbers.get(i + 1));
                    int newValue; // Variable to store the result of the operation

                    // Perform the multiplication or division operation
                    if (oper.equals("*")) {
                        // If the operator is '*', calculate the product
                        newValue = leftValue * rightValue;
                    } else {
                        // If the operator is '/', check for division by zero and perform the division
                        if (rightValue == 0) {
                            // Throw an exception if division by zero is attempted
                            throw new RuntimeException("Invalid expression: Division by zero");
                        }
                        if (leftValue % rightValue != 0) {
                            // Throw an exception if division results in a remainder
                            throw new RuntimeException("Invalid expression: Division with remainder");
                        }
                        newValue = leftValue / rightValue; // Calculate the quotient
                    }

                    // Update the numbers list with the result of the operation
                    operators.remove(i); // Remove the current operator
                    numbers.set(i, String.valueOf(newValue)); // Update the left operand with the result
                    numbers.remove(i + 1); // Remove the right operand
                }
            }
        }


        // Process addition and subtraction operations
        while (!operators.isEmpty()) {
            String oper = operators.get(0);
            int leftValue = Integer.valueOf(numbers.get(0));
            int rightValue = Integer.valueOf(numbers.get(1));
            int newValue;
            if (oper.equals("+")) {
                newValue = leftValue + rightValue;
            } else {
                newValue = leftValue - rightValue;
            }
            operators.remove(0);
            numbers.set(0, String.valueOf(newValue));
            numbers.remove(1);
        }

        // Return the final result
        return Integer.valueOf(numbers.get(0));
    }


    /**
     * This static method parses a string of equations and returns the result of the left and right expressions.
     * It involves verifying the validity of characters,
     * splitting expressions and evaluating the values of expressions
     * @param expression The expression string containing the equation.
     * @return An array containing the evaluated values of the left and right sides of the equation.
     * Index 0 holds the value of the left side, and index 1 holds the value of the right side.
     * Returns null if the expression contains invalid characters or if evaluation fails.
     */
    public static int[] GetTheExpressions(String expression) {
        //  First is to validate characters
        for (char ch : expression.toCharArray()) {
            // Check if the character is a digit or a valid operator
            if (!(Character.isDigit(ch) || ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '=')) {
                return null; // Invalid character found
            }
        }

        // Splitting expression into two parts
        int equalIndex = expression.indexOf('=');
        // Check if '=' exists and if it is the only occurrence
        if (equalIndex == -1 || equalIndex != expression.lastIndexOf('=')) {
            return null;
        }
        //Extract left and right parts of the expression
        String leftPart = expression.substring(0, equalIndex);
        String rightPart = expression.substring(equalIndex + 1);
        try {
            //Evaluate the left and right parts of the expression
            int leftValue = evaluate(leftPart);
            int rightValue = evaluate(rightPart);

            //Return the evaluated values as an array
            return new int[]{leftValue,rightValue};
        } catch (Exception e) {
            return null; // When the valuation get failed, return null
        }
    }

    /**
     * Processes the player's input guess by evaluating it and updating the game state.
     *
     * @param input The player's guess as a String.
     * @return true if the guess is valid and processed, false otherwise.
     */
    @Override
    public boolean processInput(String input) {
        boolean result = false; // Initialize the result flag to false
        // Extract and evaluate the left and right sides of the input expression
        int[] expressions = GetTheExpressions(input);
        //  Check if the expressions are valid and equal
        if (expressions != null && expressions[0] == expressions[1]) {
            // If the expressions are valid and equal, decrement the remaining attempts and set the result flag to true
            remainingAttempts--;
            result = true;
        }
        // Check if remaining attempts have reached zero
        if (remainingAttempts <= 0) {
            // If remaining attempts have reached zero, set the gameWon flag to false
            gameWon = false;
        }
        // Check if the input exactly matches the target number
        if (targetNumber.equals(input)) {
            // If the input exactly matches the target number, set the gameWon flag to true
            gameWon = true;
        }
        // Notify observers that the game state has changed
        setChanged();
        notifyObservers();
        // Return the result flag
        return result;
    }


    // Checks if the game is over by evaluating if the number of remaining attempts has reached zero or if the game has been won.
    @Override
    public boolean isGameOver() {
        return remainingAttempts <= 0 || gameWon;
    }

    // Checks if the game has been won.
    @Override
    public boolean isGameWon() {
        return gameWon;
    }

    // Retrieves the current target number that the player is attempting to guess.
    @Override
    public String getTargetNumber() {
        return targetNumber;
    }

    // Retrieves the current guess StringBuilder.
    // This method may be used to retrieve or modify the current guess in progress.
    // Modification should be done with caution as it directly affects the game state.
    @Override
    public StringBuilder getCurrentGuess() {
        return currentGuess;
    }

    // Retrieves the number of remaining attempts the player has to guess the target number.
    @Override
    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    // Starts a new game by initializing the game state.
    @Override
    public void startNewGame() {
        initialize();
    }

     //Retrieves the number of remaining attempts the player has to guess the target equation.
     //return the number of remaining attempts
    @Override
    public int getMaxAttempts() {
        return MAX_ATTEMPTS;
    }
}
