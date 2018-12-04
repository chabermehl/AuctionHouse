package Agent;
import Bank.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
            while(true) {
                if(newConnection) {
                    newConnection = false;
                    log("Start A New Connection?\n" +
                            "Type \"Bank\" or \"Auction\" to connect.\n");
                    String newInLine = inLine.readLine();
                    if(newInLine.contains("Bank")) {
                        log("What host and port is the Bank on?\n" +
                                "<host> <port>\n");
                        newInLine = inLine.readLine();
                        String[] input = newInLine.split(" ");
                        log("Connecting to the Bank...");
                        connectBank(name, input[0], Integer.parseInt(input[1]));
                    } else if(newInLine.contains("Auction")) {
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
        } catch(IOException e) {
            e.printStackTrace();
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

    private void log(String msg) {
        System.out.println(msg);
    }
}
