/**
 * Created by chabermehl
 * 11/29/18
 * CS 351
 */
package Bank;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BankServer extends Application {

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
        grid.getChildren().add(portNum);

        Button startServerButton = new Button("Start Server");
        startServerButton.setOnAction(event -> {
            try {
                startServer(Integer.parseInt(portNum.getText()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void startServer(int portNumber) throws IOException {

        ServerSocket serverSocket = new ServerSocket(portNumber);

        while (true) {
            Socket agentSocket = serverSocket.accept();
            Bank bank = new Bank(agentSocket);
            Thread t = new Thread(bank);
            t.start();
        }

    }
}
