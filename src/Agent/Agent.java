package Agent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

/**
 * This is the agent class and includes all of the logic associated with the agent.
 */
public class Agent{
    private static Map<String,NotificationListener> notificationListenerMap = new HashMap<>();
    private static Map<String,AuctionHouseProxy> auctionHouseProxyMap = new HashMap<>();
    private static LinkedList<String> itemIDs = new LinkedList<>();
    private static LinkedList<String> auctionHouseIps;
    private static BankProxy bankProxy;

    /**
     * This class listens for notifications from the bank regarding the bids.
     */
    private static class NotificationListener extends Thread{
        private double amount;
        private String id;
        private AuctionHouseProxy auctionHouseProxy;
        public NotificationListener(AuctionHouseProxy auctionHouseProxy,String id,double amount){
            this.auctionHouseProxy = auctionHouseProxy;
            this.id = id;
            this.amount = amount;
            start();
        }

        /**
         * This thread function waits for new status updates regarding bids,
         * and takes appropriate action.
         */
        @Override
        public void run(){
            String notification = "";
            while(!notification.equals("terminate")) {
                if(notification.contains("win")){
                    System.out.println("You won the bid on item "+id.split(".")[1]+
                            " in "+id.split(".")[0]+" for "+amount+" dollars");
                    bankProxy.transferMoney(id.split(".")[0],amount);
                    System.out.println("Money was successfully transferred");
                }
                else if(notification.contains("pass")){
                    System.out.println("You lost the bid");
                }
                if(notification.contains("win") || notification.contains("pass")){
                    //auctionHouseProxyMap.remove(id.split(".")[0]);
                    break;
                }
                notification = auctionHouseProxy.takeNotification(id.split(".")[1]);
            }
        }
    }
    /**
     * This is my main function which has all of the logic for my agent. It simply waits for
     * user input. User can use the following commands:
     * log out: it logs out and terminates the program.
     * get auction houses: it gets and prints the auction houses.
     * get auctions from "auction house number": this one gets auctions from the selected auction house.
     * Note that the number is the number printed when calling get auction houses.
     * bid on "itemId" for "amount" in "auctionHouse number": This one allows the user to bid on a certain item.
     * Note that the itemId will be printed via the previous command. The amount is the amount of bid. and the
     * auction house number is printed when we print auction houses.
     * @param args name amount BankIp BankPort
     */
    public static void main(String []args){
        bankProxy = new BankProxy(args[2],args[3]);
        String returnedCreatAccount = bankProxy.createAcount(args[0],Integer.parseInt(args[1]),"","");
        System.out.println(returnedCreatAccount);
        int acountnum = Integer.parseInt(returnedCreatAccount.split("Account Number: ")[1].split("\n")[0]);
        int key = Integer.parseInt(returnedCreatAccount.split("Bid Key: ")[1]);
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        LinkedList<LinkedList<String>> auctionHouses = new LinkedList<>();
        while (!input.equals("log out") || notificationListenerMap.size()>0){
            if(input.equals("log out")){
                System.out.println("You have some bids in progress, so please wait and then exit.");
            }
            if(input.equals("get auction houses")){
                auctionHouses =  bankProxy.getAuctionHouses();
                System.out.println("ID: name");
                int num=0;
                for(LinkedList list: auctionHouses){
                    System.out.println(num+": "+list.get(0));
                    num++;
                }
            }
            else if(input.contains("get auctions from")){
                String [] str = input.split("from ");
                int id = 0;
                String ip;
                String port;
                try {
                    id = Integer.parseInt(str[1]);
                    ip = auctionHouses.get(id).get(1);
                    port = auctionHouses.get(id).get(2);
                }
                catch (Exception e){
                    System.out.println("ERROR: incorrect auction house ID, try again");
                    input=sc.nextLine();
                    continue;
                }
                if(!auctionHouseProxyMap.keySet().contains(ip)) {
                    auctionHouseProxyMap.put(ip,new AuctionHouseProxy(ip, port));
                }
                LinkedList<LinkedList<String>> items =  auctionHouseProxyMap.get(ip).getItems();
                int num = 1;
                for (LinkedList<String> list: items){
                    System.out.println("num. houseId, itemId, description, minimumBid, currentBid");
                    System.out.print(num+". "+id+", ");
                    for(String s: list){
                        System.out.print(s+", ");
                    }
                    System.out.println();
                    num++;
                }
                auctionHouseProxyMap.remove(ip).terminate();
            }
            // bid on itemId for amount in auctionhouse
            else if(input.contains("bid on")){
                String [] str = input.split("on ");
                String [] str2 = input.split("for ");
                String [] str3 = input.split("in ");
                String itemId = "";
                double amount = 0;
                String auctionHouse = "";
                String ip;
                String port;
                try {
                    itemId = str[1].split(" ")[0];
                    amount = Double.parseDouble(str2[1].split(" ")[0]);
                    auctionHouse = str3[1];
                    ip = auctionHouses.get(Integer.parseInt(auctionHouse)).get(1);
                    port = auctionHouses.get(Integer.parseInt(auctionHouse)).get(2);
                }
                catch (Exception e){
                    System.out.println("ERROR: incorrect bidding format, try again");
                    input=sc.nextLine();
                    continue;
                }
                if(!auctionHouseProxyMap.keySet().contains(ip)) {
                    auctionHouseProxyMap.put(ip,new AuctionHouseProxy(ip, port));
                }
                boolean status = auctionHouseProxyMap.get(ip).bid(key,itemId,amount);
                if (!status){
                    System.out.println("Bid was rejected");
                    input=sc.nextLine();
                    continue;
                }
                else{
                    System.out.println("Bid was accepted");
                }
                notificationListenerMap.put(ip,new NotificationListener(auctionHouseProxyMap.get(ip),ip+"."+itemId,amount));
            }
            input=sc.nextLine();
        }
        for(AuctionHouseProxy ap: auctionHouseProxyMap.values()){
            ap.terminate();
        }
        bankProxy.closeAccount(acountnum);
        bankProxy.terminate();
    }
}
