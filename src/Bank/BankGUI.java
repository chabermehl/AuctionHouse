/**
 * Created by chabermehl
 * 11/29/18
 * CS 351
 */
package Bank;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Acts as the backend of the bank
 * Opens a gui that asks for a port number to run the server on
 * Every time someone connects a new Bank thread is started in order to handle
 * that user so they do not interfere with each other
 */
public class BankGUI extends Application {

    private Stage window;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * starts the gui
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle("Bank Server");
        startScene();
    }

    /**
     * called in the start method
     * holds the gui code and calls all the necessary methods when the buttons are pushed
     */
    private void startScene() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        //code for getting port number to start server
        Label startServerLabel = new Label("Enter a Port Number for the Server To Run On");
        GridPane.setConstraints(startServerLabel,0,7);

        TextField portNum = new TextField();
        portNum.setPromptText("What is the port number?");
        portNum.setPrefColumnCount(10);
        portNum.getText();
        GridPane.setConstraints(portNum, 0, 8);

        Button startServerButton = new Button("Start Server");
        startServerButton.setOnAction(event -> {
            startServer(Integer.parseInt(portNum.getText()));
        });
        GridPane.setConstraints(startServerButton, 0, 9);

        grid.getChildren().addAll(startServerLabel, portNum, startServerButton);
        Scene scene = new Scene(grid, 500, 500);
        window.setScene(scene);
        window.show();

    }

    /**
     * takes in a port number to start the server on
     * @param portNumber port number for the server to run on
     */
    private void startServer(int portNumber) {
        try {
            BankServer bankServer = new BankServer(portNumber);
            bankServer.startBankServer(bankServer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
