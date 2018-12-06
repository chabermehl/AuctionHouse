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
        while(!socket.isClosed()) {
            // Process messages from agent
            Message message = readMessage();
            if(message != null) {
                processMessage(message);
            }
        }
    }

    private void processMessage(Message message) {
        String dataInfo = message.dataInfo;

        // Attempt to bid. Send a response back to the agent with results
        if(dataInfo.equals("Bid")) {
            String[] params = message.dataInfo.split(",");
            if(params.length != 3)
            {
                // Not the correct parameter amount
                sendMessage(new Message("Incorrect Bid Input", ""));
                return;
            }

            int key = Integer.parseInt(params[0]);
            // Set our key if the agent hasn't bid yet.
            if(agentKey == -1) {agentKey = key;}
            boolean bidSuccess = ahServer.auctionHouse.bid(key, params[1], Double.parseDouble(params[2]));
            if(bidSuccess) {
                sendMessage(new Message("accept", ""));
            } else {
                sendMessage(new Message("reject", ""));
            }
        }
    }

    private Message readMessage() {
        Object obj = null;
        try {
            obj = ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj != null ? (Message)obj : null;
    }

    public void sendMessage(Message message) {
        try {
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getAgentKey() { return agentKey; }

}
