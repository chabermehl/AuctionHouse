package Bank;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SpinUpBankServer extends Application {

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

    }

    private void startServer(int portNumber) throws IOException {
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);

            while (true) {
                Socket agentSocket = serverSocket.accept();
                Bank bank = new Bank(agentSocket);
                Thread t = new Thread(bank);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
