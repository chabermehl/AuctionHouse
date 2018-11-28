package Agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.*;

public class BankProxy {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    public BankProxy(String ip,String port){
        try {
            socket = new Socket(ip, Integer.parseInt(port));
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e){
            System.out.println("Error in bank proxy");
        }
    }
    public void terminate(){
        try {
            socket.close();
        }
        catch (IOException e){
            System.out.println("Error in closing bank proxy");
        }
    }
    public int createAcount(String name,int initialBalance,String ip,String port,boolean auctionHouse){
        String message = "createAcount;"+name+";"+initialBalance+";"+ip+";"+port+";"+auctionHouse;
        String returnVal = communicate(message);
        return Integer.parseInt(returnVal);
    }
    public int getKey(int acountNum){
        String message = "getKey;"+Integer.toString(acountNum);
        String returnVal = communicate(message);
        return Integer.parseInt(returnVal);
    }
    private String communicate(String message){
        out.println(message);
        String returnedVal = "";
        try {
            returnedVal = in.readLine();
        }
        catch (IOException e){
            System.out.println("IO exception in bank proxy");
        }
        return returnedVal;
    }
    public boolean withdraw(int amount){
        String message = "withdraw;"+Integer.toString(amount);
        String returnVal = communicate(message);
        return Boolean.getBoolean(returnVal);
    }
    public void deposit(int amount){
        String message = "deposit;"+Integer.toString(amount);
        String returnVal = communicate(message);
    }
    public boolean lockBalance(int key,int amount){
        String message = "lockBalance;"+Integer.toString(amount);
        String returnVal = communicate(message);
        return Boolean.getBoolean(returnVal);
    }
    public void releaseLock(int amount){
        String message = "releaseLock;"+Integer.toString(amount);
        String returnVal = communicate(message);
    }
    public void closeAcount(int acountNum){
        String message = "releaseLock;"+Integer.toString(acountNum);
        String returnVal = communicate(message);
    }
    // format: name,ip,port;...
    public LinkedList<LinkedList<String>> getAuctionHouses(){
        String message = "getAuctionHouses";
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
    public String getAuctionHouseIp(int id){
        String message = "lockBalance;"+Integer.toString(id);
        return communicate(message);
    }
}
