package Agent;

import java.io.*;
import java.net.Socket;

public class SerialAgentClient implements Runnable {

    public BufferedReader inLine;
    public SerialAgent agent;
    public CreateConnection bank;
    public CreateConnection auction;
    public boolean newConnection = true;

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

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void connectBank(String name, String address, int port) {
        bank = new CreateConnection(name, new Socket(), address, port);
        Thread t = new Thread(bank);
        t.start();
    }

    public void connectAuction(String name, String address, int port) {
        auction = new CreateConnection(name, new Socket(), address, port);
        Thread t = new Thread(auction);
        t.start();
    }

    public class MessageSender {

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
    }

    public class MessageReceiver {

        public Socket socket;
        public ObjectInputStream ois;

        public MessageReceiver(Socket socket) throws IOException {
            this.socket = socket;
            ois = new ObjectInputStream(socket.getInputStream());
        }
    }

    private void log(String msg) {
        System.out.println(msg);
    }
}
