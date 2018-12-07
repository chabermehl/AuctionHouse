package AuctionHouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A simple class created to handle console input without blocking a thread
 */
public class AuctionCommandInput implements Runnable {

    private BufferedReader reader;
    boolean active = true;

    public AuctionCommandInput() {
        reader = new BufferedReader(new InputStreamReader(System.in));
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

            if(command == null) return;
            switch(command.toUpperCase()) {
                case "EXIT":
                    System.out.println("Shutting Down...");
                    shutDown();
                    break;
                case "CONNECTTOBANK":
                    System.out.println("Fine.");
                    break;
                default:
                    System.out.println("Not sure what you're trying to do here, bud");
                    break;
            }
        }
    }

    public void shutDown() {
        active = false;
    }

    public boolean getActive() {
        return active;
    }
}
