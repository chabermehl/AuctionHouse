package Agent;

import Bank.Message;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * @author Farhang Rouhi
 * This class is the bank proxy. This proxy is a client. It connects to the bank
 * and make functions calls. We use serialization and send and receive the object Message.
 */
public class BankProxy {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    /**
     * This is the constructor that creates my proxy via the bank ip and port.
     * @param ip
     * @param port
     */
    public BankProxy(String ip,String port){
        try {
            socket = new Socket(ip, Integer.parseInt(port));
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException e){
            System.out.println("Error in bank proxy");
        }
    }

    /**
     * This function closes the socket and terminates the class.
     */
    public void terminate(){
        try {
            socket.close();
        }
        catch (IOException e){
            System.out.println("Error in closing bank proxy");
        }
    }

    /**
     * This function creates a new account in the bank
     * @param name
     * @param initialBalance
     * @param ip
     * @param port
     * @return
     */
    public String createAcount(String name,int initialBalance,String ip,String port,String type){
        String message = "createAccount;"+name+";"+initialBalance+";"+type+";"+ip+";"+port;
        return communicate(message);
    }


    /**
     * This function sends a message to the bank and then receives the returned value.
     * @param data
     * @return
     */
    private String communicate(String data){
        String dataInfo = "";
        try {
            out.writeObject(new Message(dataInfo, data));
        }catch (IOException e){
            System.out.println("There is an IO exception in BankProxy");
        }
        String returnedVal = "";
        try {
            //returnedVal = in.readLine();
            Message m = (Message)in.readObject();
            //System.out.println(m.data);
            returnedVal = m.data;
        }
        catch (IOException e){
            System.out.println("IO exception in bank proxy");
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException in bankproxy");
        }
        return returnedVal;
    }

    /**
     * This function allows to withdraw from the account.
     * @param amount
     * @return
     */
    public boolean withdraw(int amount){
        String message = "withdraw;"+amount;
        String returnVal = communicate(message);
        return Boolean.getBoolean(returnVal);
    }

    /**
     * This function allows to deposit some money to the bank.
     * @param amount
     */
    public void deposit(int amount){
        String message = "deposit;"+amount;
        String returnVal = communicate(message);
    }

    /**
     * This function locks the balance in the bank.
     * @param key
     * @param amount
     * @return
     */
    public boolean lockBalance(String key,double amount){
        String message = "freezeFunds;"+key+";"+amount;
        String returnVal = communicate(message);
        if(returnVal.contains("not")){
            return false;
        }
        return true;
    }

    /**
     * This function releases the lock on a certain amount.
     * @param amount
     */
    public synchronized void releaseLock(String key,double amount){
        String message = "unfreezeFunds;"+key+";"+amount;
        String returnVal = communicate(message);
    }

    /**
     * This function transfers the frozen money to the auction house.
     * @param ip
     * @param amount
     * @return
     */
    public void transferMoney(String ip,double amount){
        String message = "Transfer;"+ip+";"+amount;
        String returnVal = communicate(message);
    }

    /**
     * This function closes the account.
     * @param acountNum
     */
    public void closeAccount(int acountNum){
        String message = "closeAccount;"+acountNum;
        String returnVal = communicate(message);
    }

    /**
     * This function gets auction houses and their ip's from the bank.
     * @return
     */
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

    /**
     * This function gets Auction house ip.
     * @param id
     * @return
     */
    public String getAuctionHouseIp(int id){
        String message = "getAuctionHouseIp;"+id;
        return communicate(message);
    }
}
