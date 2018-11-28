package Bank;

import javafx.application.Application;
import javafx.stage.Stage;
import java.net.ServerSocket;
import java.net.Socket;

public class SpinUpBankServer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

    }

    private void startServer(int portNumber) {

        ServerSocket serverSocket = new ServerSocket(portNumber);

        while (true) {
            Socket agentSocket = serverSocket.accept();
            Bank bank = new Bank(agentSocket);
            Thread t = new Thread(bank);
            t.start();
        }
    }
}
