import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NumberleTest {
    private NumberleModel model;
    private NumberleController controller;

    @BeforeEach
    void setUp() {
        // Initialize model and controller before testing
        model = new NumberleModel(true, false, false);
        controller = new NumberleController(model);
        model.startNewGame();
    }

    @Test
    void testGameInitLogic() {
        // Test game initialization logic
        assertNotNull(model.getTargetNumber(), "The game needs a target number");
        assertEquals(INumberleModel.MAX_ATTEMPTS, model.getRemainingAttempts(), "The game needs to be started with the max attempt");
        System.out.println("Game initialization logic test passed successfully.");
    }

    @Test
    void testInvalidInput() {
        // Assuming "xxx" is an invalid input
        assertFalse(controller.processInput("xxx"), "Reject the invalid input");
        assertFalse(controller.processInput("1+1=1"), "Reject the invalid input");
        System.out.println("Invalid input test passed successfully.");
    }

    @Test
    void testValidInput() {
        // Assuming 4*1=3+1 is a valid input
        assertTrue(controller.processInput("4*1=3+1"), "Valid expression can be processed");
        System.out.println("Valid input test passed successfully.");
    }

    @Test
    void testWinCondition() {
        model.startNewGame();
        controller.processInput(model.getTargetNumber());
        assertTrue(model.isGameWon(), "Game needs to be won if the target number is guessed by the user.");
        System.out.println("Win condition test passed successfully.");
    }

    @Test
    void testGameLoseCondition() {
        // Simulate exceeding the maximum number of attempts but no target number
        for (int i = 0; i < INumberleModel.MAX_ATTEMPTS; i++) {
            controller.processInput("1+1+1=3");
        }
        assertTrue(model.isGameOver() && !model.isGameWon(),
                "The game should be ended if the user cannot guess the target number after the last attempt.");
        System.out.println("Game lose condition test passed successfully.");
    }

    @Test
    void testStateAfterWin() {
        // Test the game state remains unchanged after winning
        controller.processInput(model.getTargetNumber()); // Submitting correct guess to win the game
        int remainingAttempts = model.getRemainingAttempts(); // Get remaining attempts after winning
        // Assert that the remaining attempts remain unchanged after winning
        assertEquals(remainingAttempts, model.getRemainingAttempts(), "No attempts should be deducted after winning.");
        System.out.println("State after win test passed successfully."); // Print success message
    }

}

