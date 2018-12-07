package AuctionHouse;

import Bank.Message;

import java.io.*;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.URL;
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
        if(args.length != 4) {
            System.out.println("Invalid number of args: expected 4.\n" +
                    "Required args: houseID, auctionServerPort, bankIp, bankport");
            return;
        }

        AuctionHouse auctionHouse = new AuctionHouse(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]));
        auctionHouse.run();
    }

    public AuctionHouse(int houseID, int serverPort, String bankIP, int bankPort) {
        this.bankIP = bankIP;
        this.bankPort = bankPort;
        this.houseID = houseID;
        ahServer = new AuctionHouseServer(serverPort, this);
    }

    public void run() {
        // set up server
        ahServer.start();

        // Connect to proxy and make an account
        boolean connectSuccess = connectToBank();

        // Read in some auctions
        readInAuctions();
        // Set up command input thread
        AuctionCommandInput commandInput = new AuctionCommandInput(this);
        new Thread(commandInput).start();

        // Set up message receiver
        MessageReceiver receiver = new MessageReceiver(ois);
        new Thread(receiver).start();

        // The core loop for processing messages
        while(commandInput.getActive() && connectSuccess && currentAuctions.size() > 0) {
            Message message = receiver.pollNextMessage();
            if(message != null) {
                System.out.println("Received message: " + message.dataInfo + " " + message.data);
                String[] params = message.data.split(";");
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

    /**
     * Attempts to create a connection with the bank and sends it a createAccountMessage with 0 balance
     */
    public boolean connectToBank() {
        // Try to connect to the bank
        try {
            bankSocket = new Socket(bankIP, bankPort);
            oos = new ObjectOutputStream(bankSocket.getOutputStream());
            ois = new ObjectInputStream(bankSocket.getInputStream());

            // Create an account with zero balance
            sendMessageToBank(new Message("Create account", "createAccount;AuctionHouse" +
                    houseID + ";0;Auction;" + Inet4Address.getLocalHost().getHostAddress() + ";" + ahServer.getPort()));

            System.out.println(Inet4Address.getLocalHost().getHostAddress());
            return true;
        } catch (IOException e) {
            System.out.println("Error: Could not connect to bank");
            return false;
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

    private void closeBankAccount() {
        sendMessageToBank(new Message("Close Account", "closeAccount;" + accountNumber));
    }

    /**
     * Used for removing finished bids. Sends a win message to whoever won the bid.
     * @param auction
     */
    public void auctionDone(Auction auction) {
        ahServer.getClientByKey(auction.getBidderKey()).sendMessage(new Message("Won bid!","#win;" + auction.getItemName()));
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

    /**
     * Attempts to bid on an auction with the given key of the bidder and
     * the amount they want to bid.
     * @param key of the bidder
     * @param name of the item to bid on
     * @param amount amount to bid
     * @return
     */
    public synchronized boolean bid(int key, String name, double amount) {
        Auction auction = getAuctionByName(name);
        if(auction == null) {return false;}

        // Make sure the amount to bid is correct.
        if(amount >= auction.getCurrentBid() + auction.getMinimumBid()) {
            // Try to freeze funds
            sendMessageToBank(new Message("freezeFunds", Integer.toString(key) + ";" + Double.toString(amount)));

            Message msg = null;
            try {
                msg = (Message)ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            if(msg.data.contains("not have")) {
                return false;
            }

            // Unfreeze the last bidder's funds if they exist
            if(auction.hasBeenBiddenOn()) {
                sendMessageToBank(new Message("unfreezeFunds", Integer.toString(key) + Double.toString(auction.getCurrentBid())));
            }

            // Find the previous bidder by ID, send them a pass notification
            ahServer.getClientByKey(auction.getBidderKey()).sendMessage(new Message("Passed on bid", "#pass;" + auction.getItemName()));

            // Update the auction to reset it's timer
            auction.setBid(key, amount);
            return true;
        }
        return false;
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

    private void readInAuctions() {
        try {

            InputStream file = getClass().getClassLoader().getResourceAsStream("auctions.txt");
            InputStreamReader fileReader = new InputStreamReader(file);

            //URL resource = AuctionHouse.class.getResource("auctions.text");
            //File file = new File("resources/auctions.txt");
            //FileReader fileReader = new FileReader(resource.toString());
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
