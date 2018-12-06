package AuctionHouse;

import Bank.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

/**
 * The auction house has
 */
public class AuctionHouse {

    // All of the active auctions in this house.
    private LinkedList<Auction> currentAuctions = new LinkedList<>();

    // Info for connecting to bank
    private String bankIP;
    private int bankPort;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private int houseID;
    private int accountNumber;

    // Socket used for talking with the bank
    private Socket bankSocket;
    private AuctionHouseServer ahServer;

    public static void main(String[] args) {
        AuctionHouse auctionHouse = new AuctionHouse(0, "bankIP", 2222);
        auctionHouse.run();
    }

    public AuctionHouse(int houseID, String bankIP, int bankPort) {
        this.bankIP = bankIP;
        this.bankPort = bankPort;
        this.houseID = houseID;
    }

    public void run() {
        // Connect to proxy and make an account
        // connectToBank();

        // set up server
        ahServer = new AuctionHouseServer(2222, this);
        ahServer.start();

        // Make a list of auctioned items for testing
        // t1 = record the time
        while(true) {
            Message message = readMessageFromBank();
            if(message != null) {
                // Process different messages from the bank
                if(message.dataInfo.equals("GetAuctions")) {
                    // Send message to bank with the items
                    sendAuctionsToBank();
                }
                else if(message.dataInfo.equals("Account Creation")) {
                    // Nab our account number
                    accountNumber = Integer.parseInt(message.data);
                }
            }
        }
        // wait for a command to terminate
        // closeBankAccount();
    }

    private Message readMessageFromBank() {
        Object objIn = null;
        try {
            objIn = ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return objIn != null ? (Message)objIn : null;
    }

    private void connectToBank() {
        // Try to connect to the bank
        try {
            bankSocket = new Socket(bankIP, bankPort);
            oos = new ObjectOutputStream(bankSocket.getOutputStream());
            ois = new ObjectInputStream(bankSocket.getInputStream());

            // Create an account with zero balance
            sendMessageToBank(new Message("createAccount", "0,true"));
        } catch (IOException e) {
            System.out.println("Error: Could not connect to bank");
            e.printStackTrace();
        }
    }

    private void sendMessageToBank(Message message) {
        try {
            oos.writeObject(message);
        } catch (IOException e) {
            System.out.println("Failed to send message to bank");
            e.printStackTrace();
        }
    }

    private void sendAuctionsToBank() {
        sendMessageToBank(new Message("Auctions", getAuctionsString()));
    }

    private synchronized void addAuction(Auction auction) {
        currentAuctions.add(auction);
        auction.start();
    }

    public void closeBankAccount() {
        sendMessageToBank(new Message("Close Account", ""));
    }

    public void auctionDone(Auction auction) {
        ahServer.getClientByKey(auction.getBidderKey()).sendMessage(new Message("winner",""));
        currentAuctions.remove(auction);
    }

    private Auction getAuctionByName(String name) {
        for(Auction auction : currentAuctions) {
            if(auction.getItemName().equals(name)) {
                return auction;
            }
        }
        return null;
    }

    public synchronized boolean bid(int key, String name, double amount) {
        Auction auction = getAuctionByName(name);
        if(auction == null) {return false;}

        // Make sure the amount to bid is correct.
        if(amount >= auction.getCurrentBid() + auction.getMinimumBid()) {
            // Try to freeze funds
            sendMessageToBank(new Message("Freeze Funds", Integer.toString(key) + "," + Double.toString(amount)));
            sendMessageToBank(new Message("Unfreeze Funds", Integer.toString(key) + Double.toString(auction.getCurrentBid())));

            // Find the previous bidder by ID, send them a pass notification
            ahServer.getClientByKey(auction.getBidderKey()).sendMessage(new Message("Pass", ""));

            // Update that auction to reset it's timer
            auction.setBid(key, amount);
            return true;
        }
        return false;
    }

    // item format is: (house id, item id, description, minimum bid, current bid)
    public synchronized LinkedList<LinkedList<String>> getItems() {
        return null;
    }

    // Formatted for sending messages
    public synchronized String getAuctionsString() {
        String listString = "";
        StringBuilder sb = new StringBuilder();
        for(Auction auction : currentAuctions) {
            sb.append(auction.getItemName() + ";" + auction.getDescription() +
                    ";" + auction.getMinimumBid() + ";" + auction.getCurrentBid() + ",");
        }
        System.out.println(listString);
        return listString;
    }

}
