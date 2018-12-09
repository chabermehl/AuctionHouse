package AuctionHouse;

/**
 * This class is instantiated when a valid bid is placed. It waits for 30 seconds and
 * if no better bids come in, it will win, otherwise it will lose.
 */
public class WinTimer extends Thread{
    private AgentProxy agentProxy;
    private String itemId;
    private AuctionHouse auctionHouse;
    private String key;
    private double amount;

    /**
     * This is the constructor. It receives some values and starts the thread.
     * @param agentProxy
     * @param itemId
     * @param auctionHouse
     * @param key
     * @param amount
     */
    public WinTimer(AgentProxy agentProxy,String itemId,AuctionHouse auctionHouse,String key,double amount){
        this.agentProxy = agentProxy;
        this.itemId = itemId;
        this.auctionHouse=auctionHouse;
        this.key=key;
        this.amount=amount;
        start();
    }

    /**
     * This is the run function of thi thread. Here, we wait for 30 seconds. If no better
     * bids come in, it will win. Otherwise it will pass.
     */
    @Override
    public void run(){
        try {
            sleep(30000);
            agentProxy.sendNotification("win",itemId,key,amount);
            auctionHouse.itemSold(itemId);
            // transfer money
            System.out.println("bid was won");
        }
        catch (InterruptedException e){
            System.out.println("bid was passed");
        }

    }

    /**
     * This function is called if a better bid comes in to cancel and pass this bid.
     */
    public void pass(){
        interrupt();
        agentProxy.sendNotification("pass",itemId,key,amount);
    }
}
