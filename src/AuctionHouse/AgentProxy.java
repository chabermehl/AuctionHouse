package AuctionHouse;

import Agent.BankProxy;
import Bank.Message;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AgentProxy extends Thread{
    private class MessageSender extends Thread{
        PrintWriter out;
        AgentProxy agentProxy;
        public MessageSender(PrintWriter out,AgentProxy agentProxy){
            this.out = out;
            this.agentProxy = agentProxy;
            start();
        }
        @Override
        public void run(){
            while(true) {
                Message message = agentProxy.getNextMessage();
                if(message.dataInfo.equals("terminate")){
                    break;
                }
                out.println(message.data);
            }
        }
    }
    private BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
    private PrintWriter out;
    private BufferedReader in;
    private AuctionHouse auctionHouse;
    private BankProxy bankProxy;
    private boolean terminated = false;
    public AgentProxy(Socket socket, AuctionHouse auctionHouse, BankProxy bankProxy){
        this.auctionHouse = auctionHouse;
        this.bankProxy=bankProxy;
        try {
            out = new PrintWriter(socket.getOutputStream (), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e){
            System.out.println("IOException in AgentProxy");
        }
        new MessageSender(out,this);
        start();
    }
    public void terminate(){
        if(!terminated) {
            try {
                terminated = true;
                messageQueue.put(new Message("","#;terminate"));
                messageQueue.put(new Message("terminate", ""));
            } catch (InterruptedException e) {
                System.out.println("exception in terminate in AgentProxy");
            }
        }
    }
    public void sendNotification(String notification,String itemId,String key,double amount){
        try {
            bankProxy.releaseLock(key,amount);
            messageQueue.put(new Message("", "#;"+notification+";" + itemId));
        }
        catch (InterruptedException e){
            System.out.println("InterruptedException in sendNotification");
        }
    }
    public Message getNextMessage(){
        Message message = null;
        try {
            message = messageQueue.take();
        }
        catch (InterruptedException e){
            System.out.println("InterruptedException in getNextMessage");
        }
        return message;
    }
    @Override
    public void run(){
        String message = "";
        while (true) {
            try {
                message = in.readLine();
                if(message==null){
                    in.close();
                    out.close();
                    terminated=true;
                    messageQueue.put(new Message("terminate", ""));
                    break;
                }
            } catch (Exception e) {
                System.out.println("Exception in agentProxy");
                break;
            }
            System.out.println("Message from agent: "+message);
            String returnVal = "";
            if (message.split(";")[0].equals("getItems")) {
                returnVal = getItems();
            } else if (message.split(";")[0].equals("bid")) {
                String key = message.split(";")[1];
                String itemId = message.split(";")[2];
                double amount = Double.parseDouble(message.split(";")[3]);
                boolean state = auctionHouse.isBidValid(key, itemId, amount, this);
                if (state) {
                    returnVal = "accept";
                } else {
                    returnVal = "reject";
                }
            } else if(message.split(";")[0].equals("receipt")){
                auctionHouse.updateReceiptNum();
            }

            try {
                messageQueue.put(new Message("", returnVal));
            } catch (InterruptedException e) {
                System.out.println("InterruptedException in run AgentProxy");
            }
        }
    }
    private String getItems(){
        if(auctionHouse.getTerminationState()){
            return "";
        }
        Map<String,String> map= auctionHouse.getItems();
        String returnVal = "";
        boolean first = true;
        for(String s: map.keySet()){
            double amount = auctionHouse.getCurrentBidAmount(s);
            if(amount!=-1){
                if(!first){
                    returnVal+=";";
                }
                first = false;
                returnVal += s+","+map.get(s).split(";")[0]+","+map.get(s).split(";")[1]+","+amount;
            }
        }
        return returnVal;
    }
}
