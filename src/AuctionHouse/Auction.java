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
    private static long auctionCount = 1;
    private long auctionID;


    public Auction(String itemName, String description, double minimumBid, AuctionHouse ahouse) {
        this.itemName = itemName;
        this.description = description;
        this.minimumBid = minimumBid;
        this.ahouse = ahouse;

        auctionID = auctionCount++;
    }

    /**
     * Updates the current bidder for this auction and resets the timer
     * @param bidderKey key of newest bidder
     * @param amount amount to set the new bid to
     */
    public synchronized void setBid(int bidderKey, double amount) {
        // Start the timer if bidding has begun
        beginTime = Instant.now();
        hasBeenBiddedOn = true;
        System.out.println("Timer started for auction " + itemName);

        currentBidderKey = bidderKey;
        currentBid = amount;
    }

    /**
     * Runs the auction loop. Timer counts down after first bid and
     * terminates afterward.
     */
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

    /**
     * Gets the current bid on this auction
     * @return the current bid
     */
    public double getCurrentBid () {return currentBid;}

    /**
     * Gets the name of the item being auctioned
     * @return name of the item being auctioned
     */
    public String getItemName () {return itemName;}

    /**
     * Gets the secret key of the current bidder
     * @return current bidder's key
     */
    public int getBidderKey() {return currentBidderKey;}

    /**
     * Gets the minimum bid required for acceptance.
     * @return minimum bid for the auction
     */
    public double getMinimumBid() {return minimumBid;}

    /**
     * Get the description of the item being auctioned
     * @return
     */
    public String getDescription() {return description;}

    /**
     * Get the auctionID
     * @return auction ID.
     */
    public long getAuctionID() {return auctionID;}

    /**
     * Returns whether or not an initial bid has been placed on the auction
     * @return whether or not that auction
     */
    public boolean hasBeenBiddenOn() {return hasBeenBiddedOn;}
}
