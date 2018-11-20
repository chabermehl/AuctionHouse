package AuctionHouse;

/**
 * Should only be used by the auction house
 * stores all data related to an auction
 * Might be able to just make this a private class
 */
public class Auction {
    private String itemName; // Name of item being sold
    private String description; // Description of item
    private double currentBid; // Current highest bid
    private int bidderKey; // Key of the highest bidder
    private double minimumBid; // Minimum bid to get the auction rolling

    public Auction(String itemName, String description, double minimumBid)
    {
        this.itemName = itemName;
        this.description = description;
        this.minimumBid = minimumBid;
    }

    public double getCurrentBid () {return currentBid;}
    public String getItemName () {return itemName;}
    public int getBidderKey() {return bidderKey;}
    public double getMinimumBid() {return minimumBid;}
}
