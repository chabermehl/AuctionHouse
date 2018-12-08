package AuctionHouse;

import Bank.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AuctionClient extends Thread {

    // Agent socket that we'll be talking to
    private Socket socket;
    private AuctionHouseServer ahServer;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private int agentKey = -1;

    // Output and input from messages
    public AuctionClient(Socket socket, AuctionHouseServer server) {
        this.socket = socket;
        this.ahServer = server;
        try {
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while(true) {
                // Process messages from agent
                // Message message = receiver.pollNextMessage();
                Message message = (Message)ois.readObject();
                if(message != null) {
                    processMessage(message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Message readMessage() {
        try {
            Message obj = (Message)ois.readObject();
            if (obj != null) {
                return obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void processMessage(Message message) {
        System.out.println("Auction Client received message: " + message.dataInfo + " " + message.data);
        // Attempt to bid. Send a response back to the agent with results
        if(message.data.contains("bid")) {
            String[] params = message.data.split(";");
            if(params.length != 4) {
                // Not the correct parameter amount
                sendMessage(new Message("Incorrect Bid Input", "Incorrect Bid Input"));
                return;
            }

            int key = Integer.parseInt(params[1]);
            // Set our key if the agent hasn't bid yet.
            if(agentKey == -1) {agentKey = key;}
            boolean bidSuccess = ahServer.auctionHouse.bid(key, params[2], Double.parseDouble(params[3]));
            if(bidSuccess) {
                System.out.println("Bid success");
                sendMessage(new Message("Bid Accepted", "accept;" + params[1]));
            } else {
                System.out.println("Bid fail");
                sendMessage(new Message("Bid Rejected", "reject;" + params[1]));
            }
        }
        else if(message.data.contains("getItems")) {

            sendMessage(new Message("Items:", ahServer.auctionHouse.getAuctionsString()));
        }
    }

    /**
     * Sends a message to the agent that's connected to this client
     * @param message to send
     */
    public void sendMessage(Message message) {
        try {
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("sent message successfully");
    }

    /**
     * Returns the key of the agent that connected Note: this will be -1 until the agent bids for the first time
     * @return
     */
    public int getAgentKey() { return agentKey; }
}
