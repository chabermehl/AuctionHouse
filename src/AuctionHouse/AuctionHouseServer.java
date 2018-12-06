package AuctionHouse;

import Bank.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Auction House server class used as a sort of proxy, I guess.
 * Forwards messages to the actual auction house
 */
public class AuctionHouseServer extends Thread {

    private int port;
    public AuctionHouse auctionHouse;
    private ServerSocket serverSocket;
    private LinkedList<AuctionClient> clients = new LinkedList<AuctionClient>();

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
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Problem creating auction house server on port " + port);
        }
        while (!serverSocket.isClosed()) {
            // Try and accept new client connections
            try {
                Socket agentSocket = serverSocket.accept();
                AuctionClient client = new AuctionClient(agentSocket, this);
                clients.add(client);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Server done running");
    }

    public AuctionClient getClientByKey(int key)
    {
        for(AuctionClient client : clients) {
            if(key == client.getAgentKey()) {
                return client;
            }
        }
        return null;
    }
}