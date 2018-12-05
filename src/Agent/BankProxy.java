package Agent;

import Bank.Message;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.*;

public class BankProxy {
    //private Socket socket;
    //private PrintWriter out;
    //private BufferedReader in;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    public BankProxy(String ip,String port){
        try {
            socket = new Socket(ip, Integer.parseInt(port));
            //out = new PrintWriter(socket.getOutputStream(), true);
            //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
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
        String message = "getKey;"+acountNum;
        String returnVal = communicate(message);
        return Integer.parseInt(returnVal);
    }
    private String communicate(String message){
        String dataInfo = "";
        String data = "";
        if(!message.contains(";")){
            dataInfo = message;
            data = "";
        }
        else{
            dataInfo = message.split(";")[0];
            data = message.split(dataInfo+";")[1];
        }
        try {
            out.writeObject(new Message(dataInfo, data));
        }catch (IOException e){
            System.out.println("There is an IO exception in BankProxy");
        }
        //out.println(message);
        String returnedVal = "";
        try {
            //returnedVal = in.readLine();
            Message m = (Message)in.readObject();
            returnedVal = m.data;
        }
        catch (IOException e){
            System.out.println("IO exception in bank proxy");
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException in bankproxy");
        }
        return returnedVal;
    }
    public boolean withdraw(int amount){
        String message = "withdraw;"+amount;
        String returnVal = communicate(message);
        return Boolean.getBoolean(returnVal);
    }
    public void deposit(int amount){
        String message = "deposit;"+amount;
        String returnVal = communicate(message);
    }
    public boolean lockBalance(int key,int amount){
        String message = "lockBalance;"+amount;
        String returnVal = communicate(message);
        return Boolean.getBoolean(returnVal);
    }
    public void releaseLock(int amount){
        String message = "releaseLock;"+amount;
        String returnVal = communicate(message);
    }
    public boolean transferMoney(String ip,double amount){
        String message = "transferMoney;"+ip+";"+amount;
        String returnVal = communicate(message);
        return Boolean.getBoolean(returnVal);
    }
    public void closeAcount(int acountNum){
        String message = "releaseLock;"+acountNum;
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
        String message = "lockBalance;"+id;
        return communicate(message);
    }
}
