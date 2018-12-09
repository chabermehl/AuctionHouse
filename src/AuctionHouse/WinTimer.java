package AuctionHouse;

public class WinTimer extends Thread{
    private AgentProxy agentProxy;
    private String itemId;
    private AuctionHouse auctionHouse;
    private String key;
    private double amount;
    public WinTimer(AgentProxy agentProxy,String itemId,AuctionHouse auctionHouse,String key,double amount){
        this.agentProxy = agentProxy;
        this.itemId = itemId;
        this.auctionHouse=auctionHouse;
        this.key=key;
        this.amount=amount;
        start();
    }
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
    public void pass(){
        interrupt();
        agentProxy.sendNotification("pass",itemId,key,amount);
    }
}
