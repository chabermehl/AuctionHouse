package AuctionHouse;

import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.LinkedList;

public class Server extends Thread {

    // Connections being made to this server
    private LinkedList<Socket> connections;
    private ServerSocket serverSocket;

    public Server(int port)
    {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        while(true) {
            try {
                serverSocket.accept();
            } catch (IOException e) {
                System.out.println("Could not accept incoming connection");
                e.printStackTrace();
            }
            System.out.println("Boi, someone connected");

        }
    }

    public static void main(String[] args)
    {
        Server server = new Server(2225);

        try {
            Socket socket = new Socket("127.0.0.1", 2225);
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.run();
    }
}
