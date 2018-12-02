package AuctionHouse;

import Bank.Message;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Auction House server class used as a sort of proxy, I guess.
 * Forwards messages to the actual auction house
 */
public class AuctionHouseServer extends Thread {

    private int port;
    private AuctionHouse auctionHouse;
    private ServerSocket serverSocket;

    public AuctionHouseServer(int port, AuctionHouse ahouse) {
        this.port = port; this.auctionHouse = ahouse;
    }
    public void shutdown() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Forward message to auction house when we get a message
    private void forwardMessageToAuctionHouse(Message message) {

    }
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Problem creating auction house server on port " + port);
            e.printStackTrace();
        }

        while (!serverSocket.isClosed()) {
            // Try and accept new client connections
            try {
                serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Server done running");
    }
}