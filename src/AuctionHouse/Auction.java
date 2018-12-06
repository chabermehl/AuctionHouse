package AuctionHouse;

import java.time.Duration;
import java.time.Instant;

/**
 * Should only be used by the auction house
 * stores all data related to an auction
 * Runs on its own thread
 */
public class Auction extends Thread {

    private String itemName; // Name of item being sold
    private String description; // Description of item
    private double currentBid; // Current highest bid
    private int currentBidderKey = 0; // Key of the highest bidder
    private double minimumBid; // Minimum bid to get the auction rolling
    private volatile boolean hasBeenBiddedOn = false;
    private Instant beginTime;
    private AuctionHouse ahouse;

    // Create unique auction IDs.
    private static long auctionCount = 0;
    private long auctionID;


    public Auction(String itemName, String description, double minimumBid) {
        this.itemName = itemName;
        this.description = description;
        this.minimumBid = minimumBid;

        auctionID = auctionCount++;
    }

    public synchronized void setBid(int bidderKey, double amount) {
        // Start the timer if bidding has begun
        beginTime = Instant.now();
        hasBeenBiddedOn = true;
        System.out.println("Timer started for auction " + itemName);

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
                    ahouse.auctionDone(this);
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
