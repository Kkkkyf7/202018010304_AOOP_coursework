// NumberleView.java
import javax.swing.*;
import java.awt.*;
import java.util.Observer;
/**
 This class implements a graphical user interface application
 based on the observer pattern to display and manage the state
 and user input of the Numberle game.
 The display and interaction of the game interface by creating GUI components,
 adding listeners, and updating the interface
 */
public class NumberleView implements Observer {
    private final INumberleModel model;
    private final NumberleController controller;
    private final JFrame frame = new JFrame("Numberle");
    private final JTextField inputTextField = new JTextField(3);;
    private final JLabel attemptsLabel = new JLabel("Attempts remaining: ");
    private final Color green = new Color(50, 175, 150);

    private final Color orange = new Color(255, 175, 0);
    private final Color gray = new Color(180, 180, 180);

    public NumberleView(INumberleModel model, NumberleController controller) {
        this.controller = controller;
        this.model = model;
        this.controller.startNewGame();
        if(this.model.isShowTarget())
            JOptionPane.showMessageDialog(frame, "For testing purposes target:"+this.model.getTargetNumber());

        ((NumberleModel)this.model).addObserver(this);
        initializeFrame();
        this.controller.setView(this);
        update((NumberleModel)this.model, null);
    }

    public void initializeFrame() {
        final int[] currentRow = {0}; // Tracks the current row index on the game board
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Sets the default close operation for the frame
        frame.setSize(700, 600); // Sets the size of the frame
        frame.setLayout(new BorderLayout()); // Sets the layout of the frame to BorderLayout

        // Creates a panel for the game board
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(model.getMaxAttempts(), 7));

        // Populates the board panel with buttons representing the game board
        for (int i = 0; i < model.getMaxAttempts(); i++) {
            for (int j = 0; j < 7; j++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(50, 50));
                button.setEnabled(false);
                boardPanel.add(button);
            }
        }

        // Adds the board panel to the frame
        frame.add(boardPanel, BorderLayout.NORTH);

        // Creates a panel for input components
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.X_AXIS));
        center.add(new JPanel()); // Placeholder panel

        // Creates an input panel for user input
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 1));

        inputPanel.add(inputTextField); // Adds a text field for user input

        JButton newGameButton = new JButton("New Game"); // Button to start a new game
        JButton submitButton = new JButton("Submit"); // Button to submit a guess

        // Adds an action listener to the submit button
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String input = inputTextField.getText(); // Retrieves user input from the text field

                // Processes the input and validates it
                boolean isValid = controller.processInput(input);
                if (isValid) {
                    // Updates the game board based on the input
                    for (int i = 0; i < input.length(); i++) {
                        JButton button = (JButton) boardPanel.getComponent(currentRow[0] * 7 + i);
                        button.setText(Character.toString(input.charAt(i))); // display the input from user
                        button.setFont(new Font(button.getFont().getName(), Font.BOLD, 18)); // set the font size
                        button.setForeground(Color.BLACK); // set the font color to black
                        // get the state and set the color according to the state
                        String state = model.getState(model.getTargetNumber(), input)[i];
                        switch (state) {
                            // If the guess in the current position is correct, set the color to green
                            case "CORRECT":
                                button.setBackground(green);
                                break;
                            // If the guess are exist but not in correct position, set the color to orange
                            case "EXIST":
                                button.setBackground(orange);
                                break;
                            //If the guess in the current position is wrong, set the color to gray
                            case "WRONG":
                                button.setBackground(gray);
                                break;
                        }
                    }
                    currentRow[0]++;

                    // Checks if the game is won or lost
                    if (model.isGameWon()) {
                        JOptionPane.showMessageDialog(frame, "You win!");
                        controller.startNewGame();
                        if (model.isShowTarget()) {
                            JOptionPane.showMessageDialog(frame, "Target number:" + model.getTargetNumber());
                        }
                    } else if (model.isGameOver()) {
                        JOptionPane.showMessageDialog(frame, "You lose!");
                        controller.startNewGame();
                        if (model.isShowTarget()) {
                            JOptionPane.showMessageDialog(frame, "Target number:" + model.getTargetNumber());
                        }
                    }
                    newGameButton.setEnabled(true);
                    inputTextField.setText(""); // Clears the input text field
                } else {
                    if (model.isShowErrorMessage()) {
                        JOptionPane.showMessageDialog(frame, "It is an invalid input");
                    }
                }
            }
        });

        // Adds components to the input panel
        inputPanel.add(submitButton);
        newGameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // Starts a new game when the new game button is clicked
                controller.startNewGame();
                if (model.isShowTarget()) {
                    JOptionPane.showMessageDialog(frame, "Target number:" + model.getTargetNumber());
                }

                // Resets the game board and input fields
                for (int i = 0; i < 35; i++) {
                    JButton button = (JButton) boardPanel.getComponent(i);
                    button.setText("");
                    button.setBackground(null);
                }
                currentRow[0] = 0;
                inputTextField.setText("");
                newGameButton.setEnabled(false);
            }
        });
        newGameButton.setEnabled(false); // Disables the new game button initially
        inputPanel.add(newGameButton); // Adds the new game button to the input panel
        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
        inputPanel.add(attemptsLabel); // Adds attempts label to the input panel
        center.add(inputPanel); // Adds the input panel to the center panel
        center.add(new JPanel()); // Placeholder panel
        frame.add(center, BorderLayout.CENTER); // Adds the center panel to the frame

        // Creates a panel for the keyboard
        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new BoxLayout(keyboardPanel, BoxLayout.X_AXIS));
        keyboardPanel.add(new JPanel()); // Placeholder panel
        JPanel numberPanel = new JPanel();
        numberPanel.setLayout(new GridLayout(2, 5));
        keyboardPanel.add(numberPanel);

        // Adds number buttons to the number panel
        for (int i = 0; i < 10; i++) {
            JButton button = new JButton(Integer.toString(i));
            button.setEnabled(true);
            // Adds an action listener to append the button text to the input text field
            button.addActionListener(e -> {
                inputTextField.setText(inputTextField.getText() + button.getText());
            });
            button.setPreferredSize(new Dimension(50, 50));
            numberPanel.add(button);
        }

        // Adds symbol buttons to the number panel
        char[] symbols = new char[]{'+','-','*','/','='};
        for (int i = 0; i < symbols.length; i++) {
            JButton button = new JButton(Character.toString(symbols[i]));
            button.setEnabled(true);
            // Adds an action listener to append the button text to the input text field
            button.addActionListener(e -> {
                inputTextField.setText(inputTextField.getText() + button.getText());
            });
            button.setPreferredSize(new Dimension(50, 50));
            numberPanel.add(button);
        }

        keyboardPanel.add(new JPanel()); // Placeholder panel
        frame.add(keyboardPanel, BorderLayout.SOUTH); // Adds the keyboard panel to the frame
        frame.setVisible(true); // Makes the frame visible
    }


    @Override
    public void update(java.util.Observable o, Object arg) {
        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
    }
}