package AuctionHouse;

import Bank.Message;

import java.io.*;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Set;

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
    private boolean running = true;

    // Socket used for talking with the bank
    private Socket bankSocket;
    private AuctionHouseServer ahServer;

    public static void main(String[] args) {
        AuctionHouse auctionHouse = new AuctionHouse(0, "localhost", 1234);
        auctionHouse.run();
    }

    public AuctionHouse(int houseID, String bankIP, int bankPort) {
        this.bankIP = bankIP;
        this.bankPort = bankPort;
        this.houseID = houseID;
    }

    public void run() {

        // set up server
        ahServer = new AuctionHouseServer(2222, this);
        ahServer.start();

        // Connect to proxy and make an account
        connectToBank();

        // Read in some auctions
        readInAuctions();

        // Set up command input thread
        AuctionCommandInput commandInput = new AuctionCommandInput();
        new Thread(commandInput).start();

        // Set up message receiver
        MessageReceiver receiver = new MessageReceiver(ois);
        new Thread(receiver).start();


        // The core loop for processing messages
        while(commandInput.getActive()) {
            Message message = receiver.pollNextMessage();
            if(message != null) {
                System.out.println("Received message: " + message.dataInfo + " " + message.data);
                // Process different messages from the bank
                if(message.dataInfo.equals("GetAuctions")) {
                    // Send message to bank with the items
                    sendAuctionsToBank();
                }
                else if(message.dataInfo.equals("Bank Key: ")) {
                    // Nab our account number
                    accountNumber = Integer.parseInt(message.data);
                    System.out.println("Assigned a bank key: " + accountNumber);
                }
            }
        }

        // wait for a command to terminate
        closeBankAccount();
        receiver.shutDown();
        shutDown();
        System.out.println("Shutdown Complete");
        System.exit(0);
    }

    private void connectToBank() {
        // Try to connect to the bank
        try {
            bankSocket = new Socket(bankIP, bankPort);
            oos = new ObjectOutputStream(bankSocket.getOutputStream());
            ois = new ObjectInputStream(bankSocket.getInputStream());

            // Create an account with zero balance
            sendMessageToBank(new Message("Create account", "createAccount;AuctionHouse" +
                    houseID + ";0;Auction;" + ahServer.getPort() + ";" + Inet4Address.getLocalHost().getHostAddress()));

            System.out.println(Inet4Address.getLocalHost().getHostAddress());
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
        System.out.println("Auction added. Name:" + auction.getItemName() +
                " Description:" + auction.getDescription() + " Minimum Bid:" + auction.getMinimumBid());
        currentAuctions.add(auction);
        auction.start();
        sendMessageToBank(new Message("auctionList", getAuctionsString()));
    }

    public void closeBankAccount() {
        sendMessageToBank(new Message("Close Account", "closeAccount;" + accountNumber));
    }

    public void auctionDone(Auction auction) {
        ahServer.getClientByKey(auction.getBidderKey()).sendMessage(new Message("winner",""));
        currentAuctions.remove(auction);
        sendMessageToBank(new Message("auctionList", getAuctionsString()));
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
            sendMessageToBank(new Message("Freeze Funds", Integer.toString(key) + ";" + Double.toString(amount)));
            

            sendMessageToBank(new Message("Unfreeze Funds", Integer.toString(key) + Double.toString(auction.getCurrentBid())));

            // Find the previous bidder by ID, send them a pass notification
            ahServer.getClientByKey(auction.getBidderKey()).sendMessage(new Message("pass", ""));

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
            sb.append(auction.getItemName() + "," + auction.getDescription() +
                    "," + auction.getMinimumBid() + "," + auction.getCurrentBid() + ";");
        }
        System.out.println(listString);
        return listString;
    }

    private void readInAuctions()
    {
        try {
            File file = new File("resources/auctions.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                String[] params = line.split(";");
                addAuction(new Auction(params[0], params[1], Double.parseDouble(params[2])));
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shutDown() {
        ahServer.shutdown();
    }

}
