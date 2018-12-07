/**
 * Created by chabermehl
 * 11/29/18
 * CS 351
 */
package Bank;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Outward facing bank class that is interacted with
 * Created in BankGUI as a "proxy"
 * Implements runnable as it has a socket connection
 */
public class BankClient implements Runnable {

    public Socket agentSocket;
    public ObjectOutputStream oos;
    public ObjectInputStream ois;

    public BankClient(Socket agentSocket) throws IOException {
        this.agentSocket = agentSocket;
        this.oos = new ObjectOutputStream(agentSocket.getOutputStream());
        this.ois = new ObjectInputStream(agentSocket.getInputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message message = (Message) ois.readObject();
                if (message.data != null && !message.data.isEmpty()) {
                    String[] inMessage = message.data.split(";");
                    if (message.data.contains("createAccount")) {
                        if (inMessage.length < 4) {
                            message = new Message("Incorrect Input", "Incorrect Input");
                        } else {
                            if ("Agent".equals(inMessage[3])) {
                                Bank.openNewAccount(inMessage[1], Double.parseDouble(inMessage[2]));
                                message = new Message("Account Information: ", Bank.getAccountDetails(Bank.getAccountNumber()));
                            } else if ("Auction".equals(inMessage[3])) {
                                Bank.openNewAuctionAccount(inMessage[1], Double.parseDouble(inMessage[2]), inMessage[4], inMessage[5]);
                                message = new Message("Account Information: ", Bank.getAuctionAccountDetails(Bank.getAccountNumber()));
                            }
                        }
                    } else if (message.data.contains("Balance")) {
                        int accountNumber = Integer.parseInt(inMessage[1]);
                        log("" + accountNumber);
                        if (Bank.isValidNumber(accountNumber)) {
                            message = new Message("Info", Bank.getAccountDetails(accountNumber));
                        } else {
                            message = new Message("Error", "Invalid Bank Key");
                        }
                    } else if (message.data.contains("Has Funds")) {
                        int accountNumber = Integer.parseInt(inMessage[1]);
                        double amount = Double.parseDouble(inMessage[2]);
                        String checkFlag;
                        if (Bank.hasEnoughFunds(accountNumber, amount)) {
                            Bank.setAccountHold(accountNumber, amount);
                            checkFlag = " has ";
                        } else {
                            checkFlag = " does not have ";
                        }
                        message = new Message("Bank", accountNumber + checkFlag + "enough funds.");
                    } else if (message.data.contains("Transfer")) {
                        int accountNumA = Integer.parseInt(inMessage[1]);
                        int accountNumB = Integer.parseInt(inMessage[2]);
                        double amount = Double.parseDouble(inMessage[3]);
                        Bank.moveMoney(accountNumA, accountNumB, amount);
                    } else if (message.data.contains("getAuctionHouses")) {
                        message = new Message("Auction Houses", Bank.getAuctionString());
                    }
                }
                this.sendMessage(message);
            }
        } catch (IOException e) {
            System.out.println(agentSocket.getRemoteSocketAddress() + " has disconnected");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) throws IOException {
        oos.writeObject(message);
    }

    private void log(String msg) {
        System.out.println(msg);
    }
}
