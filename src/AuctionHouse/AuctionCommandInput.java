package AuctionHouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A simple class created to handle console input without blocking a thread
 */
public class AuctionCommandInput implements Runnable {

    private BufferedReader reader;
    private boolean active = true;
    private AuctionHouse ahouse;

    public AuctionCommandInput(AuctionHouse house) {
        reader = new BufferedReader(new InputStreamReader(System.in));
        this.ahouse = house;
    }

    @Override
    public void run() {
        while(active) {
            String command = null;
            try {
                command = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Process console input
            String[] params = command.split(";");
            if(command == null) return;
            switch(command.toUpperCase()) {
                case "EXIT":
                    System.out.println("Shutting Down...");
                    shutDown();
                    break;
                case "CONNECTTOBANK":
                    System.out.println("Connecting to bank...");
                    boolean connectSuccess = ahouse.connectToBank();
                    break;
                default:
                    System.out.println("Not sure what you're trying to do here, bud");
                    break;
            }
        }
    }

    /**
     * Stop looping and waiting for messages
     */
    public void shutDown() {
        active = false;
    }

    /**
     * Is this thing running?
     * @return whether or not this is running
     */
    public boolean getActive() {
        return active;
    }
}
