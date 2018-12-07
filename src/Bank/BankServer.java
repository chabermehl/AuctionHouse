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

/**
 * this class does all the work of handling incoming connections
 */
public class BankServer extends Thread {

    private int portNumber;
    private List<BankClient> clients = new LinkedList<>();
    private ServerSocket serverSocket;

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
                        "To create an account, use the command: InitializeAccount;<Name>;<Initial Deposit>;<Type of Account>\n" +
                        "For example: createAccount;Steve;400;Agent\n" +
                        "---------------------------------------------\n" +
                        "To check your balance, use the command: Balance;<Account Number>\n" +
                        "---------------------------------------------\n" +
                        "To get the available auction houses, use: getAuctionHouses\n" +
                        "---------------------------------------------\n" +
                        "To make a transfer, use: Transfer;<Account Number From>;<Account Number To>;<Amount>"));
                Thread t = new Thread(bankClient);
                t.start();
                clients.add(bankClient);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
