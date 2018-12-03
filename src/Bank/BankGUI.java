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
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;



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

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle("Bank Server");
        startScene();
    }


    private void startScene() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10,10,10,10));
        grid.setVgap(5);
        grid.setHgap(5);
        TextField portNum = new TextField();
        portNum.setPromptText("What is the port number?");
        portNum.setPrefColumnCount(10);
        portNum.getText();
        GridPane.setConstraints(portNum, 0, 0);

        Button startServerButton = new Button("Start Server");
        startServerButton.setOnAction(event -> {
            startServer(Integer.parseInt(portNum.getText()));
        });
        GridPane.setConstraints(startServerButton, 0,1);

        grid.getChildren().addAll(portNum, startServerButton);
        Scene scene = new Scene(grid, 500,500);
        window.setScene(scene);
        window.show();

    }

    private void startServer(int portNumber) {
        BankServer bankServer = new BankServer(portNumber);
            bankServer.startBankServer();


    }
}
