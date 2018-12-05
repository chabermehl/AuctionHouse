package Agent;

import AuctionHouse.AuctionHouse;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

public class Agent{
    private static Map<String,NotificationListener> notificationListenerMap = new HashMap<>();
    private static Map<String,AuctionHouseProxy> auctionHouseProxyMap = new HashMap<>();
    private static LinkedList<String> itemIDs = new LinkedList<>();
    private static LinkedList<String> auctionHouseIps;
    private static BankProxy bankProxy;
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
        @Override
        public void run(){
            String notification = "";
            while(!notification.equals("terminate")) {
                notification = auctionHouseProxy.takeNotification();
                if(notification.contains("win")){
                    System.out.println("You won the bid on item "+id.split(".")[1]+
                            " in "+id.split(".")[0]+" for "+amount+" dollars");
                    boolean returnVal = bankProxy.transferMoney(id.split(".")[0],amount);
                    if (returnVal) {
                        System.out.println("Money was successfully transferred");
                    }
                    else {
                        System.out.println("There is an error in money transfer");
                    }
                }
                else if(notification.contains("pass")){
                    System.out.println("You lost the bid");
                }
                if(notification.contains("win") || notification.contains("pass")){
                    //auctionHouseProxyMap.remove(id.split(".")[0]);
                    break;
                }
            }
        }
    }
    /**
     *
     * @param args name amount BankIp BankPort
     */
    public static void main(String []args){
        bankProxy = new BankProxy(args[2],args[3]);
        int acountnum = bankProxy.createAcount(args[0],Integer.getInteger(args[1]),"","",false);
        int key = bankProxy.getKey(acountnum);
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        LinkedList<LinkedList<String>> auctionHouses = new LinkedList<>();
        while (!input.equals("log out")){
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
                    System.out.print(num+". ");
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
                    notificationListenerMap.put(ip,new NotificationListener(auctionHouseProxyMap.get(ip),ip+"."+itemId,amount));
                }
                else if(!notificationListenerMap.keySet().contains((ip))){
                    notificationListenerMap.put(ip,new NotificationListener(auctionHouseProxyMap.get(ip),ip+"."+itemId,amount));
                }
                auctionHouseProxyMap.get(ip).bid(itemId,amount);
            }
            input=sc.nextLine();
        }
        bankProxy.closeAcount(acountnum);
    }
}
