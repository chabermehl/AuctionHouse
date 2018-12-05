package Agent;

import Bank.Message;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AuctionHouseProxy extends Thread{
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    //private PrintWriter out;
    //private BufferedReader in;
    private BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private BlockingQueue<String> notificationQueue = new LinkedBlockingQueue<>();
    public AuctionHouseProxy(String ip,String port){
        try {
            socket = new Socket(ip, Integer.parseInt(port));
            //out = new PrintWriter(socket.getOutputStream(), true);
            //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
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
            //message = in.readLine();
            Message m = (Message)in.readObject();
            //message = m.dataInfo+";"+m.data;
            message = m.data;
        }
        catch (IOException e){
            System.out.println("IO Exception in AuctionHouseProxy");
        }
        catch (ClassNotFoundException e){
            System.out.println("ClassNotFoundException in AuctionHouseProxy");
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
        String dataInfo = "";
        String data = "";
        if(message.contains(";")){
            dataInfo = message.split(";")[0];
            data = message.split(dataInfo+";")[1];
        }
        else{
            dataInfo = message;
            data = "";
        }
        try {
            out.writeObject(new Message(dataInfo, data));
        }catch (IOException e){
            System.out.println("There is an IO exception");
        }
        //out.println(message);
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
