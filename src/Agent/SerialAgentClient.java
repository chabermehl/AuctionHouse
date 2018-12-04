package Agent;
import Bank.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SerialAgentClient implements Runnable {

    public BufferedReader inLine;
    public SerialAgent agent;
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
                        log("Connecting to the Bank");
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }






    private void log(String msg) {
        System.out.println(msg);
    }
}
