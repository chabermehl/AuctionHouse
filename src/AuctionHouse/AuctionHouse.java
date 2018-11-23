package AuctionHouse;

import Agent.BankProxy;
import java.util.LinkedList;
import java.util.Map;

import static java.lang.Thread.sleep;

/**
 * The auction house has
 */
public class AuctionHouse {

    // All of the active auctions in this house.
    private LinkedList<Auction> currentAuctions = new LinkedList<>();

    // Bank proxy to open/close account
    private BankProxy bankProxy;

    // Info for connecting to bank
    private String bankIP;
    private int bankPort;

    // Use this for creating house ids
    private static int houseCount = 0;
    private int houseID;

    public static void main(String[] args)
    {
        AuctionHouse auctionHouse = new AuctionHouse("Insert bank ip here", 2224);
        auctionHouse.run();
    }

    public AuctionHouse(String bankIP, int bankPort)
    {
        this.bankIP = bankIP;
        this.bankPort = bankPort;
        houseCount++;
        houseID = houseCount;
    }

    public void run()
    {
        // Connect to proxy and make an account
        // connectToBank();
        // createBankAccount()

        // Make a list of auctioned items for testing
        addAuction(new Auction("Crab Cakes", "I made too many", 2.00));
        // t1 = record the time
        // make a private class that has a thread which loop via a constant time and auction a new thing and check for the time
        // loop does things


        // wait for a command to terminate
        closeBankAccount();
    }

    private void addAuction(Auction auction)
    {
        currentAuctions.add(auction);
        auction.start();
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        auction.setBid(348943899, 20);
    }

    public synchronized void bid(int key,String name,double amount) { }

    public void closeBankAccount() {}

    public Map<String, Double> getItems(){
        return null;
    }

    public boolean freezeAmount(int key,double amount){
        return false;
    }

    public void acceptBid(int key,String item){
        // accept the bid from agent proxy
        // remove funds from acount
    }

    // Bid is rejected if the incoming bid doesn't meet the minimum required,
    // or agent tries to bid with more money than they have
    public void rejectBid(int key,String item){
        // reject the bid from agent proxy
        // unblock the money from bank proxy
    }
}
