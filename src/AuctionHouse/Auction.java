package AuctionHouse;

import java.time.Duration;
import java.time.Instant;

/**
 * Should only be used by the auction house
 * stores all data related to an auction
 * Might be able to just make this a private class
 */
public class Auction extends Thread {

    private String itemName; // Name of item being sold
    private String description; // Description of item
    private double currentBid; // Current highest bid
    private int currentBidderKey = 0; // Key of the highest bidder
    private double minimumBid; // Minimum bid to get the auction rolling
    private volatile boolean hasBeenBiddedOn = false;
    private Instant beginTime;

    public Auction(String itemName, String description, double minimumBid) {
        this.itemName = itemName;
        this.description = description;
        this.minimumBid = minimumBid;
    }

    public synchronized void setBid(int bidderKey, double amount) {
        // Start the timer if bidding has begun
        if(currentBidderKey == 0) {
            beginTime = Instant.now();
            hasBeenBiddedOn = true;
            System.out.println("Timer started for auction " + itemName);
        }
        currentBidderKey = bidderKey;
        currentBid = amount;
    }

    @Override
    public void run() {
        Duration totalElapsed;
        while(true) {
            if(hasBeenBiddedOn) {
                totalElapsed = Duration.between(beginTime, Instant.now());
                if(totalElapsed.getSeconds() >= 30) { // 30 seconds
                    System.out.println("Auction for " + itemName + " has finished.");
                    return;
                }
            }
        }
    }

    public double getCurrentBid () {return currentBid;}
    public String getItemName () {return itemName;}
    public int getBidderKey() {return currentBidderKey;}
    public double getMinimumBid() {return minimumBid;}
    public String getDescription() {return description;}
}
