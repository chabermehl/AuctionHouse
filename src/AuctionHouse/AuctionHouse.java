package AuctionHouse;

import Agent.BankProxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class handles the general logic related to the auction house.
 * It is also in charge of the main book keeping.
 */
public class AuctionHouse {
    /**
     * This private class terminated the auction house smoothly.
     */
    private class Terminator extends Thread{
        /**
         * This starts the thread.
         */
        public Terminator(){
            start();
        }

        /**
         * This is the run function of the thread.
         * It handles the logic related to termination.
         * In termination, we wait for all bids to get closed, and
         * we also wait to receive all of the transferred monies.
         */
        @Override
        public void run(){
            Scanner sc = new Scanner(System.in);
            while (true){
                String input = sc.nextLine();
                if(input.equals("exit") || input.equals("terminate") || input.equals("log out")){
                    if(soldNum==winTimerMap.size()){
                        terminate();
                    }
                    else{
                        terminate = true;
                    }
                    System.out.println("Termination in progress");
                    break;
                }
            }
        }
    }
    private ServerSocket serverSocket;
    private boolean terminate = false;
    private int receivedReceipts = 0;
    private int soldNum = 0;
    private BankProxy bankProxy;
    private Map<String,String> map;
    private Map<String,Double> currentBidMap;
    private Map<String,WinTimer> winTimerMap;

    /**
     * This is my auction house instructor.
     * It initialises every thing, but also, it creates some items.
     * @param bankProxy
     * @param serverSocket
     */
    public AuctionHouse(BankProxy bankProxy,ServerSocket serverSocket){
        this.serverSocket=serverSocket;
        new Terminator();
        this.bankProxy = bankProxy;
        map = new LinkedHashMap<>();
        currentBidMap = new LinkedHashMap<>();
        winTimerMap = new LinkedHashMap<>();
        map.put("French Fries","They're a little cold but totally fine;1.00");
        map.put("Avocado Spread","Wow avocado is nice;50.00");
        map.put("DnD Dice","I wish I had more of these in real life;4.00");
        map.put("Brand new car","The transmission doesn't actually work;500000");
        map.put("Bag of groceries","I bought these and I want to sell them for more;20.00");
        map.put("1 Dollar","This one's really special trust me;10.00");
        map.put("My Friendship","I'll give you advice and stuff;0.50");
        for(String id:map.keySet()){
            currentBidMap.put(id,0.0);
        }

    }

    /**
     * This terminates ONLY the auction house threads.
     */
    public void terminate(){
        try {
            serverSocket.close();
        }
        catch (IOException e){
            System.out.println("IOException in terminate in Auction house");
        }
    }

    /**
     * This returns the termination status.
     * @return
     */
    public boolean getTerminationState(){
        return terminate;
    }

    /**
     * This returns the map of all items.
     * @return
     */
    public Map<String,String> getItems(){
        return map;
    }

    /**
     * This one returns current bid amount for an item.
     * @param itemId
     * @return
     */
    public double getCurrentBidAmount(String itemId){
        return currentBidMap.get(itemId);
    }

    /**
     * This one is called whenever an item is sold for bok keeping.
     * @param itemId
     */
    public synchronized void itemSold(String itemId){
        currentBidMap.put(itemId,-1.0);
        soldNum++;
        if(terminate && soldNum==winTimerMap.size() && soldNum==receivedReceipts){
            terminate();
        }
    }

    /**
     * This one is called whenever a receipt is received for bok keeping.
     */
    public synchronized void updateReceiptNum(){
        receivedReceipts++;
        if(terminate && soldNum==winTimerMap.size() && soldNum==receivedReceipts){
            terminate();
        }
    }

    /**
     * This function determines if the bid is valid ro not. If it is, it wil process the bid.
     * @param key
     * @param itemId
     * @param amount
     * @param agentProxy
     * @return
     */
    public synchronized boolean isBidValid(String key,String itemId,double amount,AgentProxy agentProxy){
        boolean status = bankProxy.lockBalance(key,amount);
        if(status && currentBidMap.get(itemId)!=-1.0 && !terminate &&
                amount>currentBidMap.get(itemId) && amount>= Double.parseDouble(map.get(itemId).split(";")[1])){
            if(currentBidMap.get(itemId)>0){
                winTimerMap.get(itemId).pass();
                //winTimerMap.remove(itemId);
            }
            currentBidMap.put(itemId,amount);
            winTimerMap.put(itemId,new WinTimer(agentProxy,itemId,this,key,amount));
            return true;
        }
        bankProxy.releaseLock(key,amount);
        return false;
    }
}
