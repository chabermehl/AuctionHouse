/**
 * Created by chabermehl
 * 12/04/18
 * CS 351
 */
package Bank;

import java.io.*;
import java.net.Socket;

public class SerialAgentClient implements Runnable {

    public BufferedReader inLine;
    public SerialAgent agent;
    public CreateConnection bank;
    public CreateConnection auction;
    public boolean newConnection = true;
    public boolean hasWon = false;

    public static void main(String[] args) {
        SerialAgentClient agentClient = new SerialAgentClient();
        Thread t = new Thread(agentClient);
        t.start();
    }

    @Override
    public void run() {
        try {
            inLine = new BufferedReader(new InputStreamReader(System.in));
            log("Enter Your Name: ");
            String name = inLine.readLine();
            agent = new SerialAgent(name);
            while (true) {
                if (newConnection) {
                    newConnection = false;
                    log("Start A New Connection?\n" +
                            "Type \"Bank\" or \"Auction\" to connect.\n");
                    String newInLine = inLine.readLine();
                    if (newInLine.contains("Bank")) {
                        log("What host and port is the Bank on?\n" +
                                "<host> <port>\n");
                        newInLine = inLine.readLine();
                        String[] input = newInLine.split(" ");
                        log("Connecting to the Bank...");
                        connectBank(name, input[0], Integer.parseInt(input[1]));
                    } else if (newInLine.contains("Auction")) {
                        log("What host and port is the Auction on?\n" +
                                "<host> <port>\n");
                        newInLine = inLine.readLine();
                        String[] input = newInLine.split(" ");
                        log("Connecting to the Auction...");
                        connectAuction(name, input[0], Integer.parseInt(input[1]));
                    } else {
                        log("Incorrect Command, Please Try Again");
                        newConnection = true;
                    }
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * this class handles the making of connections
     * threaded so we can send objects across the connections
     */
    public class CreateConnection implements Runnable {
        public String name;
        public Socket socket;
        public String address;
        public int port;

        public MessageSender sender;
        public MessageReceiver receiver;

        BufferedReader inLine;

        public CreateConnection(String name, Socket socket, String address, int port) {
            this.name = name;
            this.socket = socket;
            this.address = address;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                inLine = new BufferedReader(new InputStreamReader(System.in));
                socket = new Socket(address, port);
                sender = new MessageSender(socket, name, inLine);
                receiver = new MessageReceiver(socket);

                Thread senderThread = new Thread(sender);
                Thread receiverThread = new Thread(receiver);

                senderThread.start();
                receiverThread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * connects the agent to the bank
     * @param name name of the agent
     * @param address host of the bank
     * @param port port the bank is on
     */
    public void connectBank(String name, String address, int port) {
        bank = new CreateConnection(name, new Socket(), address, port);
        Thread t = new Thread(bank);
        t.start();
    }

    /**
     * connects the agent to the auction house of their choosing
     * @param name name of the agent
     * @param address host of the auction
     * @param port port the auction is on
     */
    public void connectAuction(String name, String address, int port) {
        auction = new CreateConnection(name, new Socket(), address, port);
        Thread t = new Thread(auction);
        t.start();
    }

    /**
     * handles sending out messages to the bank or auction house
     */
    public class MessageSender implements Runnable {

        public ObjectOutputStream oos;
        public Socket socket;
        public String name;
        public BufferedReader inLine;

        public MessageSender(Socket socket, String name, BufferedReader inLine) throws IOException {
            this.socket = socket;
            this.name = name;
            this.inLine = inLine;
            oos = new ObjectOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {

        }
    }

    /**
     * handles the reception of the incoming objects on the socket
     */
    public class MessageReceiver implements Runnable {

        public Socket socket;
        public ObjectInputStream ois;

        public MessageReceiver(Socket socket) throws IOException {
            this.socket = socket;
            ois = new ObjectInputStream(socket.getInputStream());
        }

        @Override
        public void run() {

        }
    }

    /**
     * prints a message to the console
     * @param msg string value to printed
     */
    private void log(String msg) {
        System.out.println(msg);
    }
}
