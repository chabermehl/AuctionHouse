/**
 * Created by chabermehl
 * 11/29/18
 * CS 351
 */
package Bank;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BankServer {

    public int portNumber;

    public BankServer(int portNumber) {
        this.portNumber = portNumber;
    }

    public void startBankServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(portNumber);
        while(true) {
            Socket clientSocket = serverSocket.accept();
            BankClient bankClient = new BankClient(clientSocket);
            bankClient.sendMessage(new Message("BANK SERVER", "Welcome!\n" +
                    "To create an account, use the command: Initialize Account,<name>,<Initial Deposit>\n" +
                    "For example: Initialize Account,Steve,400\n" +
                    "---------------------------------------------\n" +
                    "To check your balance, use the command: balance,<bankKey>\n" +
                    "---------------------------------------------\n" +
                    "Add something about auctions houses you can choose here.\n"));
            Thread t = new Thread(bankClient);
            t.start();
        }
    }
}
