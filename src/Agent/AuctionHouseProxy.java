package Agent;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Farhang Rouhi
 * This class is the auction house proxy. This proxy is a client. It connects to the bank
 * and make functions calls. I use simple texts and send and receive the Strings for this connection.
 * It also receives notifications regarding the status of bids.
 */
public class AuctionHouseProxy extends Thread{
    private boolean terminated = false;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private BlockingQueue<String> notificationQueue = new LinkedBlockingQueue<>();

    /**
     * This is the constructor, and creates this proxy via the ip and port of the bank.
     * @param ip of the bank
     * @param port of the bank
     */
    public AuctionHouseProxy(String ip,String port){
        try {
            socket = new Socket(ip, Integer.parseInt(port));
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e){
            System.out.println("Error in AuctionHouseProxy");
        }
        start();
    }

    /**
     * returns the termination status
     * @return
     */
    public boolean isTerminated(){
        return terminated;
    }
    /**
     * This function terminates the connection and all dependencies.
     */
    public void terminate(){
        try {
            notificationQueue.put("terminate");
        }
        catch (InterruptedException e){
            System.out.println("Error in closing bank proxy");
        }
        try {
            socket.close();
        }
        catch (IOException e){
            System.out.println("Error in closing bank proxy");
        }
    }

    /**
     * This thread run function listens for messages from the auction house.
     * If the message is a notification it puts it in notificationQueue, otherwise
     * the message must be a return value. So, it puts it in the queue.
     */
    @Override
    public void run(){
        while(true) {
            String message = "";
            try {
                message = in.readLine();
            } catch (IOException e) {
                break;
            }
            if (message.split(";")[0].equals("#")) {
                if(message.contains("terminate")){
                    terminated = true;
                    terminate();
                }
                try {
                    notificationQueue.put(message);
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException in AuctionHouseProxy");
                }
            } else {
                try {
                    queue.put(message);
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException in AuctionHouseProxy");
                }
            }
        }
    }

    /**
     * this function takes notification for a certain item.
     * If the notification is not related to that item it puts it back in the queue.
     * @param itemId this is the id of the item that we are listening for its notifications.
     * @return
     */
    synchronized public String takeNotification(String itemId){
        String notification = "";
        try {
            notification = notificationQueue.take();
        }
        catch (InterruptedException e){
            System.out.println("InterruptedException exception in auction house proxy");
            return "error";
        }
        if(notification.contains(itemId)){
            return notification;
        }
        try {
            notificationQueue.put(notification);
        } catch (InterruptedException e) {
            System.out.println("InterruptedException exception in auction house proxy");
        }
        return "";
    }

    /**
     * This function sends messages to the auction house and then receives the return value,
     * by getting it from the queue.
     * @param data
     * @return
     */
    private String communicate(String data){
        out.println(data);
        String returnedVal = "";
        try {
            returnedVal = queue.take();
        }
        catch (InterruptedException e){
            System.out.println("InterruptedException exception in bank proxy");
        }
        return returnedVal;
    }

    /**
     * this function allows me to bid on items via itemId and amount.
     * @param itemId
     * @param amount
     * @return
     */
    public boolean bid(int key,String itemId,double amount){
        String message = "bid;"+key+";"+itemId+";"+amount;
        String returnVal = communicate(message);
        if(returnVal.contains("reject")){
            return false;
        }
        return true;
    }
    /**
     * This functions gets and returns the items from the auction house.
     * @return
     */
    public LinkedList<LinkedList<String>> getItems(){
        System.out.println("here");
        String message = "getItems";
        String returnVal = communicate(message);
        String [] pair = returnVal.split(";");
        LinkedList<LinkedList<String>> list = new LinkedList<>();
        for(String s: pair){
            String [] str= s.split(",");
            LinkedList<String> subList = new LinkedList<>(Arrays.asList(str));
            list.add(subList);
        }
        return list;
    }

    /**
     * This function sendsReceipt of transaction to the auction house.
     */
    public void sendReceipt(){
        String message = "receipt";
        String returnVal = communicate(message);
    }
}
