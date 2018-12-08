package AuctionHouse;

import Bank.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;

/**
 * Auction House server class used as a sort of proxy, I guess.
 * Forwards messages to the actual auction house
 */
public class AuctionHouseServer extends Thread {
    private int port;
    public AuctionHouse auctionHouse;
    private ServerSocket serverSocket;
    private LinkedList<AuctionClient> clients = new LinkedList<>();

    public AuctionHouseServer(int port, AuctionHouse ahouse) {
        this.port = port; this.auctionHouse = ahouse;
    }
    public void shutdown() {
        for(AuctionClient client : clients) {
            System.out.println("Shutting down client " + client.toString());
        }
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
            //e.printStackTrace();
        }

        // Try and accept new client connections
        while (!serverSocket.isClosed()) {
            try {
                Socket agentSocket = serverSocket.accept();
                System.out.println(agentSocket.getRemoteSocketAddress() + "connect to Auction House Server");
                AuctionClient client = new AuctionClient(agentSocket, this);
                clients.add(client);
                new Thread(client).start();
            } catch (IOException e) {
                System.out.println("Auction House Server Socket Closed");
            }
        }
        System.out.println("Server done running");
    }

    public AuctionClient getClientByKey(int key) {
        for(AuctionClient client : clients) {
            System.out.println(client.getAgentKey());
            if(key == client.getAgentKey()) {
                return client;
            }
        }
        return null;
    }

    public int getPort() {return port;}
}