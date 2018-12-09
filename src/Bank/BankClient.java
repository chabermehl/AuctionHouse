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

    private Socket agentSocket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    /**
     * bank client constructor that starts the object streams
     *
     * @param agentSocket the socket that is being connected to/from
     * @throws IOException
     */
    public BankClient(Socket agentSocket) throws IOException {
        this.agentSocket = agentSocket;
        this.oos = new ObjectOutputStream(agentSocket.getOutputStream());
        this.ois = new ObjectInputStream(agentSocket.getInputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                //grabs the incoming message object
                Message message = (Message) ois.readObject();
                //checks for nulls and emptiness
                if (message.data != null && !message.data.isEmpty()) {
                    //splitting the incoming message to be able to access the
                    //individual elements
                    String[] inMessage = message.data.split(";");
                    //checking if we are creating an account
                    if (message.data.contains("createAccount")) {
                        System.out.println(message.data);
                        //check for agent or auction account then opens a new
                        //account for either one
                        if ("Agent".equals(inMessage[3])) {
                            //creates a new account and send the account
                            //information to the agent
                            Bank.openNewAccount(inMessage[1], Double.parseDouble(inMessage[2]));
                            message = new Message("Account Information: ", Bank.getAccountDetails(Bank.getAccountNumber()));
                            log(Bank.getAccountDetails(Bank.getAccountNumber()));
                        } else if ("Auction".equals(inMessage[3])) {
                            //creates a new account and send the account
                            //information to the auction
                            Bank.openNewAuctionAccount(inMessage[1], Double.parseDouble(inMessage[2]), inMessage[4], inMessage[5]);
                            message = new Message("Account Information: ", Bank.getAuctionAccountDetails(Bank.getAccountNumber()));
                            log(Bank.getAuctionAccountDetails(Bank.getAccountNumber()));
                        } else {
                            System.out.println(message.data);
                            message = new Message("Incorrect Input", "Incorrect Input");
                            log("Incorrect input");
                        }
                        //checks for a message containing Balance
                    } else if (message.data.contains("Balance")) {
                        //grabs the account number that comes in with the balance message
                        int accountNumber = Integer.parseInt(inMessage[1]);
                        log("" + accountNumber);
                        //checks for a valid account number before getting the account info
                        //sends a message back to the agent containing their account information
                        if (Bank.isValidNumber(accountNumber)) {
                            message = new Message("Info", Bank.getAccountDetails(accountNumber));
                            log(Bank.getAccountDetails(accountNumber));
                        } else {
                            message = new Message("Error", "Invalid Account Number");
                            log("Error: Invalid Account Number");
                        }
                        //primarily used by the auction house when a bid has been made
                        //checks if the agent has enough to make a bid then freezes the funds
                        //returns a message one way or the other
                    } else if (message.data.contains("freezeFunds") && !message.data.contains("unfreezeFunds")) {
                        int accountNumber = Integer.parseInt(inMessage[1]);
                        double amount = Double.parseDouble(inMessage[2]);
                        String checkFlag;
                        if (Bank.hasEnoughFunds(accountNumber, amount)) {
                            Bank.setAccountHold(accountNumber, amount);
                            checkFlag = " has ";
                            log("Account Hold Set for: " + amount);
                        } else {
                            checkFlag = " does not have ";
                            log("Does not have enough funds to lock");
                        }
                        message = new Message("Bank", accountNumber + checkFlag + "enough funds.");
                        //checks if the agent is making a transfer after winning
                        //makes a transfer from one account to the other
                    } else if (message.data.contains("Transfer")) {
                        int accountNumA = Bank.getAccountNumber();
                        int accountNumB = Bank.getAccountNumberFromIp(inMessage[1]);
                        double amount = Double.parseDouble(inMessage[2]);
                        Bank.moveMoney(accountNumA, accountNumB, amount);
                        message = new Message("nothing", "transfer complete");
                        //when the agent needs to know what auction houses are available
                    } else if (message.data.contains("getAuctionHouses")) {
                        message = new Message("Auction Houses", Bank.getAuctionString());
                        log(Bank.getAuctionString());
                        //unfreezes funds if a bid is passed so that the agent can make a new bid
                    } else if (message.data.contains("unfreezeFunds")) {
                        Bank.unlockAccount(Integer.parseInt(inMessage[1]),Double.parseDouble(inMessage[2]));
                        log("Account Funds Unlocked");
                        //closes account when prompted to
                    } else if (message.data.contains("closeAccount")) {
                        Bank.closeAccount(Integer.parseInt(inMessage[1]));
                        log("Account Closed: " + inMessage[1]);
                        //rinky dink error message if none of the inputs match
                    } else {
                        message = new Message("Incorrect Input", "Incorrect Input");
                        log("Incorrect Input");
                    }
                }
                //sends the message to the client that is connected
                this.sendMessage(message);
            }
            //catching stuff boiiiiiiiiiiiiiiiiiiiiiiiiiii
        } catch (IOException e) {
            System.out.println(agentSocket.getRemoteSocketAddress() + " has disconnected");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends a message object back to the connected client
     *
     * @param message message object containing a string
     * @throws IOException
     */
    public void sendMessage(Message message) throws IOException {
        oos.writeObject(message);
    }

    /**
     * im lazy
     *
     * @param msg string value to be printed
     */
    private void log(String msg) {
        System.out.println(msg);
    }
}
