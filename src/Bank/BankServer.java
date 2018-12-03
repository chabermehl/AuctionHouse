/**
 * Created by chabermehl
 * 11/29/18
 * CS 351
 */
package Bank;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

public class BankServer extends Thread {

    public int portNumber;
    public List<BankClient> clients = new LinkedList<>();
    public ServerSocket serverSocket;

    public BankServer(int portNumber) throws IOException {
        this.portNumber = portNumber;
        serverSocket = new ServerSocket(portNumber);
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            System.out.println("Current IP: " + ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void startBankServer(BankServer bankServer) {
        bankServer.start();
    }

    @Override
    public void run() {
        System.out.println("Listening for clients on port: " + serverSocket.getLocalPort());
        while (true) {
            try {
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
                clients.add(bankClient);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
