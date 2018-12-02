package AuctionHouse;

import Bank.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
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
    // Use this for creating unique house ids
    private int houseID;

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
        //connectToBank();
        // set up server
        ahServer = new AuctionHouseServer(2222, this);
        ahServer.start();

        // Make a list of auctioned items for testing
        // t1 = record the time
        while(true) {

        }
        // wait for a command to terminate
        // closeBankAccount();
    }

    private void connectToBank() {
        // Try to connect to the bank
        try {
            // Try to connect to bank
            bankSocket = new Socket(bankIP, bankPort);
            oos = new ObjectOutputStream(bankSocket.getOutputStream());
            ois = new ObjectInputStream(bankSocket.getInputStream());

            // Create an account with zero balance
            sendMessageToBank(new Message("Create Account", "0,true"));
        } catch (IOException e) {
            System.out.println("Error: Could not connect to bank");
            e.printStackTrace();
        }
    }

    private void sendMessageToBank(Message message)
    {
        try {
            oos.writeObject(message);
        } catch (IOException e) {
            System.out.println("Failed to send message to bank");
            e.printStackTrace();
        }
    }

    private void sendAuctionsToBank() {
        try {
            oos.writeObject(new Message("Auctions", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void addAuction(Auction auction) {
        currentAuctions.add(auction);
        auction.start();
    }

    public void closeBankAccount() {
        sendMessageToBank(new Message("Close Account", ""));
    }

    private Auction getAuctionByName(String name)
    {
        for(Auction auction : currentAuctions) {
            if(auction.getItemName().equals(name)) {
                return auction;
            }
        }
        return null;
    }

    public synchronized void bid(int key, String name, double amount) {
        Auction auction = getAuctionByName(name);
        if(auction != null) {
            if(amount >= auction.getCurrentBid() + auction.getMinimumBid())
            {
                // Try to freeze funds
                sendMessageToBank(new Message("Freeze Funds", Integer.toString(key) + "," + Double.toString(amount)));
                // if that worked, unfreeze the funds of the bidder that got passed and notify them
                sendMessageToBank(new Message("Unfreeze Funds", Integer.toString(key) + Double.toString(auction.getCurrentBid())));

                // Update that auction to reset it's timer
                auction.setBid(key, amount);
            }
        }
    }

    public void acceptBid(int key,String item) {
        // accept the bid from agent proxy
        // remove funds from acount
    }

    // Bid is rejected if the incoming bid doesn't meet the minimum required,
    // or agent tries to bid with more money than they have
    public void rejectBid(int key,String item){
        // reject the bid from agent proxy
        // unblock the money from bank proxy
    }


    // item format is: (house id, item id, description, minimum bid, current bid)
    public LinkedList<LinkedList<String>> getItems() {
        return null;
    }
}
