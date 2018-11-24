package Agent;

import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

public class Agent{
    /**
     *
     * @param args name ip BankIp BankPort
     */
    public static void main(String []args){
        BankProxy bankProxy = new BankProxy();
        int acountnum = bankProxy.createAcount(Integer.parseInt(args[1]),args[2],false);
        int key = bankProxy.getKey(acountnum);
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        while (!input.equals("log out")){
            if(input.equals("get auction houses")){
                Map<Integer,String> auctionHouses =  bankProxy.getAuctionHouses();
                System.out.println("ID: name");
                for(Integer id: auctionHouses.keySet()){
                    System.out.println(id+": "+auctionHouses.get(id));
                }
            }
            else if(input.contains("get auctions from")){
                String [] str = input.split("from ");
                int id = 0;
                try {
                    id = Integer.parseInt(str[str.length - 1]);
                }
                catch (Exception e){
                    System.out.println("ERROR: incorrect auction house ID, try again");
                    input=sc.nextLine();
                    continue;
                }
                AuctionHouseProxy auctionHouseProxy = new AuctionHouseProxy(bankProxy.getAuctionHouseIp(id));
                LinkedList<LinkedList<String>> auctionHouses =  auctionHouseProxy.getItems();
                int num = 1;
                for (LinkedList<String> list: auctionHouses){
                    System.out.println("num. houseId, itemId, description, minimumBid, currentBid");
                    System.out.print(num+". ");
                    for(String s: list){
                        System.out.print(s+", ");
                    }
                    System.out.println();
                    num++;
                }
            }
            else if(input.equals(""))
            input=sc.nextLine();
        }
        bankProxy.closeAcount(acountnum);
    }
}
