import javax.swing.*;

//Main class for running the Numberle game as a GUI application.
public class GUIApp {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        createAndShowGUI();
                    }
                }
        );
    }
    //Creates and displays the GUI for the Numberle game.
    public static void createAndShowGUI() {
        // Create a Numberle game model with specified settings
        INumberleModel model = new NumberleModel(true,false,true);
        // Create a controller for the Numberle game
        NumberleController controller = new NumberleController(model);
        // Create a view for the Numberle game
        NumberleView view = new NumberleView(model, controller);
    }
}