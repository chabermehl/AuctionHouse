package AuctionHouse;

import java.util.Map;

public class AuctionHouse {
    public static void main(String[]args){
        // make an instance of proxies
        // make an account
        // make a list of auctioned items


        // t1 = record the time
        // make a private class that has a thread which loop via a constant time and auction a new thing and check for the time
        // loop does things
        // wait for a command to terminate
    }

    public synchronized void bid(int key,String name,double amount){

    }
    public Map<String, Double> getItems(){
        return null;
    }
    public boolean freezAmount(int key,double amount){
        return false;
    }
    public void acceptBid(int key,String item){
        // accept the bid from agent proxy
        // remove funds from acount
    }
    public void rejectBid(int key,String item){
        // reject the bid from agent proxy
        // unblock the money from bank proxy
    }
}
