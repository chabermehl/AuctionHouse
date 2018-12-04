package Agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AuctionHouseProxy extends Thread{
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private BlockingQueue<String> notificationQueue = new LinkedBlockingQueue<>();
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
    @Override
    public void run(){
        String message="";
        try {
            message = in.readLine();
        }
        catch (IOException e){
            System.out.println("IO Exception in AuctionHouseProxy");
        }
        if (message.split(";")[0].equals("#")){
            try {
                notificationQueue.put(message);
            }
            catch (InterruptedException e){
                System.out.println("InterruptedException in AuctionHouseProxy");
            }
        }
        else{
            try {
                queue.put(message);
            }
            catch (InterruptedException e){
                System.out.println("InterruptedException in AuctionHouseProxy");
            }
        }
    }
    public String takeNotification(){
        try {
            return notificationQueue.take();
        }
        catch (InterruptedException e){
            System.out.println("InterruptedException exception in bank proxy");
            return "error";
        }
    }
    private String communicate(String message){
        out.println(message);
        String returnedVal = "";
        try {
            returnedVal = queue.take();
        }
        catch (InterruptedException e){
            System.out.println("InterruptedException exception in bank proxy");
        }
        return returnedVal;
    }
    public String bid(String itemId,double amount){
        String message = "bid;"+itemId+";"+amount;
        return communicate(message);
    }

    public LinkedList<LinkedList<String>> getItems(){
        return null;
    }
}
