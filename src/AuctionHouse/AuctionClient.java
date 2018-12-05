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
    public ObjectOutputStream oos;
    public ObjectInputStream ois;

    public AuctionClient(Socket socket, AuctionHouseServer server) {
        this.socket = socket;
        this.ahServer = server;
    }

    public void run()
    {
        while(!socket.isClosed())
        {
            // Process messages from agent
            Message message = readMessage();
            if(message != null) {
                processMessage(message);
            }
        }
    }

    private void processMessage(Message message)
    {
        String dataInfo = message.dataInfo;
    }

    private Message readMessage()
    {
        Object obj = null;
        try {
            obj = ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj != null ? (Message)obj : null;
    }

}
